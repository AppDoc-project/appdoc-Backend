package webdoc.authentication.domain.exceptions;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String message) {
        super(message);
    }

    public WrongPasswordException(Throwable e, String message) {
        super(message,e);
    }
}
