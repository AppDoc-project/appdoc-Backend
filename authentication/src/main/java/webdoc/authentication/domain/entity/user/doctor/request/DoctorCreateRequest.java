package webdoc.authentication.domain.entity.user.doctor.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import webdoc.authentication.domain.entity.user.doctor.enums.MedicalSpecialities;

import java.time.LocalDate;

@Getter
@Setter
public class DoctorCreateRequest {

    public DoctorCreateRequest (){}

    @NotNull
    private LocalDate dateOfBirth;
    @Email
    @Size(max=50)
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[가-힣]*$")
    private String name;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d).+$")
    private String password;

    @NotEmpty
    @Pattern(regexp = "^[0-9]{11}$")
    private String contact;


    @Size(max=100)
    private String hospitalName;

    @NotEmpty
    private String certificateAddress;

    @NotEmpty
    @Size(max=1000)
    private String address;
    @NotNull
    private MedicalSpecialities medicalSpeciality;

    @Size(max=1000)
    private String selfDescription;

    @Builder
    private DoctorCreateRequest(LocalDate dateOfBirth,String name, String email,
                                String address, MedicalSpecialities medicalSpeciality,
                                String selfDescription,String certificateAddress,String contact,
                                String hospitalName,String password){
        this.dateOfBirth = dateOfBirth;
        this.name = name;
        this.email = email;
        this.address = address;
        this.medicalSpeciality = medicalSpeciality;
        this.selfDescription = selfDescription;
        this.certificateAddress = certificateAddress;
        this.contact = contact;
        this.hospitalName = hospitalName;
        this.password = password;
    }



}
