package webdoc.community.domain.entity.post.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
/*
* 대댓글 생성 객체 : deprecated
 */
@Getter
@Setter
public class ThreadOfThreadCreateRequest {
    @NotEmpty
    @Size(max = 500)
    private String text;
    @NotNull
    private Long parentThreadId;
    @NotNull
    private Long postId;

    @Builder
    private ThreadOfThreadCreateRequest(String text, Long parentThreadId, Long postId){
        this.text = text;
        this.parentThreadId = parentThreadId;
        this.postId = postId;
    }
}
