package webdoc.community.controller.community;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import webdoc.community.controller.lesson.LessonController;
import webdoc.community.controller.tutor.ReservationController;
import webdoc.community.controller.tutor.TutorController;
import webdoc.community.controller.profile.ProfileController;
import webdoc.community.domain.exceptions.TutorAlreadyHasReservationException;
import webdoc.community.domain.exceptions.UserBannedException;
import webdoc.community.domain.exceptions.UserNotExistException;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.utility.messageprovider.CommonMessageProvider;
import webdoc.community.utility.messageprovider.ResponseCodeProvider;
import java.util.NoSuchElementException;
/*
* 예외처리 Controller Advice
 */
@Slf4j
@RestControllerAdvice(assignableTypes = {CommunityController.class, ProfileController.class, TutorController.class, ReservationController.class, LessonController.class})
public class CommunityControlAdvice {

    // 서버에러
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CodeMessageResponse serverError(Exception e){
        log.error("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.INTERNAL_SERVER_ERROR,500, ResponseCodeProvider.INTERNAL_SERVER_ERROR);
    }

    // 비정상적인 접근
    @ExceptionHandler({NoSuchElementException.class,IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse invalidAccess(Exception e){
        log.error("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.INVALID_ACCESS,400,ResponseCodeProvider.INVALID_ACCESS);
    }

    // 바인딩 실패
    @ExceptionHandler({MethodArgumentTypeMismatchException.class,IllegalArgumentException.class, HttpMessageNotReadableException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse bindingFailure(Exception e){
        log.error("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.BINDING_FAILURE,400,ResponseCodeProvider.BINDING_FAILURE);
    }

    // 차단된 유저가 접근
    @ExceptionHandler({UserBannedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse userBanned(UserBannedException e){
        log.error("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(e.getUntilWhen().toString(), 400, ResponseCodeProvider.BANNED);
    }

    // 없는 자원을 접근
    @ExceptionHandler({UserNotExistException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse notExists(Exception e){
        log.error("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.NOT_FOUND, 400, ResponseCodeProvider.NOT_FOUND);

    }

    // 튜터 및 튜티의 예약시간이 존재한다
    @ExceptionHandler({TutorAlreadyHasReservationException.class, TutorAlreadyHasReservationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CodeMessageResponse tuteeTutorReservation(Exception e){
        log.error("에러 메시지: {}", e.getMessage(), e);
        return new CodeMessageResponse(CommonMessageProvider.RESERVATION_EXISTS, 400, ResponseCodeProvider.ALREADY_EXISTS);

    }



}
