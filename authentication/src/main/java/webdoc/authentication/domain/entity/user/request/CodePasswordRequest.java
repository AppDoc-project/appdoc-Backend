package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodePasswordRequest {
    public CodePasswordRequest(String password, String code){
        this.password = password;
        this.code = code;
    }
    @NotNull
    private String code;
    @NotEmpty
    @Size(min = 8, max = 18)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@~_])[a-zA-Z\\d!@~_]+$")
    private String password;

    @Email
    private String email;
}

