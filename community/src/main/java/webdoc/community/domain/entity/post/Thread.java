package webdoc.community.domain.entity.post;

import jakarta.persistence.*;
import lombok.*;
import webdoc.community.domain.BaseEntity;
import webdoc.community.domain.entity.user.User;

@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class Thread extends BaseEntity {
    protected Thread(){}
    public void setPost(Post post){
        this.post = post;
    }
    @Builder
    private Thread(String text,Thread parent, Post post,User user){
        this.text = text;
        this.parent = parent;
        this.post = post;
        this.user = user;

    }

    public static Thread createThread(String text,Post post,User user){
        return
                Thread.builder()
                        .text(text)
                        .post(post)
                        .user(user)
                        .build();

    }

    public void setThisAsParent(Thread childThread){
        childThread.setParent(this);
    }
    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    private String text;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Thread parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
