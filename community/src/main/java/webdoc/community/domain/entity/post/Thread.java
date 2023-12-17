package webdoc.community.domain.entity.post;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import webdoc.community.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

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
    private Thread(String text,Thread parent, Post post,Long userId){
        this.text = text;
        this.parent = parent;
        this.userId = userId;
        post.addThreads(this);
    }

    public static Thread createThread(String text,Post post,Long userId){
        return
                Thread.builder()
                        .text(text)
                        .post(post)
                        .userId(userId)
                        .build();
    }

    public void setThisAsParent(Thread childThread){
        childThread.setParent(this);
        childs.add(childThread);
    }
    @GeneratedValue
    @Id
    private Long id;
    @Column(nullable = false)
    private String text;
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "parent",orphanRemoval = true,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Thread> childs = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Thread parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private Long userId;
}
