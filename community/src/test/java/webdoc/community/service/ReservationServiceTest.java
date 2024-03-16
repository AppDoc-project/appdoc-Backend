package webdoc.community.service;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.reservation.Reservation;
import webdoc.community.domain.entity.reservation.enums.LessonType;
import webdoc.community.domain.entity.reservation.request.ReservationCreateRequest;
import webdoc.community.domain.entity.reservation.response.ReservationResponse;
import webdoc.community.domain.entity.user.Specialities;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.exceptions.TuteeAlreadyHasReservationException;
import webdoc.community.domain.exceptions.TutorAlreadyHasReservationException;
import webdoc.community.domain.exceptions.UserNotExistException;
import webdoc.community.repository.ReservationRepository;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReservationServiceTest {
    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @MockBean
    UserService userService;


    @DisplayName("예약을 할 수 있다")
    @Test
    void makeAReservation(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"우석우"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "03:30",LessonType.REMOTE,
                        "02:30",2L,"2024:10:04"
                );


        //when
        Reservation reservation = reservationService.enrollReservation(
                request,1L
        );


        //then
        Reservation foundReservation = reservationRepository.findById(reservation.getId())
                .orElse(null);
        assertThat(foundReservation).isNotNull();
        assertThat(foundReservation).isEqualTo(reservation);


     }

    @DisplayName("탈퇴한 회원을 상대로 예약할 수 없다")
    @Test
    void makeAReservationWithSignedOut(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"탈퇴회원"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "03:30",LessonType.REMOTE,
                        "02:30",2L,"2024:10:04"
                );


        //when + then
        assertThatThrownBy(()-> reservationService.enrollReservation(
                request,1L
        )).isInstanceOf(UserNotExistException.class);

    }

    @DisplayName("적절한 시간을 표시하지 않으면 예약할 수 없다")
//    @Test
    void makeAReservationWithInvaildTime(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"김민수"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "25:30",LessonType.REMOTE,
                        "24:30",2L,"2024:10:04"
                );

        ReservationCreateRequest request2 =
                reservationCreateRequest(
                        "23:30",LessonType.REMOTE,
                        "22:29",2L,"2024:10:04"
                );

        ReservationCreateRequest request3 =
                reservationCreateRequest(
                        "23:39",LessonType.REMOTE,
                        "22:30",2L,"2024:10:04"
                );


        //when + then
        assertThatThrownBy(()-> reservationService.enrollReservation(
                request,1L
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(()-> reservationService.enrollReservation(
                request2,1L
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(()-> reservationService.enrollReservation(
                request3,1L
        )).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("현재 보다 지난 예약을 할 수 없다")
//    @Test
    void makeAReservationWithPast(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"김민수"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "13:30",LessonType.REMOTE,
                        "12:30",2L,"2024:02:01"
                );


        //when + then
        assertThatThrownBy(()-> reservationService.enrollReservation(
                request,1L
        )).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("시작 시간이 15분도 안남은 예약을 할 수 없다")
//    @Test
    void makeAReservationWith15MinutesLeft(){
        //given M
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"김민수"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "13:30",LessonType.REMOTE,
                        "11:30",2L,"2024:02:04"
                );



        //when + then
        assertThatThrownBy(()-> reservationService.enrollReservation(
                request,1L
        )).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("튜터와 시간 예약이 겹치면 예약할 수 없다")
    @Test
    void makeAReservationWithDuplicatedTutor(){
        //given M
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"김민수"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "13:30",LessonType.REMOTE,
                        "11:30",2L,"2024:05:25"
                );

        ReservationCreateRequest request2 =
                reservationCreateRequest(
                        "13:30",LessonType.REMOTE,
                        "10:30",2L,"2024:05:25"
                );

        ReservationCreateRequest request3 =
                reservationCreateRequest(
                        "12:00",LessonType.REMOTE,
                        "10:30",2L,"2024:05:25"
                );
        ReservationCreateRequest request4 =
                reservationCreateRequest(
                        "14:00",LessonType.REMOTE,
                        "13:30",2L,"2024:05:25"
                );

        ReservationCreateRequest request5 =
                reservationCreateRequest(
                        "11:30",LessonType.REMOTE,
                        "10:30",2L,"2024:05:25"
                );
        ReservationCreateRequest request6 =
                reservationCreateRequest(
                        "12:00",LessonType.REMOTE,
                        "11:30",2L,"2024:05:25"
                );

        Reservation reservation = reservationService.enrollReservation(
                request,1L
        );




        //when + then
        assertThatThrownBy(()->reservationService.enrollReservation(
                request2,1L
        )).isInstanceOf(TutorAlreadyHasReservationException.class);

        assertThatThrownBy(()->reservationService.enrollReservation(
                request3,1L
        )).isInstanceOf(TutorAlreadyHasReservationException.class);

        assertThatThrownBy(()->reservationService.enrollReservation(
                request6,1L
        )).isInstanceOf(TutorAlreadyHasReservationException.class);


        Reservation reservation2 = reservationService.enrollReservation(
                request4,1L
        );

        Reservation reservation3 = reservationService.enrollReservation(
                request5,1L
        );




    }

    @DisplayName("튜티와 시간 예약이 겹치면 예약할 수 없다")
    @Test
    void makeAReservationWithDuplicatedTutee(){
        //given M
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"김민수"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "13:30",LessonType.REMOTE,
                        "11:30",2L,"2024:05:25"
                );

        ReservationCreateRequest request2 =
                reservationCreateRequest(
                        "13:30",LessonType.REMOTE,
                        "10:30",2L,"2024:05:25"
                );

        ReservationCreateRequest request3 =
                reservationCreateRequest(
                        "12:00",LessonType.REMOTE,
                        "10:30",2L,"2024:05:25"
                );
        ReservationCreateRequest request4 =
                reservationCreateRequest(
                        "14:00",LessonType.REMOTE,
                        "13:30",2L,"2024:05:25"
                );

        ReservationCreateRequest request5 =
                reservationCreateRequest(
                        "11:30",LessonType.REMOTE,
                        "10:30",2L,"2024:05:25"
                );
        ReservationCreateRequest request6 =
                reservationCreateRequest(
                        "12:00",LessonType.REMOTE,
                        "11:30",2L,"2024:05:25"
                );

        Reservation reservation = reservationService.enrollReservation(
                request,1L
        );




        //when + then
        assertThatThrownBy(()->reservationService.enrollReservation(
                request2,2L
        )).isInstanceOf(TuteeAlreadyHasReservationException.class);

        assertThatThrownBy(()->reservationService.enrollReservation(
                request3,3L
        )).isInstanceOf(TuteeAlreadyHasReservationException.class);

        assertThatThrownBy(()->reservationService.enrollReservation(
                request6,4L
        )).isInstanceOf(TuteeAlreadyHasReservationException.class);


        Reservation reservation2 = reservationService.enrollReservation(
                request4,5L
        );

        Reservation reservation3 = reservationService.enrollReservation(
                request5,6L
        );




    }

    @DisplayName("튜티는 예약 목록을 가져올 수 있다")
    @Test
    void tuteeFetchReservations(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"우석우"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "03:30",LessonType.REMOTE,
                        "02:30",2L,"2024:10:04"
                );

        ReservationCreateRequest request2 =
                reservationCreateRequest(
                        "04:30",LessonType.REMOTE,
                        "03:30",2L,"2024:10:04"
                );

        reservationService.enrollReservation(
                request,1L
        );
        reservationService.enrollReservation(
                request2,1L
        );

        List<ReservationResponse> reservations = reservationService.getMyReservations(2L,false);
        assertThat(reservations.size()).isEqualTo(2);



    }

    @DisplayName("튜터는 예약 목록을 가져올 수 있다")
    @Test
    void tutorFetchReservations(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"우석우"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "03:30",LessonType.REMOTE,
                        "02:30",2L,"2024:10:04"
                );

        ReservationCreateRequest request2 =
                reservationCreateRequest(
                        "04:30",LessonType.REMOTE,
                        "03:30",2L,"2024:10:04"
                );

        ReservationCreateRequest request3 =
                reservationCreateRequest(
                        "05:30",LessonType.REMOTE,
                        "04:30",2L,"2024:10:04"
                );

        reservationService.enrollReservation(
                request,1L
        );
        reservationService.enrollReservation(
                request2,1L
        );
        reservationService.enrollReservation(
                request3,3L
        );

        List<ReservationResponse> reservations = reservationService.getMyReservations(1L,true);
        assertThat(reservations.size()).isEqualTo(2);



    }


    @DisplayName("예약을 취소한다")
    @Test
    void cancelReservation(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"우석우"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "03:30",LessonType.REMOTE,
                        "02:30",2L,"2024:10:04"
                );

        Reservation reservation = reservationService.enrollReservation(
                request,1L
        );

        //when
        reservationService.cancelReservation(reservation.getId(),1L);

        //then
        Reservation foundReservation = reservationRepository.findById(reservation.getId())
                .orElse(null);
        assertThat(foundReservation).isNull();

    }

    @DisplayName("튜티는 예약을 취소할 수 없다")
    @Test
    void tuteeCancelReservation(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false,"우석우"))
                );

        ReservationCreateRequest request =
                reservationCreateRequest(
                        "03:30",LessonType.REMOTE,
                        "02:30",2L,"2024:10:04"
                );

        Reservation reservation = reservationService.enrollReservation(
                request,1L
        );

        //when + then
        assertThatThrownBy(()->reservationService.cancelReservation(reservation.getId(),2L))
                .isInstanceOf(IllegalStateException.class);

    }



    private ReservationCreateRequest reservationCreateRequest(
            String endTime, LessonType lessonType,String startTime,
            Long tuteeId,String date
            ){
        return
                ReservationCreateRequest
                        .builder()
                        .endTime(endTime)
                        .lessonType(lessonType)
                        .startTime(startTime)
                        .tuteeId(tuteeId)
                        .date(date)
                        .build();
    }
    private UserResponse createUser(boolean isTutor,String nickName){
        return UserResponse.builder()
                .contact("01025045779")
                .email("1dilumn0@gmail.com")
                .id(2L)
                .isTutor(isTutor)
                .nickName(nickName)
                .name(nickName)
                .profile("naver.com")
                .specialities(List.of(Specialities.KEYBOARD_INSTRUMENT))
                .build();
    }
}
