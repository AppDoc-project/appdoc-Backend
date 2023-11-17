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
import webdoc.authentication.domain.entity.user.tutor.request.TutorCreateRequest;
import webdoc.authentication.domain.exceptions.EmailDuplicationException;
import webdoc.authentication.domain.response.CodeMessageResponse;
import webdoc.authentication.domain.entity.user.request.EmailRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.entity.user.tutee.request.TuteeCreateRequest;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import webdoc.authentication.utility.messageprovider.ResponseCodeProvider;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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

    @PostMapping("/image")
    public CodeMessageResponse authenticationImage(HttpServletResponse res,@RequestParam MultipartFile file) throws IOException {
        if(file.isEmpty()){
            throw new IllegalArgumentException("바인딩 실패");
        }
        String uuid = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if ( !extension.equals("pdf")) {
            throw new IllegalArgumentException("바인딩 실패");
        }
        String fullPath = path + "/" + uuid + "."+ extension;

        file.transferTo(new File(fullPath));

        return new CodeMessageResponse(address+"/"+uuid+"."+extension,201,ResponseCodeProvider.SUCCESS);

    }

}
