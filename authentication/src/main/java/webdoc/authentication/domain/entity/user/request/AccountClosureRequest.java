package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/*
 * 회원 삭제 요청 객체
 */
@Getter
@Setter
public class AccountClosureRequest {

    protected AccountClosureRequest(){}
    @NotEmpty
    private String password;

    @Builder
    private AccountClosureRequest(String password){
        this.password = password;
    }
}
