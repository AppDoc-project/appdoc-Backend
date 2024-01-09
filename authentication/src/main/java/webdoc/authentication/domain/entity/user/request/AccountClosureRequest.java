package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
