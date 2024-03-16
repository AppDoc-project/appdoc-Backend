package webdoc.community.domain.entity.like.request;

import lombok.Getter;
import lombok.Setter;
/*
 * 좋아요 및 북마크 생성 객체
 */
@Getter
@Setter
public class CreateRequestWithPostId {
    private Long postId;
}
