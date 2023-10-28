package webdoc.community.domain.entity.post;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.user.User;

import java.util.List;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
public class Post {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name = "user_id")
    private User user;

    @Column(nullable = false, length = 3000)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "community_id")
    private Community community;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "post")
    private List<Picture> pictures;


}
