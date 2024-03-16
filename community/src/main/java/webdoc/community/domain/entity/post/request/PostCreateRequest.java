package webdoc.community.domain.entity.post.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
/*
* 게시글 생성 객체
 */
@Getter
@Setter
public class PostCreateRequest {

    @NotEmpty
    @Size(max = 20)
    private String title;
    @NotEmpty
    @Size(max = 3000)
    private String text;
    @NotNull
    private Long communityId;
    @Size(max = 5)
    private List<String> addresses = new ArrayList<>();


    @Builder
    private PostCreateRequest(String title,String text,Long communityId,List<String> addresses){
        this.title = title;
        this.text = text;
        this.communityId = communityId;
        this.addresses = addresses;
    }

}
