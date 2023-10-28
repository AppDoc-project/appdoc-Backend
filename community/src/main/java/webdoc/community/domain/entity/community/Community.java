package webdoc.community.domain.entity.community;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import webdoc.community.domain.BaseEntity;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
public class Community extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

}
