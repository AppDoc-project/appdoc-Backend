package webdoc.authentication.domain.entity.user.tutor.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TutorCreateRequest {

    public TutorCreateRequest(){}

    @Email
    @Size(max=50)
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[가-힣]*$")
    private String name;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@~_])[a-zA-Z\\d!@~_]+$")
    @Size(min = 8, max = 18)
    private String password;

    @NotEmpty
    @Pattern(regexp = "^[0-9]{11}$")
    private String contact;
    @NotEmpty
    private String authenticationAddress;
    @NotNull
    private List<Specialities> specialities;

    @Size(max=1000)
    private String selfDescription;

    @Builder
    private TutorCreateRequest(String name, String email,
                               List<Specialities> specialities,
                               String selfDescription, String authenticationAddress, String contact,
                               String password){
        this.name = name;
        this.email = email;
        this.specialities = specialities;
        this.selfDescription = selfDescription;
        this.authenticationAddress = authenticationAddress;
        this.contact = contact;
        this.password = password;
    }



}
