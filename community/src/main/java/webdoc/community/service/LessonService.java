package webdoc.community.service;


import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.feedback.Feedback;
import webdoc.community.domain.entity.lesson.Lesson;
import webdoc.community.domain.entity.lesson.enums.LessonStatus;
import webdoc.community.domain.entity.lesson.response.LessonResponse;
import webdoc.community.domain.entity.reservation.Reservation;
import webdoc.community.domain.entity.reservation.enums.LessonType;
import webdoc.community.domain.entity.review.Review;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.repository.FeedbackRepository;
import webdoc.community.repository.LessonRepository;
import webdoc.community.repository.ReservationRepository;
import webdoc.community.repository.ReviewRepository;
import webdoc.community.utility.LocalDateTimeToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
* 레슨 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    private final AgoraService agoraService;

    private final RabbitService rabbitService;

    private final LessonRepository lessonRepository;

    private final ReservationRepository reservationRepository;

    private final UserService userService;

    private final ReviewRepository reviewRepository;

    private final FeedbackRepository feedbackRepository;

    private final EmailService emailService;

    @Value("${authentication.server}")
    private String authServer;


    // 예약을 레슨으로 변경하는 스케쥴링 로직
    /*
     *  메일 서비스를 통한 알림 서비스 구현!!
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void openUpLessons(){

        List<Reservation> reservations = reservationRepository
                .findByStartTimeBefore(LocalDateTime.now().plusMinutes(1));


        List<Lesson> lessons = reservations.stream()
                .map(l -> {
                    reservationRepository.delete(l);
                    Lesson lesson = Lesson.createLesson(
                            l.getTutorId(), l.getTuteeId(),
                            l.getStartTime(), l.getEndTime(),
                            LessonStatus.ONGOING, l.getLessonType(),
                            null, null, l.getMemo()
                    );

                    lessonRepository.save(lesson);
                    if(l.getLessonType().equals(LessonType.REMOTE)){
                        agoraService.createChannel(lesson, lesson.getTutorId(), lesson.getTuteeId());
                        // rabbit queue에 오픈 사실 전달
                        rabbitService.sendOpen(lesson.getId(), lesson.getTutorId(),lesson.getTuteeId());
                    }
                    UserResponse tutor = userService.fetchUserResponseFromAuthServer(
                            authServer+"/server/user/id/"+lesson.getTutorId(),10_000,10_1000
                    ).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));

                    UserResponse tutee = userService.fetchUserResponseFromAuthServer(
                            authServer+"/server/user/id/"+lesson.getTutorId(),10_000,10_1000
                    ).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));

                    try {
                        emailService.sendEmail(tutor.getEmail(),"레슨이 시작되었습니다!","BEATMATE 레슨 알림");
                        emailService.sendEmail(tutee.getEmail(),"레슨이 시작되었습니다!","BEATMATE 레슨 알림");
                    } catch (MessagingException e) {

                    }

                    return lesson;

                }).toList();

    }

    // cron job :: 레슨을 종료 상태로 변경하는 로직
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void closeLessons() {

        List<Lesson> lessons = lessonRepository.findByEndTimeBeforeAndLessonStatus(
                LocalDateTime.now(), LessonStatus.ONGOING
        );

        lessons
                .forEach(l -> {
                    l.setLessonStatus(LessonStatus.ENDED);
                    if (l.getLessonType().equals(LessonType.REMOTE)){
                        // 메세지 큐를 통해서 종료를 알린다
                        rabbitService.sendClose(
                                l.getId(),l.getTutorId(),l.getTuteeId(),"레슨이 종료되었습니다"
                        );
                    }


                });

    }

    // 강사 피드백 미작성 레슨 리스트

    public List<LessonResponse> getNoFeedbackByTutor(Long tutorId) {

        return
                lessonRepository.findByLessonStatusAndTutorId(LessonStatus.ENDED,
                                tutorId)
                        .stream().map(this::mapToResponse).filter(s -> !s.isFeedbackYn()).collect(Collectors.toList());
    }

    // 튜티 리뷰 미작성 레슨 리스트
    public List<LessonResponse> getNoReviewByTutee(Long tuteeId) {
        return
                lessonRepository.findByLessonStatusAndTuteeId(LessonStatus.ENDED,
                                tuteeId)
                        .stream().map(this::mapToResponse).filter(s -> !s.isReviewYn()).collect(Collectors.toList());
    }
    // 연 월을 기준으로 레슨 가져오기

    public List<LessonResponse> fetchLessonByYearAndMonth(int year, int month, long userId) {
        return
                lessonRepository.findByUserIdAndYearMonth(
                        userId, year, month
                ).stream().filter(e -> e.getLessonStatus().equals(LessonStatus.ENDED)).map(this::mapToResponse).collect(Collectors.toList());

    }

    public LessonResponse fetchOnGoingLesson(Long userId) {

        Lesson lesson = lessonRepository.findByLessonStatusAndUserId(LessonStatus.ONGOING, userId);
        if (lesson == null) {
            return null;
        }

        return mapToResponse(lesson);
    }

    // 리뷰 작성
    @Transactional
    public Review WriteReview(Long lessonId, Long userId, String content, int score) {

        Review review;
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalStateException("비정상적인 접근입니다"));

        if (!lesson.getTuteeId().equals(userId)) {
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        if (lesson.getReview() != null) {
            review = lesson.getReview();

            review.setReview(content);
            review.setScore(score);

        } else {
            review = Review.createReview(
                    lesson.getTuteeId(), lesson.getTutorId(),
                    content, score
            );

            reviewRepository.save(review);
            lesson.setReview(review);

        }

        return review;

    }

    // 피드백 작성

    @Transactional
    public Feedback WriteFeedback(Long lessonId, Long userId, String content) {

        Feedback feedback;
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalStateException("비정상적인 접근입니다"));

        if (!lesson.getTutorId().equals(userId)) {
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        if (lesson.getFeedback() != null) {
            feedback = lesson.getFeedback();
            feedback.setFeedback(content);

        } else {
            feedback = Feedback.createFeedback(
                    lesson.getTuteeId(), lesson.getTutorId(),
                    content
            );

            feedbackRepository.save(feedback);
            lesson.setFeedback(feedback);

        }

        return feedback;

    }

    // 레슨 종료
    @Transactional
    public void endLesson(Long lessonId, Long tutorId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalStateException("비정상적인 접근입니다"));

        if (!lesson.getTutorId().equals(tutorId)) {
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        lesson.setLessonStatus(LessonStatus.ENDED);
        rabbitService.sendClose(
                lesson.getId(),lesson.getTutorId(),lesson.getTuteeId(),"레슨이 종료되었습니다"
        );


    }

    // 특정 회원 레슨 존재 여부
    public boolean hasLesson(Long userId, LocalDateTime localDateTime) {
        List<Lesson> lessons = lessonRepository.hasLessonAfterTime(localDateTime, userId);
        return lessons.size() != 0;
    }

    // 비대면 레슨 정보를 가져온다
    public LessonResponse fetchRemoteLesson(Long lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).filter(e->e.getLessonType().equals(LessonType.REMOTE))
                .orElseThrow(()->new IllegalStateException("비정상적인 접근입니다"));

        LessonResponse response = mapToResponse(lesson);

        response.setTutorToken(lesson.getTutorToken());
        response.setTuteeToken(lesson.getTuteeToken());
        response.setChannelName(lesson.getChannelName());

        return response;

    }

    // 도메인 객체를 응답 객체로 변환하는 함수

    private LessonResponse mapToResponse(Lesson s) {

        UserResponse tutor = userService.fetchUserResponseFromAuthServer(
                authServer + "/server/user/id/" + s.getTutorId(), 10_000, 10_000
        ).orElseThrow(() -> new IllegalStateException("비정상적인 접근"));

        UserResponse tutee = userService.fetchUserResponseFromAuthServer(
                authServer + "/server/user/id/" + s.getTuteeId(), 10_000, 10_000
        ).orElseThrow(() -> new IllegalStateException("비정상적인 접근"));

        return
                new LessonResponse(
                        s.getId(), tutor.getNickName(), tutee.getNickName(),
                        LocalDateTimeToString.convertToLocalDateTimeString(
                                s.getStartTime()
                        ),
                        LocalDateTimeToString.convertToLocalDateTimeString(
                                s.getEndTime()
                        ),
                        tutor.getSpecialities(),
                        s.getLessonType(),
                        tutor.getProfile(), tutee.getProfile(),
                        s.getFeedback() != null,
                        s.getReview() != null,
                        s.getFeedback() == null ? null : s.getFeedback().getFeedback(),
                        s.getReview() == null ? null : s.getReview().getReview(),
                        s.getMemo(),
                        s.getFeedback() == null ? null : LocalDateTimeToString.convertToLocalDateTimeString(s.getFeedback().getCreatedAt()),
                        s.getReview() == null ? null : LocalDateTimeToString.convertToLocalDateTimeString(s.getReview().getCreatedAt()),
                        s.getReview() == null ? null : s.getReview().getScore()
                );


    }


}
