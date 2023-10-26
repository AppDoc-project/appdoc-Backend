package webdoc.authentication.domain.entity.user.patient.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientCreateRequest {

    @NotNull
    private LocalDate dateOfBirth;
    @Email
    @Size(max=50)
    private String email;

    @NotEmpty
    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[가-힣]*$")
    private String name;

    @NotEmpty
    @Size(min = 8, max = 18)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d).+$")
    private String password;

    @NotEmpty
    @Pattern(regexp = "^[0-9]{11}$")
    private String contact;

    @Builder
    private PatientCreateRequest(LocalDate dateOfBirth,String email,String name,
                                 String password, String contact){
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.name = name;
        this.password = password;
        this.contact = contact;
    }


}
