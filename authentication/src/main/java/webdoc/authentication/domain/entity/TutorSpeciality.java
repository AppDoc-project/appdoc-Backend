package webdoc.authentication.domain.entity;

import jakarta.persistence.*;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;

import java.util.List;
import java.util.stream.Collectors;

@Entity
public class TutorSpeciality {
    protected TutorSpeciality(){}

    public TutorSpeciality(Tutor tutor,Specialities specialities){
        this.tutor = tutor;
        this.specialities = specialities;
    }
    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Tutor tutor;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Specialities specialities;

    public static List<TutorSpeciality> convertTutorSpeciality(List<Specialities> specialities, Tutor tutor){
        return specialities.stream()
                .map(s->new TutorSpeciality(tutor,s)).collect(Collectors.toList());
    }


}
