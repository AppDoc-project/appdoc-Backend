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

    public TuteeProfileResponse(int bookmarkCount, int postCount, int threadCount,
                                String name){
        super(postCount,threadCount,bookmarkCount);
        this.name = name;
    }
}