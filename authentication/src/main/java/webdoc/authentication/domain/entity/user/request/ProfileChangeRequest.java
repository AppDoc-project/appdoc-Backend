package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
/*
* 프로필 변경 요청 객체
 */
@Getter
@Setter
public class ProfileChangeRequest {
    protected ProfileChangeRequest(){}

    @NotEmpty
    private String profile;
    @Builder
    private ProfileChangeRequest(String profile){
        this.profile = profile;
    }
}
