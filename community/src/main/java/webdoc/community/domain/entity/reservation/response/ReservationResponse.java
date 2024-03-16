package webdoc.community.domain.entity.reservation.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import webdoc.community.domain.entity.user.Specialities;

import java.util.List;
/*
* 예약 응답 객체
 */
@Getter
@RequiredArgsConstructor
@Setter
public class ReservationResponse {
    private final Long id;

    private final String tuteeName;
    private final String tutorName;

    private final List<Specialities> tutorSpecialities;
    private final String lessonStartTime;

    private final String lessonEndTime;
    private final String tutorProfile;

    private final String memo;
}
