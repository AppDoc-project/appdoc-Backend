package webdoc.community.domain.entity.like;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import webdoc.community.domain.BaseEntity;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.user.User;

@Entity(name = "love")
@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class Like extends BaseEntity {
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
