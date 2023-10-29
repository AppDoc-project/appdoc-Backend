package webdoc.community.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.utility.messageprovider.CommonMessageProvider;

import java.util.NoSuchElementException;

@RestControllerAdvice(assignableTypes = {CommunityController.class})
public class CommunityControlAdvice {
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

    // 바인딩 실패
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse bindingFailure(NoSuchElementException e){
        e.printStackTrace();
        return new CodeMessageResponse(CommonMessageProvider.BINDING_FAILURE,400,400);
    }

}
