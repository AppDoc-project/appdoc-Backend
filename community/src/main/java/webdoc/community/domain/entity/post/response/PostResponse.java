package webdoc.community.domain.entity.post.response;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@RequiredArgsConstructor
@ToString
public class PostResponse {
    private final Long id;
    private final Long userId;
    private final String title;
    private final String nickName;
    private final String profile;
    private final Integer bookmarkCount;
    private final Integer likeCount;
    private final Integer threadCount;
    private final Integer mediaCount;
    private final LocalDateTime createdAt;
    private final Boolean isTutor;
    private final Long view;

    // 닉네임, 제목, 작성자 프로필, 사진 포함 여부 및 개수, 댓글 수, 좋아요 수, 튜터 여부

}
