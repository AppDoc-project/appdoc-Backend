package webdoc.authentication.domain.dto.user;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import webdoc.authentication.domain.enums.MedicalSpecialities;

@Getter
@Setter
public class DoctorDto {
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


    @NotEmpty
    private String certificateAddress;


    @NotEmpty
    @Size(max=1000)
    private String address;
    @NotNull
    private MedicalSpecialities medicalSpeciality;

    @Size(max=1000)
    private String selfDescription;


}
