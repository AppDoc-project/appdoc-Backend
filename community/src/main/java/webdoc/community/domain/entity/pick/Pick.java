package webdoc.community.domain.entity.pick;

import jakarta.persistence.*;
import lombok.*;
import webdoc.community.domain.BaseEntity;

/*
* 강사 찜 도메인 객체
 */
@Entity
@EqualsAndHashCode(of = "id")
@Getter
public class Pick extends BaseEntity {
    protected Pick(){}

    public static Pick createPick(Long tuteeId, Long tutorId){
        return Pick
                .builder()
                .tutorId(tutorId)
                .tuteeId(tuteeId)
                .build();

    }
    @Builder
    private Pick(Long tuteeId,Long tutorId){
        this.tuteeId = tuteeId;
        this.tutorId = tutorId;
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long tuteeId;

    @Column(nullable = false)
    private Long tutorId;



}