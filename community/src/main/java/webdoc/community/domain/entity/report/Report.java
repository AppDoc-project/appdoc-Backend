package webdoc.community.domain.entity.report;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import webdoc.community.domain.BaseEntity;

@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@DiscriminatorColumn(name="dtype")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Report extends BaseEntity {
    protected Report(){}

    protected Report(Long userId,String reason){
        this.userId = userId;
        this.reason = reason;
    }
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private String reason;


}
