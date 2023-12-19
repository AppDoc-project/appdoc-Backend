package webdoc.community.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.utility.messageprovider.CommonMessageProvider;
import webdoc.community.utility.messageprovider.ResponseCodeProvider;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice(assignableTypes = {CommunityController.class})
public class CommunityControlAdvice {
    // 서버에러
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CodeMessageResponse serverError(Exception e){
        log.info("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.INTERNAL_SERVER_ERROR,500, ResponseCodeProvider.INTERNAL_SERVER_ERROR);
    }

    // 비정상적인 접근
    @ExceptionHandler({NoSuchElementException.class,IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse invalidAccess(Exception e){
        log.info("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.INVALID_ACCESS,400,ResponseCodeProvider.INVALID_ACCESS);
    }

    // 바인딩 실패
    @ExceptionHandler({MethodArgumentTypeMismatchException.class,IllegalArgumentException.class, HttpMessageNotReadableException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse bindingFailure(Exception e){
        log.info("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.BINDING_FAILURE,400,ResponseCodeProvider.BINDING_FAILURE);
    }



}
