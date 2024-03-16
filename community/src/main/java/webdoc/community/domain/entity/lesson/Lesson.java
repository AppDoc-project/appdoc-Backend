package webdoc.community.domain.entity.lesson;

import jakarta.persistence.*;
import lombok.*;
import webdoc.community.domain.entity.feedback.Feedback;
import webdoc.community.domain.entity.lesson.enums.LessonStatus;
import webdoc.community.domain.entity.reservation.enums.LessonType;
import webdoc.community.domain.entity.review.Review;

import java.time.LocalDateTime;

/*
 * 레슨 도메인 객체
 */
@Entity
@Getter
@EqualsAndHashCode(of = "id")
public class Lesson {
    protected Lesson(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tutorId;

    @Column(nullable = false)
    private Long tuteeId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LessonStatus lessonStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LessonType lessonType;
    @JoinColumn
    @OneToOne(fetch = FetchType.LAZY)
    private Review review;

    @JoinColumn
    @OneToOne(fetch = FetchType.LAZY)
    private Feedback feedback;

    @Column
    private String channelName;

    @Column
    private String tuteeToken;

    @Column
    private String tutorToken;

    @Column(length = 300)
    private String memo;



    public void setFeedback(Feedback feedback){
        this.feedback = feedback;
    }
    public void setReview(Review review){
        this.review = review;
    }
    public void setChannelName(String channelName){this.channelName = channelName;}
    public void setTuteeToken(String token){this.tuteeToken=token;}
    public void setTutorToken(String token){this.tutorToken=token;}

    public void setLessonStatus(LessonStatus lessonStatus){
        this.lessonStatus = lessonStatus;
    }

    @Builder
    private Lesson(Long tutorId, Long tuteeId, LocalDateTime startTime, LocalDateTime endTime,
                   LessonStatus lessonStatus, LessonType lessonType, Review review, Feedback feedback,String memo){

        this.tuteeId = tuteeId;
        this.tutorId = tutorId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lessonStatus = lessonStatus;
        this.lessonType = lessonType;
        this.review = review;
        this.feedback = feedback;
        this.memo = memo;

    }

    public static Lesson createLesson(Long tutorId, Long tuteeId, LocalDateTime startTime, LocalDateTime endTime,
                                      LessonStatus lessonStatus, LessonType lessonType, Review review, Feedback feedback,String memo){
        return
                Lesson
                        .builder()
                        .tuteeId(tuteeId)
                        .tutorId(tutorId)
                        .startTime(startTime)
                        .endTime(endTime)
                        .lessonStatus(lessonStatus)
                        .lessonType(lessonType)
                        .review(review)
                        .feedback(feedback)
                        .memo(memo)
                        .build();

    }

}
