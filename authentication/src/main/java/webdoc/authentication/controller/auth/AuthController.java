package webdoc.authentication.controller.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webdoc.authentication.config.security.token.JwtAuthenticationToken;
import webdoc.authentication.domain.entity.user.request.CodePasswordRequest;
import webdoc.authentication.domain.entity.user.tutor.request.TutorCreateRequest;
import webdoc.authentication.domain.exceptions.EmailDuplicationException;
import webdoc.authentication.domain.response.ArrayResponse;
import webdoc.authentication.domain.response.CodeMessageResponse;
import webdoc.authentication.domain.entity.user.request.EmailRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.entity.user.tutee.request.TuteeCreateRequest;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import webdoc.authentication.utility.messageprovider.CommonMessageProvider;
import webdoc.authentication.utility.messageprovider.ResponseCodeProvider;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    @Value("${file.dir}")
    private String path;
    @Value("${server.add}")
    private String address;
    private final AuthService authService;
    private final UserRepository userRepository;

    // 로그아웃
    @PostMapping("/logout")
    public CodeMessageResponse logout(){
        Object user = SecurityContextHolder.getContext().getAuthentication();
        if(user == null || user instanceof AnonymousAuthenticationToken){
            return new CodeMessageResponse(AuthMessageProvider.LOGOUT_SUCCESS,200, ResponseCodeProvider.SUCCESS);
        }
        JwtAuthenticationToken token = (JwtAuthenticationToken) user;
        authService.logOut((User)token.getPrincipal());
        return new CodeMessageResponse(AuthMessageProvider.LOGOUT_SUCCESS,200,ResponseCodeProvider.SUCCESS);
    }

    // 비밀번호 찾기 이메일 입력
    @GetMapping("/password/code")
    public CodeMessageResponse passwordFindAuth(@RequestParam String email){
        try{
            authService.findPassword(email);
        }catch(NoSuchElementException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
    }

    // 비밀번호 찾기 인증코드 입력
    @PostMapping("/password/code")
    public CodeMessageResponse validateCode(@Validated @RequestBody  CodeRequest codeRequest, BindingResult bindingResult,HttpServletResponse res){
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }

        try{
            String token =
                    authService.validateCodeForPasswordFind(codeRequest.getEmail(), codeRequest.getCode());
            res.setHeader("token",token);

        }catch(TimeOutException | AuthenticationServiceException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public CodeMessageResponse changePassword(@Validated @RequestBody  CodePasswordRequest codePasswordRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }
        try{
            authService.changePassword(codePasswordRequest.getEmail(),codePasswordRequest.getCode(), codePasswordRequest.getPassword());
        }catch (IllegalStateException | NoSuchElementException e){
            throw e;
        }catch( Exception e){
            throw new RuntimeException(e);
        }

        return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
    }

    // 이메일 중복 검사
    @PostMapping("/join/duplication")
    public CodeMessageResponse emailDuplicatedCheck(HttpServletResponse res, @RequestBody @Validated EmailRequest dto, BindingResult bindingResult){
        String message = AuthMessageProvider.NOT_DUPLICATED_EMAIL;
        // 값 바인딩 실패
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }

        boolean isDuplicated = authService.isEmailDuplicated(dto.getEmail());

        if(isDuplicated){
            throw new EmailDuplicationException("해당 이메일을 가진 유저가 존재합니다");
        }
        return new CodeMessageResponse(message,200,ResponseCodeProvider.SUCCESS);

    }

    // 튜터 회원가입

    @PostMapping("/join/tutor")
    public CodeMessageResponse tutorJoin(
            HttpServletResponse res,
            @RequestBody @Validated TutorCreateRequest dto,
            BindingResult result
    ){
        String message = AuthMessageProvider.JOIN_SUCCESS;
        if (result.hasErrors()) {
            throw new IllegalArgumentException("바인딩 실패");
        }

        try {
            authService.createTutorUser(dto);
        }
        catch(EmailDuplicationException e){throw e;}
        catch(Exception e){throw new RuntimeException(e);}

        res.setStatus(201);
        return new CodeMessageResponse(message, 201,ResponseCodeProvider.SUCCESS);
    }

    // 튜티 회원가입
    @PostMapping("/join/tutee")
    public CodeMessageResponse tuteeJoin(
            HttpServletResponse res,
            @RequestBody @Validated TuteeCreateRequest dto,
            BindingResult result
    )  {
        String message = AuthMessageProvider.JOIN_SUCCESS;

        if (result.hasErrors()) {
            throw new IllegalArgumentException("바인딩 실패");
        }

        try {
            authService.createTuteeUser(dto);
        }
        catch(EmailDuplicationException e){throw e;}
        catch(Exception e){throw new RuntimeException(e);}


        return new CodeMessageResponse(message, 201, ResponseCodeProvider.SUCCESS);
    }


    // 튜티 인증
    @PostMapping("/validate/tutee")
    public CodeMessageResponse tuteeValidation(
            HttpServletResponse res,
            @RequestBody @Validated CodeRequest dto,
            BindingResult result
    ) {
        String message = AuthMessageProvider.VALIDATION_SUCCESS;

        if (result.hasErrors()) {
            throw new IllegalArgumentException("바인딩 실패");
        }

        try {
            authService.validateTutee(dto, LocalDateTime.now());
        }
        catch(TimeOutException | NoSuchElementException | AuthenticationServiceException e) {throw e;}
        catch(Exception e){ throw new RuntimeException(e);}

        res.setStatus(200);
        return new CodeMessageResponse(message, 200,ResponseCodeProvider.SUCCESS);
    }

    // 튜터 인증
    @PostMapping("/validate/tutor")
    public CodeMessageResponse tutorValidation(
            HttpServletResponse res,
            @RequestBody @Validated CodeRequest dto,
            BindingResult result
    ) {
        String message = AuthMessageProvider.VALIDATION_SUCCESS;

        if (result.hasErrors()) {
            throw new IllegalArgumentException("바인딩 실패");
        }

        try {
            authService.validateTutor(dto,LocalDateTime.now());
        }
        catch(TimeOutException | NoSuchElementException | AuthenticationServiceException e) {throw e;}
        catch(Exception e){ throw new RuntimeException(e);}


        res.setStatus(200);
        return new CodeMessageResponse(message, 200,ResponseCodeProvider.SUCCESS);
    }

    // 인증용 이미지 업로드

    // 이미지 등록하기
    @PostMapping("/images")
    public ArrayResponse<String> uploadImages(HttpServletResponse res, @RequestParam("files") List<MultipartFile> files) {
        if (files.size()>5){
            throw new IllegalArgumentException("사진은 5개 까지만 전송할 수 있습니다");
        }
        List<String> addresses = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("바인딩 실패");
            }

            String uuid = UUID.randomUUID().toString();
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

            String[] supportedExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "tiff", "tif", "ico"};

            // 현재 파일의 확장자가 지원하는 형식인지 확인
            boolean isSupported = false;
            for (String supportedExtension : supportedExtensions) {
                if (extension.equals(supportedExtension)) {
                    isSupported = true;
                    break;
                }
            }

            if (!isSupported) {
                throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
            }

            String fullPath = path + "/" + uuid + "." + extension;

            try {
                file.transferTo(new File(fullPath));
                String imageUrl = address + "/" + uuid + "." + extension;
                addresses.add(imageUrl);
            } catch (IOException e) {
                // 파일 전송 중 오류 처리
                throw new RuntimeException(e);
            }
        }

        return new ArrayResponse<>(addresses,200,addresses.size());
    }

}
