package webdoc.authentication.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.UserMail;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.TutorMail;
import webdoc.authentication.domain.entity.user.tutor.request.TutorCreateRequest;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.domain.entity.user.tutee.TuteeMail;
import webdoc.authentication.domain.entity.user.tutee.request.TuteeCreateRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.exceptions.EmailDuplicationException;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserMailRepository;
import webdoc.authentication.repository.UserRepository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthService authService;

    @Autowired
    UserMailRepository userMailRepository;

    @MockBean
    EmailService emailService;

    @DisplayName("튜터 회원가입을 테스트 한다")
    @Test
    void createTutorUserTest() throws MessagingException {
        //given
        TutorCreateRequest dto =
                tutorCreateRequest();

        //when
        authService.createTutorUser(dto);
        TutorMail userMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TutorMail)e).findAny().get();

        //then
        assertThat(userMail).isNotNull();
        assertThat(userMail)
                .extracting("authenticationAddress", "contact", "email"
                      ,"specialities", "password", "selfDescription", "name"
                )
                .contains(
                        dto.getAuthenticationAddress(), dto.getContact(),
                        dto.getEmail(), Specialities.enumToString(dto.getSpecialities()),
                        dto.getPassword(), dto.getSelfDescription(),
                        dto.getName()
                );
     }

    @DisplayName("튜터 이메일 인증 번호가 올바르고 3분이내의 경우를 테스트 한다")
    @Test
    void validateTutorSuccessTest() throws MessagingException {
        //given
        TutorCreateRequest dto =
                tutorCreateRequest();

        authService.createTutorUser(dto);

        TutorMail tutorMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TutorMail)e).findAny().get();


        //when
        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()), LocalDateTime.now());

        //then
        Tutor tutor = (Tutor) userRepository
                .findByEmail(dto.getEmail()).orElseThrow(
                        ()->
                            new IllegalStateException("해당하는 유저가 없습니다")
                );
        UserMail userMail = userMailRepository.findByEmail(tutor.getEmail()).orElse(null);
        assertThat(userMail).isNull();
        assertThat(tutor.getSpecialities()).hasSize(2);


     }

     @DisplayName("튜터 인증번호가 틀린경우를 테스트한다")
     @Test
     void validateTutorWithWrongCode() throws MessagingException {
         //given
         TutorCreateRequest dto =
                tutorCreateRequest();

         authService.createTutorUser(dto);

         TutorMail tutorMail = userMailRepository.findByEmail(dto.getEmail())
                 .stream().map(e->(TutorMail)e).findAny().get();


         //when and then
         assertThatThrownBy(()->{
             authService.validateTutor(new CodeRequest(tutorMail.getEmail(),"21321413"), LocalDateTime.now());
         }).isInstanceOf(AuthenticationServiceException.class);

         User user = userRepository
                 .findByEmail(dto.getEmail()).orElse(null);

         assertThat(user).isNull();;



     }

    @DisplayName("튜터 인증번호가 시간이 지난 경우를 테스트한다")
    @Test
    void validateTutorTimeOut() throws MessagingException {
        //given
        TutorCreateRequest dto =
              tutorCreateRequest();

        authService.createTutorUser(dto);

        TutorMail tutorMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TutorMail)e).findAny().get();


        //when and then
        assertThatThrownBy(()->{
            authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()), LocalDateTime.now().plusMinutes(4L));
        }).isInstanceOf(TimeOutException.class).hasMessage("인증 시간을 초과하였습니다");

        User user = userRepository
                .findByEmail(dto.getEmail()).orElse(null);

        assertThat(user).isNull();;


    }

    @DisplayName("이메일이 중복된 튜터 회원가입을 테스트한다")
    @Test
    void createDuplicateTutor() throws MessagingException {
        //given
        TutorCreateRequest dto =
                tutorCreateRequest();

        TutorCreateRequest dto2 =
                tutorCreateRequest();

        authService.createTutorUser(dto);

        TutorMail tutorMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TutorMail)e).findAny().get();

        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()), LocalDateTime.now());

        //when and then
        assertThatThrownBy(()->{
            authService.createTutorUser(dto2);
        }).isInstanceOf(EmailDuplicationException.class)
                .hasMessage("해당 이메일을 가진 유저가 존재합니다");

     }

     @DisplayName("회원가입을 하지 않은 튜터회원의 메일을 인증한다")
     @Test
     void validateTutorNotRegistered(){

         assertThatThrownBy(
                 ()->{
                     authService.validateTutor(new CodeRequest("1sdad@naver.com","1123"), LocalDateTime.now());
                 }
         ).isInstanceOf(NoSuchElementException.class).hasMessage("id에 해당하는 회원이 없습니다");

      }


    @DisplayName("튜티 회원가입을 테스트 한다")
    @Test
    void createTuteeTest() throws MessagingException {
        //given
        TuteeCreateRequest dto =
                tuteeCreateRequest();

        //when
        authService.createTuteeUser(dto);
        TuteeMail userMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TuteeMail)e).findAny().get();

        //then
        assertThat(userMail).isNotNull();
        assertThat(userMail)
                .extracting(
                        "contact","email","name","password"
                )
                .contains(
                       dto.getContact(),dto.getEmail(),dto.getName(),
                        dto.getPassword()
                );
    }

    @DisplayName("튜티 이메일 인증 번호가 올바르고 3분이내의 경우를 테스트 한다")
    @Test
    void validateTuteeSuccessTest() throws MessagingException {
        //given
        TuteeCreateRequest dto =
                tuteeCreateRequest();

        authService.createTuteeUser(dto);

        TuteeMail tuteeMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TuteeMail)e).findAny().get();


        //when
        authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()), LocalDateTime.now());


        //then
        User user = userRepository
                .findByEmail(tuteeMail.getEmail()).orElseThrow(
                        ()->
                                new IllegalStateException("해당하는 유저가 없습니다")
                );

        UserMail userMail = userMailRepository.findByEmail(user.getEmail()).orElse(null);
        assertThat(userMail).isNull();


    }

    @DisplayName("튜티 인증번호가 틀린경우를 테스트한다")
    @Test
    void validateTuteeWithWrongCode() throws MessagingException {
        //given
        TuteeCreateRequest dto =
               tuteeCreateRequest();

        authService.createTuteeUser(dto);

        TuteeMail tuteeMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TuteeMail)e).findAny().get();


        //when and then
        assertThatThrownBy(()->{
            authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),"21321413"), LocalDateTime.now());
        }).isInstanceOf(AuthenticationServiceException.class);

        User user = userRepository
                .findByEmail(dto.getEmail()).orElse(null);

        assertThat(user).isNull();;



    }

    @DisplayName("튜티 인증번호가 시간이 지난 경우를 테스트한다")
    @Test
    void validateTuteeTimeOut() throws MessagingException {
        //given
        TuteeCreateRequest dto =
                tuteeCreateRequest();

        authService.createTuteeUser(dto);

        TuteeMail tuteeMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TuteeMail)e).findAny().get();


        //when and then
        assertThatThrownBy(()->{
            authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()), LocalDateTime.now().plusMinutes(4L));
        }).isInstanceOf(TimeOutException.class).hasMessage("인증 시간을 초과하였습니다");

        User user = userRepository
                .findByEmail(dto.getEmail()).orElse(null);

        assertThat(user).isNull();;


    }

    @DisplayName("이메일이 중복된 튜티 회원가입을 테스트한다")
    @Test
    void createDuplicateTutee() throws MessagingException {
        //given
        TutorCreateRequest dto =
                tutorCreateRequest();

        TuteeCreateRequest dto2 =
                tuteeCreateRequest();

        authService.createTutorUser(dto);

        TutorMail tutorMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TutorMail)e).findAny().get();

        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()), LocalDateTime.now());

        //when and then
        assertThatThrownBy(()->{
            authService.createTuteeUser(dto2);
        }).isInstanceOf(EmailDuplicationException.class)
                .hasMessage("해당 이메일을 가진 유저가 존재합니다");

    }


    @DisplayName("회원가입을 하지 않은 튜티의 메일을 인증한다")
    @Test
    void validateTuteeNotRegistered(){

        assertThatThrownBy(
                ()->{
                    authService.validateTutee(new CodeRequest("1sdad@naver.com","1123"), LocalDateTime.now());
                }
        ).isInstanceOf(NoSuchElementException.class).hasMessage("id에 해당하는 회원이 없습니다");

    }








    private TutorCreateRequest tutorCreateRequest(){
        return TutorCreateRequest
                .builder()
                .authenticationAddress("http://localhost:8080")
                .contact("01025045779")
                .email("1dilumn0@gmail.com")
                .specialities(List.of(Specialities.KEYBOARD_INSTRUMENT,Specialities.BASS))
                .password("dntjrdn78")
                .selfDescription("좋은 튜터입니다")
                .name("우석우")
                .build();
    }

    private TuteeCreateRequest tuteeCreateRequest(){
        return TuteeCreateRequest
                .builder()
                .contact("01025045779")
                .email("1dilumn0@gmail.com")
                .name("우석우")
                .password("dntjrdn78")
                .build();
    }
}