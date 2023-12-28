package webdoc.community.domain.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserBannedException extends RuntimeException{

    private LocalDateTime untilWhen;

    public UserBannedException(String message,LocalDateTime untilWhen){
        super(message);
        this.untilWhen = untilWhen;
    }
    public UserBannedException(Throwable e, String message,LocalDateTime untilWhen){
        super(message,e);
        this.untilWhen = untilWhen;
    }
}
