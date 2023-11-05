package webdoc.community.domain.entity.like;

import jakarta.persistence.*;
import lombok.*;
import webdoc.community.domain.BaseEntity;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.user.User;

@Entity(name = "love")
@EqualsAndHashCode(of = "id")
@Getter
@Setter(value = AccessLevel.PRIVATE)
public class Like extends BaseEntity {
    protected Like(){}

    public static Like createLike(User user,Post post){
        return Like.builder()
                .user(user)
                .post(post)
                .build();
    }
    @Builder
    private Like(User user,Post post){
        this.user = user;
        this.post = post;
        post.addLikes(this);
    }
    public void setPost(Post post){
        this.post = post;
    }
    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;


}
