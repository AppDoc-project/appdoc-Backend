package webdoc.community.domain.entity.post.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
/*
* 게시글 수정 객체
 */
@Getter
@Setter
public class PostModifyRequest {

    @NotNull
    private Long postId;

    @NotEmpty
    @Size(max = 20)
    private String title;
    @NotEmpty
    @Size(max = 3000)
    private String text;
    @Size(max = 5)
    private List<String> addresses = new ArrayList<>();


    @Builder
    private PostModifyRequest(Long postId,String title,String text,List<String> addresses){
        this.title = title;
        this.text = text;
        this.addresses = addresses;
        this.postId = postId;
    }

}
