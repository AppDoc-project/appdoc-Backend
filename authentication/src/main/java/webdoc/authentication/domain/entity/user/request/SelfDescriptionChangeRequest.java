package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelfDescriptionChangeRequest {
    protected SelfDescriptionChangeRequest(){}
    @Size(max=1000)
    @NotEmpty
    private String selfDescription;

    @Builder
    private SelfDescriptionChangeRequest(String selfDescription){
        this.selfDescription = selfDescription;
    }
}
