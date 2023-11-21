package webdoc.community.domain.entity.like;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import webdoc.community.domain.BaseEntity;
import webdoc.community.domain.entity.post.Post;

@Entity
@EqualsAndHashCode(of = "id")
@Getter
@Setter(value = AccessLevel.PRIVATE)
public class Bookmark extends BaseEntity {

    public void setPost(Post post){
        this.post = post;
    }
    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;


}