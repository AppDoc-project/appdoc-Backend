package webdoc.community.domain.entity.feedback.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
/*
 * 피드백 생성 객체
 */
@Getter
@Setter
public class FeedbackCreateRequest {
    @NotNull
    private Long lessonId;

    @Size(max = 500)
    @NotEmpty
    private String feedback;

}
