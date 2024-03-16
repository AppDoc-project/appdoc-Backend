package webdoc.community.domain.exceptions;

import java.time.LocalDateTime;
/*
 * 튜터가 이미 예약을 갖고 있어서 발생하는 예외
 */
public class TutorAlreadyHasReservationException extends RuntimeException{


    public TutorAlreadyHasReservationException (String message){
        super(message);
    }
    public TutorAlreadyHasReservationException (Throwable e, String message){
        super(message,e);

    }
}
