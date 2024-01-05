package webdoc.authentication.service;

import jakarta.mail.MessagingException;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.entity.user.tutee.Tutee;
import webdoc.authentication.domain.entity.user.tutee.TuteeMail;
import webdoc.authentication.domain.entity.user.tutee.request.TuteeCreateRequest;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.TutorMail;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.domain.entity.user.tutor.request.TutorCreateRequest;
import webdoc.authentication.domain.exceptions.WrongPasswordException;
import webdoc.authentication.repository.UserMailRepository;
import webdoc.authentication.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class SettingServiceTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthService authService;

    @Autowired
    SettingService settingService;

    @Autowired
    UserMailRepository userMailRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @MockBean
    EmailService emailService;

    @DisplayName("비밀번호를 변경한다")
    @Test
    void changePassword() throws MessagingException {
        //given
        TuteeCreateRequest dto =
                tuteeCreateRequest();

        authService.createTuteeUser(dto);

        TuteeMail tuteeMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TuteeMail)e).findAny().get();

        authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()), LocalDateTime.now());

        User user = userRepository.findUserByEmail(dto.getEmail())
                .orElse(null);


        //when
        settingService.changePassword("dntjr","dntjrdn78",user.getId());


        //then
        User changedUser = userRepository.findUserByEmail(dto.getEmail())
                        .orElse(null);
        assertThat(passwordEncoder.matches("dntjrdn78",changedUser.getPassword()));
     }

    @DisplayName("비밀번호를 변경시 현재 비밀번호가 틀려서는 안된다")
    @Test
    void changePasswordWithWrongPw() throws MessagingException {
        //given
        TuteeCreateRequest dto =
                tuteeCreateRequest();

        authService.createTuteeUser(dto);

        TuteeMail tuteeMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TuteeMail)e).findAny().get();

        authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()), LocalDateTime.now());

        User user = userRepository.findUserByEmail(dto.getEmail())
                .orElse(null);


        //when + then
        assertThatThrownBy(()->settingService.changePassword("dntjrsd","dntjrdn78",user.getId()))
                .isInstanceOf(WrongPasswordException.class);


    }

    @DisplayName("연락처를 변경한다")
    @Test
    void changeContact() throws MessagingException {
        //given
        TuteeCreateRequest dto =
                tuteeCreateRequest();

        authService.createTuteeUser(dto);

        TuteeMail tuteeMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TuteeMail)e).findAny().get();

        authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()), LocalDateTime.now());

        User user = userRepository.findUserByEmail(dto.getEmail())
                .orElse(null);


        //when
        settingService.changeContact("dntjr","01042325779",user.getId());


        //then
        User changedUser = userRepository.findUserByEmail(dto.getEmail())
                .orElse(null);
        assertThat(changedUser.getContact()).isEqualTo("01042325779");
    }

    @DisplayName("연락처를 변경할 때 비밀번호가 틀리면 안된다")
    @Test
    void changeContactWithWrongPw() throws MessagingException {
        //given
        TuteeCreateRequest dto =
                tuteeCreateRequest();

        authService.createTuteeUser(dto);

        TuteeMail tuteeMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TuteeMail)e).findAny().get();

        authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()), LocalDateTime.now());

        User user = userRepository.findUserByEmail(dto.getEmail())
                .orElse(null);


        //when + then
        assertThatThrownBy(()->settingService.changeContact("dntjsdsdr","01042325779",user.getId()))
                .isInstanceOf(WrongPasswordException.class);

    }

    @DisplayName("튜터만이 자기소개를 변경할 수 있다")
    @Test
    void changeSelfDescriptionOnlyForTutor() throws MessagingException {
        //given
        TuteeCreateRequest dto =
                tuteeCreateRequest();

        authService.createTuteeUser(dto);

        TuteeMail tuteeMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TuteeMail)e).findAny().get();

        authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()), LocalDateTime.now());


        User user = userRepository.findUserByEmail(tuteeMail.getEmail())
                .orElse(null);


        //when + then
        assertThatThrownBy(()->settingService.changeSelfDescription("ggggg",user.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("튜터만이 자기소개를 변경할 수 있다")
    @Test
    void changeSelfDescription() throws MessagingException {
        //given
        TutorCreateRequest tutorCreateRequest = tutorCreateRequest();

        authService.createTutorUser(tutorCreateRequest);

        TutorMail tuteeMail = userMailRepository.findByEmail(tutorCreateRequest.getEmail())
                .stream().map(e->(TutorMail)e).findAny().get();

        authService.validateTutor(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()), LocalDateTime.now());

        User user = userRepository.findUserByEmail(tutorCreateRequest.getEmail())
                .orElse(null);


        //when
        settingService.changeSelfDescription("ggggg",user.getId());


        //then
        Tutor changedUser = (Tutor) userRepository.findUserByEmail(user.getEmail())
                .orElse(null);
        assertThat(changedUser.getSelfDescription()).isEqualTo("ggggg");
    }

    @DisplayName("닉네임 변경은 튜티만이 가능하다")
    @Test
    void changeNickNameOnlyForTutee() throws MessagingException {
        //given
        TutorCreateRequest tutorCreateRequest = tutorCreateRequest();

        authService.createTutorUser(tutorCreateRequest);

        TutorMail tuteeMail = userMailRepository.findByEmail(tutorCreateRequest.getEmail())
                .stream().map(e->(TutorMail)e).findAny().get();

        authService.validateTutor(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()), LocalDateTime.now());

        User user = userRepository.findUserByEmail(tutorCreateRequest.getEmail())
                .orElse(null);

        //when
        assertThatThrownBy(()->settingService.changeNickName("gdgad",user.getId()))
                .isInstanceOf(IllegalStateException.class);


     }

    @DisplayName("튜터는 닉네임 변경을 할 수 없다")
    @Test
    void changeNickName() throws MessagingException {
        //given
        TuteeCreateRequest dto =
                tuteeCreateRequest();

        authService.createTuteeUser(dto);

        TuteeMail tuteeMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(TuteeMail)e).findAny().get();

        authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()), LocalDateTime.now());

        User user = userRepository.findUserByEmail(dto.getEmail())
                .orElse(null);

        //when
        settingService.changeNickName("gdgad",user.getId());

        //then
        Tutee tutee = (Tutee) userRepository.findByEmail(user.getEmail())
                .orElse(null);
        assertThat(tutee.getNickName()).isEqualTo("gdgad");
    }

    private TutorCreateRequest tutorCreateRequest(){
        return TutorCreateRequest
                .builder()
                .authenticationAddress("http://localhost:8080")
                .contact("01025045779")
                .email("1dilumn0@gmail.com")
                .specialities(List.of(Specialities.KEYBOARD_INSTRUMENT,Specialities.BASS))
                .password("dntjrdn")
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
                .password("dntjr")
                .build();
    }
}
