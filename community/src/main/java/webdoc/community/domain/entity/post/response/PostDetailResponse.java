package webdoc.community.domain.entity.post.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
public class PostDetailResponse {
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

    // 내가 북마크 눌렀는 지 여부
    private final Boolean bookmarkYN;

    private final String text;
    private final Long view;
    // 사진 리스트
    private final List<String> pictures;






}
