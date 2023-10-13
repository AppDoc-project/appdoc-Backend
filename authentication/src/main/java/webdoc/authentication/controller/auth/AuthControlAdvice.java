package webdoc.authentication.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webdoc.authentication.domain.response.CodeMessageResponse;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import webdoc.authentication.utility.messageprovider.CommonMessageProvider;

import java.util.NoSuchElementException;

@RestControllerAdvice(assignableTypes = {AuthController.class})
public class AuthControlAdvice {
    // 이메일이 중복되는 경우
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse emailExists(IllegalStateException e){
        e.printStackTrace();
        return new CodeMessageResponse(AuthMessageProvider.EMAIL_EXISTS,400,null);
    }

    // 타입 불 일치
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse typeMismatch(HttpMessageNotReadableException e){
        e.printStackTrace();
        return new CodeMessageResponse(AuthMessageProvider.BINDING_FAILURE,400,null);
    }

    // 서버에러
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CodeMessageResponse serverError(RuntimeException e){
        e.printStackTrace();
        return new CodeMessageResponse(CommonMessageProvider.INTERNAL_SERVER_ERROR,500,null);
    }

    // 비정상적인 접근
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse invalidAccess(NoSuchElementException e){
        e.printStackTrace();
        return new CodeMessageResponse(CommonMessageProvider.INVALID_ACCESS,400,400);
    }

    // 인증 시간이 초과할 경우
    @ExceptionHandler(TimeOutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse timeOut(TimeOutException e){
        e.printStackTrace();
        return new CodeMessageResponse(AuthMessageProvider.VALIDATION_EXPIRED,401,400);
    }

    // 인증 코드가 틑릴 경우
    @ExceptionHandler(AuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse wrongCode(AuthenticationServiceException e){
        e.printStackTrace();
        return new CodeMessageResponse(AuthMessageProvider.WRONG_CODE,400,402);
    }




}
