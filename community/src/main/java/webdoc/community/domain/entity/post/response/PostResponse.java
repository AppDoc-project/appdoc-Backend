package webdoc.community.domain.entity.post.response;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
/*
* 게시글 응답 객체
 */

@Getter
@RequiredArgsConstructor
@ToString
public class PostResponse {
    private final Long id;
    private final Long userId;
    private final String title;
    private final String nickName;
    private final String profile;

    private final String text;
    private final Integer bookmarkCount;
    private final Integer likeCount;
    private final Integer threadCount;
    private final Integer mediaCount;
    private final String createdAt;
    private final Boolean isTutor;
    private final Long view;
    private final String communityName;
    private final Long communityId;

    // 닉네임, 제목, 내용 ,작성자 프로필, 사진 포함 여부 및 개수, 댓글 수, 좋아요 수, 튜터 여부

}
