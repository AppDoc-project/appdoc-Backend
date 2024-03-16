package webdoc.community.domain.entity.report;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;
import webdoc.community.domain.entity.post.Post;
/*
* 게시글 신고 도메인 객체
 */
@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@DiscriminatorValue("post")
public class PostReport extends Report {

    protected PostReport(){}

    @Builder
    private PostReport(Long userId, String reason, Post post){
        super(userId, reason);
        this.post = post;

    }

    public static PostReport createPostReport(String reason, Long userId, Post post){
        return
                PostReport.builder()
                        .userId(userId)
                        .reason(reason)
                        .post(post)
                        .build();
    }
    @OneToOne
    @JoinColumn(nullable = false)
    private Post post;

}
