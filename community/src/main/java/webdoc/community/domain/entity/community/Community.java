package webdoc.community.domain.entity.community;

import jakarta.persistence.*;
import lombok.*;
import webdoc.community.domain.BaseEntity;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Setter(value = AccessLevel.PRIVATE)
public class Community extends BaseEntity {

    protected Community(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder
    private Community(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public static Community createCommunity(String name){
        return
                Community
                        .builder()
                        .name(name)
                        .build();
    }

}
