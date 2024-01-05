package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
