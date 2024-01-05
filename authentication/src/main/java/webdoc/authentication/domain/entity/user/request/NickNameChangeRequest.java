package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NickNameChangeRequest {
    protected NickNameChangeRequest(){}
    @Size(max = 10)
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$")
    private String nickName;

    @Builder
    private NickNameChangeRequest(String nickName){
        this.nickName = nickName;
    }
}
