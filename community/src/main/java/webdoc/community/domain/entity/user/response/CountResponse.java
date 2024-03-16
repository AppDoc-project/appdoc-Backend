package webdoc.community.domain.entity.user.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
/*
* 작성글, 댓글, 북마크 카운트 객체
 */
@Getter
@Setter
@RequiredArgsConstructor
public class CountResponse {
    private final int postCount;

    private final int threadCount;

    private final int bookmarkCount;
}
