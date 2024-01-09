package webdoc.authentication.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import webdoc.authentication.domain.BaseEntity;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
public class SpecialityChange extends BaseEntity {
    protected SpecialityChange(){}

    public SpecialityChange(Tutor tutor, Specialities specialities){
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

    public static List<SpecialityChange> convertTutorSpeciality(List<Specialities> specialities, Tutor tutor){
        return specialities.stream()
                .map(s->new SpecialityChange(tutor,s)).collect(Collectors.toList());
    }
}
