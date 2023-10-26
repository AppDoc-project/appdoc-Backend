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
import webdoc.authentication.domain.entity.user.doctor.request.DoctorCreateRequest;
import webdoc.authentication.domain.response.CodeMessageResponse;
import webdoc.authentication.domain.entity.user.request.EmailRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.entity.user.patient.request.PatientCreateRequest;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import webdoc.authentication.utility.messageprovider.CommonMessageProvider;

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
            return new CodeMessageResponse(AuthMessageProvider.LOGOUT_SUCCESS,200,null);
        }
        JwtAuthenticationToken token = (JwtAuthenticationToken) user;
        authService.logOut((User)token.getPrincipal());
        return new CodeMessageResponse(AuthMessageProvider.LOGOUT_SUCCESS,200,null);
    }

    // 이메일 중복 검사
    @PostMapping("/join/duplication")
    public CodeMessageResponse emailDuplicatedCheck(HttpServletResponse res, @RequestBody @Validated EmailRequest dto, BindingResult bindingResult){
        String message = AuthMessageProvider.NOT_DUPLICATED_EMAIL;
        // 값 바인딩 실패
        if (bindingResult.hasErrors()){
            log.info("type error={}", bindingResult.getAllErrors());
            message = AuthMessageProvider.BINDING_FAILURE;
            res.setStatus(400);
            return new CodeMessageResponse(message, 400 ,400);
        }
        boolean isDuplicated = authService.isEmailDuplicated(dto.getEmail());

        if(isDuplicated){
            res.setStatus(400);
            message = AuthMessageProvider.EMAIL_EXISTS;
            return  new CodeMessageResponse(message,400,401);
        }
        return new CodeMessageResponse(message,200,200);

    }

    // 의사 회원가입

    @PostMapping("/join/doctor")
    public CodeMessageResponse doctorJoin(
            HttpServletResponse res,
            @RequestBody @Validated DoctorCreateRequest dto,
            BindingResult result
    ){
        String message = AuthMessageProvider.JOIN_SUCCESS;
        if (result.hasErrors()) {
            log.info("type error={}", result.getAllErrors());
            message = AuthMessageProvider.BINDING_FAILURE;
            res.setStatus(400);
            return new CodeMessageResponse(message, 400,null);
        }

        try {
            authService.createDoctorUser(dto);
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw new IllegalStateException(e.getMessage());
            } else {
                throw new RuntimeException("서버 내부 에러가 발생하였습니다", e);
            }
        }
        res.setStatus(201);
        return new CodeMessageResponse(message, 201,null);
    }

    // 환자 회원가입
    @PostMapping("/join/patient")
    public CodeMessageResponse patientJoin(
            HttpServletResponse res,
            @RequestBody @Validated PatientCreateRequest dto,
            BindingResult result
    )  {
        String message = AuthMessageProvider.JOIN_SUCCESS;

        if (result.hasErrors()) {
            log.info("type error={}", result.getAllErrors());
            message = AuthMessageProvider.BINDING_FAILURE;
            res.setStatus(400);
            return new CodeMessageResponse(message, 400,null);
        }

        try {
            authService.createPatientUser(dto);
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw new IllegalStateException(e.getMessage());
            } else {
                throw new RuntimeException("서버 내부 에러가 발생하였습니다", e);
            }
        }

        return new CodeMessageResponse(message, 201, null);
    }


    // 환자 인증
    @PostMapping("/validate/patient")
    public CodeMessageResponse patientValidation(
            HttpServletResponse res,
            @RequestBody @Validated CodeRequest dto,
            BindingResult result
    ) {
        String message = AuthMessageProvider.VALIDATION_SUCCESS;

        if (result.hasErrors()) {
            log.info("type error={}", result.getAllErrors());
            message = AuthMessageProvider.BINDING_FAILURE;
            res.setStatus(400);
            // 값 바인딩 실패
            return new CodeMessageResponse(message, 400,403);
        }

        try {
            authService.validatePatient(dto, LocalDateTime.now());
        } catch (Exception e) {
            if (
                    e instanceof TimeOutException || e instanceof NoSuchElementException
                    || e instanceof AuthenticationServiceException
            ) {
                throw e;
            } else {
                throw new RuntimeException(e);
            }
        }

        res.setStatus(200);
        return new CodeMessageResponse(message, 200,null);
    }

    // 의사 인증
    @PostMapping("/validate/doctor")
    public CodeMessageResponse doctorValidation(
            HttpServletResponse res,
            @RequestBody @Validated CodeRequest dto,
            BindingResult result
    ) {
        String message = AuthMessageProvider.VALIDATION_SUCCESS;

        if (result.hasErrors()) {
            log.info("type error={}", result.getAllErrors());
            message = AuthMessageProvider.BINDING_FAILURE;
            res.setStatus(400);
            // 값 바인딩 실패
            return new CodeMessageResponse(message, 400,403);
        }

        try {
            authService.validateDoctor(dto,LocalDateTime.now());
        } catch (Exception e) {
            if (
                    e instanceof TimeOutException || e instanceof NoSuchElementException
                            || e instanceof AuthenticationServiceException
            ) {
                throw e;
            } else {
                throw new RuntimeException(e);
            }
        }

        res.setStatus(200);
        return new CodeMessageResponse(message, 200,null);
    }

    // 인증용 이미지 업로드

    @PostMapping("/image")
    public CodeMessageResponse authenticationImage(HttpServletResponse res,@RequestParam MultipartFile file) throws IOException {
        if(file.isEmpty()){
            res.setStatus(400);
            return new CodeMessageResponse(CommonMessageProvider.NO_UPLOAD,400,null);
        }
        String uuid = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if (!extension.equals("jpg") && !extension.equals("png") && !extension.equals("jpeg") && !extension.equals("pdf")) {
            return new CodeMessageResponse(CommonMessageProvider.NOT_IMAGE,400,null);
        }
        String fullPath = path + "/" + uuid + "."+ extension;

        file.transferTo(new File(fullPath));

        return new CodeMessageResponse(address+"/authentication_image"+"/"+uuid+"."+extension,200,null);

    }

}
