package webdoc.community.domain.entity.post;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import webdoc.community.domain.BaseEntity;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
public class Picture extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "post_id")
    private Post post;
}
