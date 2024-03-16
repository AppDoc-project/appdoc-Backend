package webdoc.authentication.domain.exceptions;
/*
* 비밀번호 틀릴 경우 발생 예외
 */
public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String message) {
        super(message);
    }

    public WrongPasswordException(Throwable e, String message) {
        super(message,e);
    }
}
