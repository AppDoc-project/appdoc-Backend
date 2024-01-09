package webdoc.authentication.domain.entity.user.tutor.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;

import java.util.List;

@Getter
@Setter
public class TutorSpecialityRequest{
    public TutorSpecialityRequest(){}
    @NotEmpty
    private String authenticationAddress;
    @NotNull
    private List<Specialities> specialities;

    @Builder
    private TutorSpecialityRequest(
                               List<Specialities> specialities, String authenticationAddress){

        this.specialities = specialities;
        this.authenticationAddress = authenticationAddress;

    }

}