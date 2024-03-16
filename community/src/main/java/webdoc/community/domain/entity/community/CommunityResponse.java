package webdoc.community.domain.entity.community;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/*
 * 커뮤니티 응답 객체
 */
@Getter
@Setter
public class CommunityResponse {
    public CommunityResponse(){}
    public CommunityResponse(String name, Long id){
        this.name = name;
        this.id = id;
    }
    private  String name;
    private  Long id;
}
