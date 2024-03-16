package webdoc.authentication.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import webdoc.authentication.domain.BaseEntity;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;

import java.util.List;
import java.util.stream.Collectors;
/*
* 튜터 전공과목 도메인 객체
 */
@Entity
@Getter
public class TutorSpeciality extends BaseEntity {
    protected TutorSpeciality(){}

    public TutorSpeciality(Tutor tutor,Specialities specialities){
        this.tutor = tutor;
        this.specialities = specialities;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Tutor tutor;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Specialities specialities;

    public static List<TutorSpeciality> convertTutorSpeciality(List<Specialities> specialities, Tutor tutor){

        return specialities.stream().collect(Collectors.toSet()).stream()
                .map(s->new TutorSpeciality(tutor,s)).collect(Collectors.toList());
    }


}
