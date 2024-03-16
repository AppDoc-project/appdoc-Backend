package webdoc.authentication.domain.exceptions;
/*
* 이메일 중복 예외
 */
public class EmailDuplicationException extends RuntimeException{
    public EmailDuplicationException(String message){
        super(message);
    }

    public EmailDuplicationException(Throwable e, String message){
        super(message,e);
    }
}
