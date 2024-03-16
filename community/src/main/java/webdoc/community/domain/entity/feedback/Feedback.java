package webdoc.community.domain.entity.feedback;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import webdoc.community.domain.BaseEntity;

/*
 * 피드백 도메인 객체
 */
@Entity
@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class Feedback extends BaseEntity {
    protected Feedback(){}

    public static Feedback createFeedback(Long tuteeId, Long tutorId, String feedback){
        return Feedback
                .builder()
                .tutorId(tutorId)
                .tuteeId(tuteeId)
                .feedback(feedback)
                .build();

    }
    @Builder
    private Feedback(Long tuteeId,Long tutorId,String feedback){
        this.tuteeId = tuteeId;
        this.tutorId = tutorId;
        this.feedback = feedback;
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long tuteeId;

    @Column(nullable = false)
    private Long tutorId;

    @Column(nullable = false,length = 500)
    private String feedback;


}