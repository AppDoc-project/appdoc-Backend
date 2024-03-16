package webdoc.authentication.domain.exceptions;
/*
* 인증시간 초과 예외
 */
public class TimeOutException extends RuntimeException{
    public TimeOutException(String message) {
        super(message);
    }

    public TimeOutException(Throwable e, String message) {
        super(message,e);
    }
}
