package webdoc.community.domain.entity.post.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private List<AddressAndPriority> addresses = new ArrayList<>();
    @Getter
    @Setter
    public static class AddressAndPriority {
        public AddressAndPriority(String address, Integer priority){
            this.address = address;
            this.priority = priority;
        }
        private String address;
        private Integer priority;
    }

    @Builder
    private PostModifyRequest(Long postId,String title,String text,List<AddressAndPriority> addressAndPriorities){
        this.title = title;
        this.text = text;
        this.addresses = addressAndPriorities;
        this.postId = postId;
    }

}
