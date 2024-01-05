package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {
    @NotEmpty
    @Size(min = 8, max = 18)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@~_])[a-zA-Z\\d!@~_]+$")
    private String currentPassword;

    @NotEmpty
    @Size(min = 8, max = 18)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@~_])[a-zA-Z\\d!@~_]+$")
    private String changedPassword;

    @Builder
    private PasswordChangeRequest(String currentPassword,String changedPassword){
        this.currentPassword = currentPassword;
        this.changedPassword = changedPassword;
    }
}
