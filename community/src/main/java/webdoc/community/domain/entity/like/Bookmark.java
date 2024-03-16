package webdoc.community.domain.entity.like;

import jakarta.persistence.*;
import lombok.*;
import webdoc.community.domain.BaseEntity;
import webdoc.community.domain.entity.post.Post;

/*
 * 북마크 도메인 객체
 */
@Entity
@EqualsAndHashCode(of = "id")
@Getter
public class Bookmark extends BaseEntity {
    protected Bookmark(){ }
    public void setPost(Post post){
        this.post = post;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;
    public static Bookmark createBookmark(Long userId,Post post){
        return Bookmark.builder()
                .userId(userId)
                .post(post)
                .build();
    }
    @Builder
    private Bookmark(Long userId,Post post){
        this.userId = userId;
        post.addBookmark(this);
    }
}