package webdoc.authentication.domain.exceptions;
/*
 * 회원탈퇴 시 남은 예약, 레슨이 있을 경우 예외 발생
 */
public class HasReservationOrLessonException extends RuntimeException{
    public HasReservationOrLessonException(String message){
        super(message);
    }

    public HasReservationOrLessonException(Throwable e, String message){
        super(message,e);
    }
}
