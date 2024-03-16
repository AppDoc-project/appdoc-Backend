package webdoc.community.domain.entity.review.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
/*
* 리뷰 생성 객체
 */
@Getter
@Setter
public class ReviewCreateRequest {
    @NotNull
    private Long lessonId;

    @Size(max = 300)
    @NotEmpty
    private String review;

    @Min(value = 0)
    @Max(value = 5)
    @NotNull
    private Integer score;
}
