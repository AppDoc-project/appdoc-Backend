package webdoc.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.lesson.Lesson;
import webdoc.community.domain.entity.lesson.enums.LessonStatus;
import webdoc.community.domain.entity.reservation.response.ReservationResponse;
import webdoc.community.domain.exceptions.TuteeAlreadyHasReservationException;
import webdoc.community.domain.exceptions.TutorAlreadyHasReservationException;
import webdoc.community.repository.LessonRepository;
import webdoc.community.repository.ReservationRepository;
import webdoc.community.domain.entity.reservation.Reservation;
import webdoc.community.domain.entity.reservation.request.ReservationCreateRequest;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.exceptions.UserNotExistException;
import webdoc.community.utility.LocalDateTimeToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/*
 * 예약 서비스
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;

    private final UserService userService;

    private final LessonRepository lessonRepository;

    @Value("${authentication.server}")
    private String authServer;


    // 예약을 등록하는 로직
    @Transactional
    public Reservation enrollReservation(ReservationCreateRequest createRequest, Long tutorId){

        Long tuteeId = createRequest.getTuteeId();
        // 양측 모두 해당 시간내에 예약이 있으면 예약 등록이 불가능하다
        // 튜티가 탈퇴한 회원이면 예외를 내뱉는다


        LocalDateTime startTime;
        LocalDateTime endTime;

        UserResponse userResponse = userService.fetchUserResponseFromAuthServer(authServer+"/server/user/id/" + createRequest.getTuteeId().toString(),10_000,10_000)
                .orElseThrow(()->new IllegalStateException("비정상적인 접근입니다"));

        if(userResponse.getNickName().equals("탈퇴회원")){
            throw new UserNotExistException("존재하지 않는 회원입니다");
        }

        if(userResponse.getIsTutor()){
            throw new IllegalStateException("비정상적인 접근입니다");
        }


        try{

            startTime = convertToLocalDateTime(createRequest.getStartTime(),createRequest.getDate());
            endTime = convertToLocalDateTime(createRequest.getEndTime(),createRequest.getDate());

            if(startTime.isEqual(endTime) || startTime.isAfter(endTime)){
                throw new IllegalArgumentException("바인딩 실패");
            }

            // 배포 시에는 15분 제한을 둬야함
//
//            if(startTime.minusMinutes(15L).isBefore(LocalDateTime.now())){
//                throw new IllegalArgumentException("바인딩 실패");
//            }


        }catch(Exception e){
            throw  new IllegalArgumentException("바인딩 실패");
        }


        // 튜티의 예약이 있는 지 확인한다
        List<Reservation> reservationsForTutor = reservationRepository
                .findByTutorIdAndStartTimeAndEndTimeWithCustomQuery(tutorId,startTime,endTime);

        // 튜터의 예약이 있는 지 확인한다
        List<Reservation> reservationsForTutee = reservationRepository
                .findByTuteeIdAndStartTimeAndEndTimeWithCustomQuery(tuteeId,startTime,endTime);

        if(reservationsForTutor.size() != 0){
            throw new TutorAlreadyHasReservationException("튜터의 예약이 차있습니다");
        }

        if(reservationsForTutee.size() != 0){
            throw new TuteeAlreadyHasReservationException("튜터의 예약이 차있습니다");
        }

        // 튜티의 레슨과 예약시간이 겹치는 지 확인한다
        List<Lesson> lessonsForTutee = lessonRepository.findByLessonStatusAndTuteeIdWithCustom(LessonStatus.ONGOING,tuteeId,startTime,endTime);
        if(lessonsForTutee.size() != 0){
            throw new TutorAlreadyHasReservationException("튜티가 레슨을 진행 중입니다");
        }

        // 튜터의 레슨과 예약시간이 겹치는 지 확인한다
        List<Lesson> lessonsForTutor = lessonRepository.findByLessonStatusAndTutorIdWithCustom(LessonStatus.ONGOING,tutorId,startTime,endTime);
        if(lessonsForTutor.size() != 0){
            throw new TutorAlreadyHasReservationException("튜티가 레슨을 진행 중입니다");
        }


        Reservation reservation = Reservation.createReservation(
                tuteeId,tutorId,startTime,endTime,createRequest.getLessonType(),createRequest.getMemo()
        );

        reservationRepository.save(reservation);

        return reservation;

    }

    // 내 id로 예약한 예약들을 가져온다
    public List<ReservationResponse> getMyReservations(Long userId,boolean isTutor){
        List<Reservation> reservations;
        if (isTutor){
            reservations = reservationRepository.findByTutorIdOrderByStartTime(userId);
        }else{
            reservations = reservationRepository.findByTuteeIdOrderByStartTime(userId);
        }

        return mapToReservationResponse(reservations);



    }
    // 특정 id의 예약을 취소한다
    @Transactional
    public void cancelReservation(Long reservationId, Long userId){

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                ()-> new IllegalStateException("비정상적인 접근입니다")
        );
        if (!reservation.getTutorId().equals(userId)){
            throw  new IllegalStateException("비정상적인 접근입니다");
        }

        reservationRepository.delete(reservation);
    }
    // 가장 최근의 예약을 확인한다
    public ReservationResponse fetchUpcomingReservation(Long userId){
        Reservation e = reservationRepository.finUpcommingReservation(userId);

        if(e == null) return null;

        Long tutorId = e.getTutorId();
        Long tuteeId = e.getTuteeId();

        UserResponse tutor = userService.fetchUserResponseFromAuthServer(
                authServer + "/server/user/id/" + tutorId,10_000,10_000
        ).orElseThrow(()->new IllegalStateException("비정상적인 접근"));

        UserResponse tutee = userService.fetchUserResponseFromAuthServer(
                authServer + "/server/user/id/" + tuteeId,10_000,10_000
        ).orElseThrow(()->new IllegalStateException("비정상적인 접근"));

        return new ReservationResponse(
                e.getId(), tutee.getNickName(), tutor.getNickName(),
                tutor.getSpecialities(), LocalDateTimeToString.convertToLocalDateTimeString(e.getStartTime())   ,
                LocalDateTimeToString.convertToLocalDateTimeString(e.getEndTime()),tutor.getProfile(), e.getMemo());
    }

    // 현재 예약이 존재하는 지 확인한다
    public boolean hasReservation(Long userId){
        Reservation reservation = reservationRepository.finUpcommingReservation(userId);
        return reservation != null;
    }


    private List<ReservationResponse> mapToReservationResponse(List<Reservation> reservations){
        return reservations.stream()
                .map(e->{

                    Long tutorId = e.getTutorId();
                    Long tuteeId = e.getTuteeId();

                    UserResponse tutor = userService.fetchUserResponseFromAuthServer(
                            authServer + "/server/user/id/" + tutorId,10_000,10_000
                    ).orElseThrow(()->new IllegalStateException("비정상적인 접근"));

                    UserResponse tutee = userService.fetchUserResponseFromAuthServer(
                            authServer + "/server/user/id/" + tuteeId,10_000,10_000
                    ).orElseThrow(()->new IllegalStateException("비정상적인 접근"));

                    return new ReservationResponse(
                        e.getId(), tutee.getNickName(), tutor.getNickName(),
                        tutor.getSpecialities(), LocalDateTimeToString.convertToLocalDateTimeString(e.getStartTime())   ,
                        LocalDateTimeToString.convertToLocalDateTimeString(e.getEndTime()),tutor.getProfile(), e.getMemo()
                    );

                }).collect(Collectors.toList());

    }

    private LocalDateTime convertToLocalDateTime(String time,String date){

        String[] dateParts = date.split(":");
        String[] timeParts = time.split(":");

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);


        return LocalDateTime.of(
                year,month,day,hour,minute
        );

    }

}
