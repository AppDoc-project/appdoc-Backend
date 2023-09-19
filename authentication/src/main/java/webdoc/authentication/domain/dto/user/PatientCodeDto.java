package webdoc.authentication.domain.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientCodeDto {
    @NotNull
    private String email;
    @NotEmpty
    private String code;
}
