package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactChangeRequest {

    protected ContactChangeRequest(){ }
    @NotEmpty
    @Pattern(regexp = "^[0-9]{11}$")
    private String contact;

    @NotEmpty
    @Size(min = 8, max = 18)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@~_])[a-zA-Z\\d!@~_]+$")
    private String currentPassword;
    @Builder
    private ContactChangeRequest(String contact,String currentPassword){
       this.contact = contact;
       this.currentPassword = currentPassword;
    }
}
