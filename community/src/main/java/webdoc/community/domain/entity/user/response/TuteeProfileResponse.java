package webdoc.community.domain.entity.user.response;

import lombok.Getter;
import lombok.Setter;
import webdoc.community.domain.entity.user.Specialities;
import webdoc.community.domain.response.CountResponse;

import java.util.List;

@Getter
@Setter
public class TuteeProfileResponse extends CountResponse {
    private String name;

    private String profile;

    public TuteeProfileResponse(int bookmarkCount, int postCount, int threadCount,
                                String name,String profile){
        super(postCount,threadCount,bookmarkCount);
        this.name = name;
        this.profile = profile;
    }
}