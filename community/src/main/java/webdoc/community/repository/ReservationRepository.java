package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webdoc.community.domain.entity.reservation.Reservation;

import java.time.LocalDateTime;
import java.util.List;
/*
 * 예약 repository
 */
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    // 예약 중복확인 로직
    @Query("select r from Reservation r where (r.tutorId=:tutorId and (:endTime > r.startTime and :startTime < r.endTime))")
    List<Reservation> findByTutorIdAndStartTimeAndEndTimeWithCustomQuery(Long tutorId, LocalDateTime startTime, LocalDateTime endTime);

    // 예약 중복 확인 로직
    @Query("select r from Reservation r where (r.tuteeId=:tuteeId and (:endTime > r.startTime and :startTime < r.endTime))")
    List<Reservation> findByTuteeIdAndStartTimeAndEndTimeWithCustomQuery(Long tuteeId, LocalDateTime startTime, LocalDateTime endTime);
    List<Reservation> findByTutorIdOrderByStartTime(Long tutorId);
    List<Reservation> findByTuteeIdOrderByStartTime(Long tuteeId);

    // 시작할 예약을 가져오는 로직
    @Query("select r from Reservation r where (r.startTime <= :localDateTime)")
    List<Reservation> findByStartTimeBefore(LocalDateTime localDateTime);

    // 가장 임박한 예약을 가져오는 로직
    @Query("select r from Reservation r where (r.tuteeId=:userId or r.tutorId=:userId) order by r.startTime limit 1")
    Reservation finUpcommingReservation(Long userId);



}
