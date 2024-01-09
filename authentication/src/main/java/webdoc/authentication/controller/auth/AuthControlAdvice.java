package webdoc.authentication.controller.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webdoc.authentication.domain.exceptions.EmailDuplicationException;
import webdoc.authentication.domain.exceptions.WrongPasswordException;
import webdoc.authentication.domain.response.CodeMessageResponse;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import webdoc.authentication.utility.messageprovider.CommonMessageProvider;
import webdoc.authentication.utility.messageprovider.ResponseCodeProvider;

import java.util.NoSuchElementException;
@Slf4j
@RestControllerAdvice(assignableTypes = {AuthController.class, AuthServerController.class, AuthSettingController.class})
public class AuthControlAdvice {
    // 이메일이 중복되는 경우
    @ExceptionHandler(EmailDuplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse emailExists(EmailDuplicationException e){
        log.info("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(AuthMessageProvider.EMAIL_EXISTS,400, ResponseCodeProvider.EMAIL_EXISTS);
    }

    // 타입 불 일치 및 바인딩 실패
    @ExceptionHandler({HttpMessageNotReadableException.class,IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse typeMismatch(Exception e){
        log.info("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.BINDING_FAILURE,400,ResponseCodeProvider.BINDING_FAILURE);
    }

    // 서버에러
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CodeMessageResponse serverError(Exception e){
        log.info("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.INTERNAL_SERVER_ERROR,500,ResponseCodeProvider.INTERNAL_SERVER_ERROR);
    }

    // 비정상적인 접근
    @ExceptionHandler({NoSuchElementException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse invalidAccess(Exception e){
        log.info("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.INVALID_ACCESS,400,ResponseCodeProvider.INVALID_ACCESS);
    }

    // 인증 시간이 초과할 경우
    @ExceptionHandler(TimeOutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse timeOut(TimeOutException e){
        log.info("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(AuthMessageProvider.VALIDATION_EXPIRED,400,ResponseCodeProvider.VALIDATION_EXPIRED);
    }

    // 인증 코드가 틑릴 경우
    @ExceptionHandler(AuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse wrongCode(AuthenticationServiceException e){
        log.info("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(AuthMessageProvider.WRONG_CODE,400,ResponseCodeProvider.WRONG_CODE);
    }

    // 비밀번호가 틀릴 경우
    @ExceptionHandler(WrongPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse wrongPassword(WrongPasswordException e){
        log.info("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(AuthMessageProvider.WRONG_PASSWORD,400,ResponseCodeProvider.WRONG_CODE);
    }



}
