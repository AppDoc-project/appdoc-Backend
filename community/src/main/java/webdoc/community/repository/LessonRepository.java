package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import webdoc.community.domain.entity.lesson.Lesson;
import webdoc.community.domain.entity.lesson.enums.LessonStatus;
import webdoc.community.domain.entity.reservation.Reservation;

import java.time.LocalDateTime;
import java.util.List;

/*
 * 레슨 repository
 */
@Repository

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // 튜터아이디로 겹치는 레슨이 존재하는 지 확인하는 로직
    @Query("select l from Lesson l where (l.tutorId=:tutorId and l.lessonStatus=:lessonStatus and (:endTime > l.startTime and :startTime < l.endTime))")
    List<Lesson> findByLessonStatusAndTutorIdWithCustom(LessonStatus lessonStatus, Long tutorId, LocalDateTime startTime, LocalDateTime endTime);

    // 튜티아이디로 겹치는 레슨이 존재하는 지 확인하는 로직
    @Query("select l from Lesson l where (l.tuteeId=:tuteeId and l.lessonStatus=:lessonStatus and (:endTime > l.startTime and :startTime < l.endTime))")
    List<Lesson> findByLessonStatusAndTuteeIdWithCustom(LessonStatus lessonStatus, Long tuteeId, LocalDateTime startTime, LocalDateTime endTime);

    // 종료된 레슨을 확인하는 로직
    @Query("select l from Lesson  l where l.endTime <=:localDateTime and l.lessonStatus=:lessonStatus")
    List<Lesson> findByEndTimeBeforeAndLessonStatus(LocalDateTime localDateTime, LessonStatus lessonStatus);

    // 튜터아이디와 레슨 종료로 레슨을 확인하는 로직
    List<Lesson> findByLessonStatusAndTutorId(LessonStatus lessonStatus, Long tutorId);

    // 튜티아이디와 레슨 종류로 레슨을 확인하는 로직
    List<Lesson> findByLessonStatusAndTuteeId(LessonStatus lessonStatus, Long tuteeId);

    // 레슨 종류와 유저 아이디로 레슨을 확인하는 로직
    @Query("select l from Lesson l where l.lessonStatus=:lessonStatus and (l.tutorId=:userId or l.tuteeId=:userId)")
    Lesson findByLessonStatusAndUserId(LessonStatus lessonStatus, Long userId);

    // 특정 년 월의 레슨을 확인하는 로직
    @Query("select l from Lesson l where (l.tuteeId =:userId or l.tutorId =:userId) and (year(l.startTime)=:y and month(l.startTime)=:m ) ")
    List<Lesson> findByUserIdAndYearMonth(Long userId, int y, int m);

    // 특정 시간 이후 레슨이 존재하는 지 확인하는 로직
    @Query("select l from Lesson l where (l.tutorId =:userId or l.tutorId=:userId) and (l.endTime>=:time)")
    List<Lesson> hasLessonAfterTime(LocalDateTime time, Long userId);
}
