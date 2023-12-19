package webdoc.community.domain.entity.post;

import jakarta.persistence.*;
import lombok.*;
import webdoc.community.domain.BaseEntity;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.like.Bookmark;
import webdoc.community.domain.entity.like.Like;
import webdoc.community.domain.entity.post.request.PostModifyRequest;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class Post extends BaseEntity {

    protected Post(){}

    @Builder
    private Post(Long userId, String title,String text, Community community){
        this.userId = userId;
        this.text = text;
        this.title = title;
        this.community = community;
    }

    public static Post CreatePost(Long userId,String title, String text, Community community){
        return
                Post.builder()
                        .userId(userId)
                        .text(text)
                        .title(title)
                        .community(community)
                        .build();
    }

    public void modifyPost(PostModifyRequest request){
        title = request.getTitle();
        text = request.getText();
    }
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    @Column(nullable = false, length = 3000)
    private String text;

    @Column(nullable = false)
    private Long view = 0L;
    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "community_id")
    private Community community;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "post",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Picture> pictures = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Thread> threads = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();



    public void addPictures(Picture picture){
        picture.setPost(this);
        pictures.add(picture);
    }

    public void addThreads(Thread thread){
        thread.setPost(this);
        threads.add(thread);
    }

    public void addBookmark(Bookmark bookmark){
        bookmark.setPost(this);
        bookmarks.add(bookmark);
    }

    public void addLikes(Like like){
        like.setPost(this);
        likes.add(like);
    }

    public void viewPlus(){
        this.view++;
    }




}