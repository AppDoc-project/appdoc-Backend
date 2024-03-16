package webdoc.community.domain.entity.review.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
/*
* 리뷰 응답 객체
 */
@Getter
@RequiredArgsConstructor
@Setter
public class ReviewResponse {
    private final Long reviewId;

    private final Long userId;
    private final String nickName;
    private final String review;
    private final String profile;
    private final String createdAt;

    private final int score;
}
