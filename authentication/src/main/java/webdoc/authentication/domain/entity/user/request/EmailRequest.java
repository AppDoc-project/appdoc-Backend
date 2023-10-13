package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    @NotEmpty
    private  String email;
}
