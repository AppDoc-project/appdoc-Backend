package webdoc.community.domain.entity.community;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import webdoc.community.domain.BaseEntity;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
public class Community extends BaseEntity {

    protected Community(){}
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder
    private Community(Long id, String name){
        this.id = id;
        this.name = name;
    }

}
