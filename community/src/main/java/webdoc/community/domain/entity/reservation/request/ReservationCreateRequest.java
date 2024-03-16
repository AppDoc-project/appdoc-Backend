package webdoc.community.domain.entity.reservation.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import webdoc.community.domain.entity.reservation.enums.LessonType;
/*
* 예약 생성 객체
 */

@Getter
@Setter
public class ReservationCreateRequest {
    @NotNull
    private Long tuteeId;
    @NotNull
    private LessonType lessonType;

    @NotNull
    @Pattern(regexp = "^\\d{4}:\\d{2}:\\d{2}") // YYYY:MM:DD
    private String date;

    @NotNull
    @Pattern(regexp = "^\\d{2}:\\d{2}$") // HH:mm
    private String startTime;

    @NotNull
    @Pattern(regexp = "^\\d{2}:\\d{2}$") // HH:mm
    private String endTime;


    @Size(max = 1000)
    private String memo;

    @Builder
    private ReservationCreateRequest(Long tuteeId, LessonType lessonType, String startTime,
                                     String endTime,String date,String memo){
        this.tuteeId = tuteeId;
        this.lessonType = lessonType;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.memo = memo;
    }
}
