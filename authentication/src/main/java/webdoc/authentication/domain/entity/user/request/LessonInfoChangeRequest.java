package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;

import java.util.List;
/*
* 레슨 정보 전달객체
 */
@Getter
@Setter
public class LessonInfoChangeRequest {
    protected LessonInfoChangeRequest(){}
    @NotEmpty
    private String authenticationAddress;
    @NotNull
    private List<Specialities> specialities;

    @Builder
    private LessonInfoChangeRequest(String authenticationAddress, List<Specialities> specialities){
        this.authenticationAddress = authenticationAddress;
        this.specialities = specialities;
    }

}
