package webdoc.community.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.utility.messageprovider.CommonMessageProvider;
import webdoc.community.utility.messageprovider.ResponseCodeProvider;

import java.util.NoSuchElementException;

@RestControllerAdvice(assignableTypes = {CommunityController.class})
public class CommunityControlAdvice {
    // 서버에러
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CodeMessageResponse serverError(RuntimeException e){
        e.printStackTrace();
        return new CodeMessageResponse(CommonMessageProvider.INTERNAL_SERVER_ERROR,500, ResponseCodeProvider.INTERNAL_SERVER_ERROR);
    }

    // 비정상적인 접근
    @ExceptionHandler({NoSuchElementException.class,IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse invalidAccess(Exception e){
        e.printStackTrace();
        return new CodeMessageResponse(CommonMessageProvider.INVALID_ACCESS,400,400);
    }

    // 바인딩 실패
    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse bindingFailure(Exception e){
        e.printStackTrace();
        return new CodeMessageResponse(CommonMessageProvider.BINDING_FAILURE,400,ResponseCodeProvider.BINDING_FAILURE);
    }

}
