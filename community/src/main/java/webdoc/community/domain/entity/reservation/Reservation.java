package webdoc.community.domain.entity.reservation;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import webdoc.community.domain.entity.reservation.enums.LessonType;
import java.time.LocalDateTime;
/*
* 예약 도메인 객체
 */
@Entity
@EqualsAndHashCode(of = "id")
@Getter
public class Reservation {
    protected Reservation(){}

    public static Reservation createReservation(Long tuteeId, Long tutorId,LocalDateTime startTime,
                                                LocalDateTime endTime, LessonType lessonType,String memo){
        return Reservation
                .builder()
                .tutorId(tutorId)
                .tuteeId(tuteeId)
                .startTime(startTime)
                .endTime(endTime)
                .memo(memo)
                .lessonType(lessonType)
                .build();

    }
    @Builder
    private Reservation (Long tuteeId, Long tutorId, LocalDateTime startTime
            ,LocalDateTime endTime, LessonType lessonType,String memo){
        this.tuteeId = tuteeId;
        this.tutorId = tutorId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lessonType = lessonType;
        this.memo = memo;

    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long tuteeId;

    @Column(nullable = false)
    private Long tutorId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LessonType lessonType;

    @Column(length = 300)
    private String memo;
}
