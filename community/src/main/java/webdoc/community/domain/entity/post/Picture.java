package webdoc.community.domain.entity.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.parameters.P;
import webdoc.community.domain.BaseEntity;
/*
* 사진 도메인 객체
 */

@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class Picture extends BaseEntity {

    public static Picture createPicture(String address){
        Picture picture = new Picture();
        picture.setAddress(address);
        return picture;
    }

    public void setPost(Post post){
        this.post = post;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "post_id")
    private Post post;






}
