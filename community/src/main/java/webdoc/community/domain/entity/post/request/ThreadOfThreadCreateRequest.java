package webdoc.community.domain.entity.post.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThreadOfThreadCreateRequest {
    private String text;
    private Long parentThreadId;
    private Long postId;
}
