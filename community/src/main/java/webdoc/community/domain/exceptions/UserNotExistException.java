package webdoc.community.domain.exceptions;

import java.time.LocalDateTime;
/*
 * 해당 유저가 존재하지 않아서 발생하는 예외
 */
public class UserNotExistException extends RuntimeException {
    public UserNotExistException(String message){
        super(message);
    }
    public UserNotExistException(Throwable e, String message){
        super(message,e);
    }
}
