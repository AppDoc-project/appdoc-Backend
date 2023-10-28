package webdoc.community.domain.entity.post;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
public class Post {

    protected Post(){}

    @Builder
    private Post(User user, String text, Community community){
        this.user = user;
        this.text = text;
        this.community = community;
    }

    public static Post CreatePost(User user, String text, Community community){
        return
                Post.builder()
                        .user(user)
                        .text(text)
                        .community(community)
                        .build();
    }


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

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "post",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Picture> pictures = new ArrayList<>();

    public void addPictures(Picture picture){
        picture.setPost(this);
        pictures.add(picture);
    }




}