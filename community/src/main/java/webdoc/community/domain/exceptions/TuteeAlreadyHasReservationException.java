package webdoc.community.domain.exceptions;
/*
 * 튜티가 이미 예약을 갖고 있어서 발생하는 예외
 */
public class TuteeAlreadyHasReservationException extends RuntimeException{

    public TuteeAlreadyHasReservationException (String message){
        super(message);
    }
    public TuteeAlreadyHasReservationException (Throwable e, String message){
        super(message,e);

    }
}
