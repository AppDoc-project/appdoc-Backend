package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
/*
* 코드 전달 객체
 */
@Getter
@Setter
public class CodeRequest {

    public CodeRequest(String email, String code){
        this.email = email;
        this.code = code;
    }
    @NotNull
    private String email;
    @NotEmpty
    private String code;
}
