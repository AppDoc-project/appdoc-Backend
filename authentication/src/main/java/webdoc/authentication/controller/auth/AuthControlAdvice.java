package webdoc.authentication.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webdoc.authentication.domain.dto.response.CodeMessageResponse;
import webdoc.authentication.domain.dto.response.SubCodeMessageResponse;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import webdoc.authentication.utility.messageprovider.CommonMessageProvider;

import java.util.NoSuchElementException;

@RestControllerAdvice(assignableTypes = {AuthController.class})
public class AuthControlAdvice {

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse emailExists(IllegalStateException e){
        e.printStackTrace();
        return new CodeMessageResponse(AuthMessageProvider.EMAIL_EXISTS,400);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse typeMismatch(HttpMessageNotReadableException e){
        e.printStackTrace();
        return new CodeMessageResponse(AuthMessageProvider.BINDING_FAILURE,400);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CodeMessageResponse serverError(RuntimeException e){
        e.printStackTrace();
        return new CodeMessageResponse(CommonMessageProvider.INTERNAL_SERVER_ERROR,500);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SubCodeMessageResponse invalidAccess(NoSuchElementException e){
        e.printStackTrace();
        return new SubCodeMessageResponse(CommonMessageProvider.INVALID_ACCESS,400,400);
    }

    @ExceptionHandler(TimeOutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SubCodeMessageResponse timeOut(TimeOutException e){
        e.printStackTrace();
        return new SubCodeMessageResponse(AuthMessageProvider.VALIDATION_EXPIRED,401,400);
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SubCodeMessageResponse wrongCode(AuthenticationServiceException e){
        e.printStackTrace();
        return new SubCodeMessageResponse(AuthMessageProvider.WRONG_CODE,402,400);
    }




}
