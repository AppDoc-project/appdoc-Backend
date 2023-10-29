package webdoc.community.domain.entity.post.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThreadCreateRequest {
    @NotNull
    private Long postId;

    @NotEmpty
    @Max(500)
    private String text;

}
