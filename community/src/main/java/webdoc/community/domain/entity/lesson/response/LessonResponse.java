package webdoc.community.domain.entity.lesson.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import webdoc.community.domain.entity.reservation.enums.LessonType;
import webdoc.community.domain.entity.user.Specialities;
import java.util.List;

/*
 * 레슨 응답 객체
 */
@Getter
@Setter
@RequiredArgsConstructor
public class LessonResponse {
    private final Long id;
    private final String tutorName;
    private final String tuteeName;
    private final String startTime;
    private final String endTime;
    private final List<Specialities> specialities;
    private final LessonType lessonType;

    private final String tutorProfile;

    private final String tuteeProfile;

    private final boolean feedbackYn;

    private final boolean reviewYn;

    private final String feedBack;

    private final String review;

    private final String memo;

    private final String feedbackTime;

    private final String reviewTime;

    private final Integer score;

    private String channelName;

    private String tutorToken;

    private String tuteeToken;


}
