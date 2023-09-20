package webdoc.authentication.controller.auth;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
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
import webdoc.authentication.domain.dto.response.CodeMessageResponse;
import webdoc.authentication.domain.dto.response.SubCodeMessageResponse;
import webdoc.authentication.domain.dto.user.DoctorDto;
import webdoc.authentication.domain.dto.user.EmailDto;
import webdoc.authentication.domain.dto.user.PatientCodeDto;
import webdoc.authentication.domain.dto.user.PatientDto;
import webdoc.authentication.domain.entity.user.Doctor;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import webdoc.authentication.utility.messageprovider.CommonMessageProvider;

import java.io.File;
import java.io.IOException;
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
            return new CodeMessageResponse(AuthMessageProvider.LOGOUT_SUCCESS,200);
        }
        JwtAuthenticationToken token = (JwtAuthenticationToken) user;
        authService.logOut((User)token.getPrincipal());
        return new CodeMessageResponse(AuthMessageProvider.LOGOUT_SUCCESS,200);
    }

    // 이메일 중복 검사
    @PostMapping("/join/duplication")
    public SubCodeMessageResponse emailDuplicatedCheck(HttpServletRequest req, HttpServletResponse res,@RequestBody @Validated EmailDto dto,BindingResult bindingResult){
        int code = 200;
        String message = AuthMessageProvider.NOT_DUPLICATED_EMAIL;
        if (bindingResult.hasErrors()){
            log.info("type error={}", bindingResult.getAllErrors());
            code = 400;
            message = AuthMessageProvider.BINDING_FAILURE;
            res.setStatus(code);
            return new SubCodeMessageResponse(message, 400,code);
        }
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);

        // 의사회원 중 승인이 거부되지 않은 사용자는 제외한다
        // 환자회원 중 활성화 된 계정은 제외한다
        // 가독성을 위해 if, else if  분리 유지
        // 추후 타입을 분리할 필요도 보임
        if (user != null && !user.isDenied() && user instanceof Doctor){
            code = 400;
            message = AuthMessageProvider.DUPLICATED_EMAIL;
            return new SubCodeMessageResponse(message,401,code);
        } else if(user != null && user.isActive()){
            code = 400;
            message = AuthMessageProvider.DUPLICATED_EMAIL;
            return new SubCodeMessageResponse(message,401,code);
        }
        return new SubCodeMessageResponse(message,200,code);

    }

    // 의사 회원가입

    @PostMapping("/join/doctor")
    public CodeMessageResponse doctorJoin(
            HttpServletRequest req,
            HttpServletResponse res,
            @RequestBody @Validated DoctorDto dto,
            BindingResult result
    ) {
        int code = 201;
        String message = AuthMessageProvider.JOIN_SUCCESS;

        if (result.hasErrors()) {
            log.info("type error={}", result.getAllErrors());
            code = 400;
            message = AuthMessageProvider.BINDING_FAILURE;
            res.setStatus(code);
            return new CodeMessageResponse(message, code);
        }

        try {
            authService.createDoctorUser(dto);
        } catch (Exception e) {
            if (!(e instanceof IllegalStateException)) {
                throw new RuntimeException("서버 내부 에러가 발생하였습니다", e);
            } else {
                throw e;
            }
        }

        return new CodeMessageResponse(message, code);
    }

    // 환자 회원가입
    @PostMapping("/join/patient")
    public CodeMessageResponse patientJoin(
            HttpServletRequest req,
            HttpServletResponse res,
            @RequestBody @Validated PatientDto dto,
            BindingResult result
    ) throws MessagingException {
        int code = 201;
        String message = AuthMessageProvider.JOIN_SUCCESS;

        if (result.hasErrors()) {
            log.info("type error={}", result.getAllErrors());
            code = 400;
            message = AuthMessageProvider.BINDING_FAILURE;
            res.setStatus(code);
            return new CodeMessageResponse(message, code);
        }

        try {
            authService.createPatientUser(dto);
        } catch (Exception e) {
            if (!(e instanceof IllegalStateException)) {
                throw new RuntimeException("서버 내부 에러가 발생하였습니다", e);
            } else {
                throw e;
            }
        }

        return new CodeMessageResponse(message, code);
    }


    // 환자 인증
    @PostMapping("/validate/patient")
    public CodeMessageResponse patientValidation(
            HttpServletRequest req,
            HttpServletResponse res,
            @RequestBody @Validated PatientCodeDto dto,
            BindingResult result
    ) {
        int code = 200;
        String message = AuthMessageProvider.VALIDATION_SUCCESS;

        if (result.hasErrors()) {
            log.info("type error={}", result.getAllErrors());
            code = 400;
            message = AuthMessageProvider.BINDING_FAILURE;
            res.setStatus(code);
            return new CodeMessageResponse(message, code);
        }

        try {
            authService.validatePatient(dto);
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

        res.setStatus(code);
        return new CodeMessageResponse(message, code);
    }

    // 인증용 이미지 업로드

    @PostMapping("/image")
    public CodeMessageResponse authenticationImage(HttpServletResponse res,@RequestParam MultipartFile file) throws IOException {
        if(file.isEmpty()){
            res.setStatus(400);
            return new CodeMessageResponse(CommonMessageProvider.NO_UPLOAD,400);
        }
        System.out.println(path);
        System.out.println(address);
        String uuid = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if (!extension.equals("jpg") && !extension.equals("png") && !extension.equals("jpeg") && !extension.equals("pdf")) {
            return new CodeMessageResponse(CommonMessageProvider.NOT_IMAGE,400);
        }
        String fullPath = path + "/" + uuid + "."+ extension;

        file.transferTo(new File(fullPath));

        return new CodeMessageResponse(address+"/authentication_image"+"/"+uuid+"."+extension,200);

    }

}
