package webdoc.authentication.domain.exceptions;

public class EmailDuplicationException extends RuntimeException{
    public EmailDuplicationException(String message){
        super(message);
    }

    public EmailDuplicationException(Throwable e, String message){
        super(message,e);
    }
}
