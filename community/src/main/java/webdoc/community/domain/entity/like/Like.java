package webdoc.community.domain.entity.like;

import jakarta.persistence.*;
import lombok.*;
import webdoc.community.domain.BaseEntity;
import webdoc.community.domain.entity.post.Post;

@Entity(name = "choose")
@EqualsAndHashCode(of = "id")
@Getter
@Setter(value = AccessLevel.PRIVATE)
public class Like extends BaseEntity {
    protected Like(){}

    public static Like createLike(Long userId,Post post){
        return Like.builder()
                .userId(userId)
                .post(post)
                .build();
    }
    @Builder
    private Like(Long userId,Post post){
        this.userId = userId;
        post.addLikes(this);
    }
    public void setPost(Post post){
        this.post = post;
    }
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;


}
