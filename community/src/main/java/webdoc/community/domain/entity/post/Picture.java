package webdoc.community.domain.entity.post;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.parameters.P;
import webdoc.community.domain.BaseEntity;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Picture extends BaseEntity {

    public static Picture createPicture(String address, Long priority){
        Picture picture = new Picture();
        picture.setAddress(address);
        picture.setPriority(priority);
        return picture;
    }
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "post_id")
    private Post post;

    @Column(nullable = false)
    private Long priority;


}
