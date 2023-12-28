package webdoc.community.domain.entity.user.response;

import lombok.Getter;
import lombok.Setter;
import webdoc.community.domain.entity.user.Specialities;
import webdoc.community.domain.response.CountResponse;

import java.util.List;

@Getter
@Setter
public class TutorProfileResponse extends CountResponse {
    private String name;
    private List<Specialities> specialities;

    public TutorProfileResponse(int bookmarkCount, int postCount, int threadCount,
                                String name, List<Specialities> specialities){
        super(postCount,threadCount,bookmarkCount);
        this.name = name;
        this.specialities = specialities;
    }
}
