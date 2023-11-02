package webdoc.authentication.domain.exceptions;

public class TimeOutException extends RuntimeException{
    public TimeOutException(String message) {
        super(message);
    }

    public TimeOutException(Throwable e, String message) {
        super(message,e);
    }
}
