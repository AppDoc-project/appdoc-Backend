package webdoc.community.domain.entity.post.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostCreateRequest {
    @NotEmpty
    @Max(3000)
    private String text;
    @NotNull
    private Long communityId;
    @Size(max = 5)
    private List<AddressAndPriority> addresses = new ArrayList<>();
    @Getter
    @Setter
    public static class AddressAndPriority {
        public AddressAndPriority(String address, Long priority){
            this.address = address;
            this.priority = priority;
        }
        private String address;
        private Long priority;
    }

}