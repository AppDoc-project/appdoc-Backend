package webdoc.community.domain.entity.user.response;

import lombok.Getter;
import lombok.Setter;
/*
* 튜티 프로필 응답객체
 */
@Getter
@Setter
public class TuteeProfileResponse extends CountResponse {
    private String name;

    private int pickCount;
    private String profile;

    public TuteeProfileResponse(int bookmarkCount, int postCount, int threadCount, int pickCount,
                                String name,String profile){
        super(postCount,threadCount,bookmarkCount);
        this.name = name;
        this.profile = profile;
        this.pickCount = pickCount;
    }
}