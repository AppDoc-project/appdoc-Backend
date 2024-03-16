package webdoc.community.domain.entity.post.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
/*
* 댓글 생성 객체
 */
@Getter
@Setter
public class ThreadCreateRequest {
    @NotNull
    private Long postId;

    @NotEmpty
    @Size(max = 500)
    private String text;

    @Builder
    private ThreadCreateRequest(Long postId,String text){
        this.postId = postId;
        this.text = text;
    }

}
