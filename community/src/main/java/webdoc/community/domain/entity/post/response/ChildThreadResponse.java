package webdoc.community.domain.entity.post.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
/*
* 대댓글 응답객체 : deprecated
 */
@Getter
@RequiredArgsConstructor
@ToString
public class ChildThreadResponse {
    private final Long id;
    private final Long userId;
    private final String createdAt;
    private final String text;
    private final String nickName;
    private final Boolean isTutor;
    private final String profile;
}
