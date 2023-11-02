package webdoc.authentication.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.UserMail;
import webdoc.authentication.domain.entity.user.doctor.DoctorMail;
import webdoc.authentication.domain.entity.user.doctor.request.DoctorCreateRequest;
import webdoc.authentication.domain.entity.user.doctor.enums.MedicalSpecialities;
import webdoc.authentication.domain.entity.user.patient.PatientMail;
import webdoc.authentication.domain.entity.user.patient.request.PatientCreateRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.exceptions.EmailDuplicationException;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserMailRepository;
import webdoc.authentication.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @DisplayName("의사 회원가입을 테스트 한다")
    @Test
    void createDoctorUserTest() throws MessagingException {
        //given
        DoctorCreateRequest dto =
                doctorCreateRequest();

        //when
        authService.createDoctorUser(dto);
        DoctorMail userMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(DoctorMail)e).findAny().get();

        //then
        assertThat(userMail).isNotNull();
        assertThat(userMail)
                .extracting(
                        "address", "certificateAddress", "contact", "dateOfBirth", "email",
                        "hospitalName", "medicalSpeciality", "password", "selfDescription", "name"
                )
                .contains(
                        dto.getAddress(), dto.getCertificateAddress(), dto.getContact(),
                        dto.getDateOfBirth(), dto.getEmail(), dto.getHospitalName(),
                        dto.getMedicalSpeciality(), dto.getPassword(), dto.getSelfDescription(),
                        dto.getName()
                );
     }

    @DisplayName("의사 이메일 인증 번호가 올바르고 3분이내의 경우를 테스트 한다")
    @Test
    void validateDoctorSuccessTest() throws MessagingException {
        //given
        DoctorCreateRequest dto =
                doctorCreateRequest();

        authService.createDoctorUser(dto);

        DoctorMail doctorMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(DoctorMail)e).findAny().get();


        //when
        authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()), LocalDateTime.now());

        //then
        User user = userRepository
                .findByEmail(dto.getEmail()).orElseThrow(
                        ()->
                            new IllegalStateException("해당하는 유저가 없습니다")
                );
        UserMail userMail = userMailRepository.findByEmail(user.getEmail()).orElse(null);
        assertThat(userMail).isNull();


     }

     @DisplayName("의사 인증번호가 틀린경우를 테스트한다")
     @Test
     void validateDoctorWithWrongCode() throws MessagingException {
         //given
         DoctorCreateRequest dto =
                 doctorCreateRequest();

         authService.createDoctorUser(dto);

         DoctorMail doctorMail = userMailRepository.findByEmail(dto.getEmail())
                 .stream().map(e->(DoctorMail)e).findAny().get();


         //when and then
         assertThatThrownBy(()->{
             authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),"21321413"), LocalDateTime.now());
         }).isInstanceOf(AuthenticationServiceException.class);

         User user = userRepository
                 .findByEmail(dto.getEmail()).orElse(null);

         assertThat(user).isNull();;



     }

    @DisplayName("의사 인증번호가 시간이 지난 경우를 테스트한다")
    @Test
    void validateDoctorTimeOut() throws MessagingException {
        //given
        DoctorCreateRequest dto =
                doctorCreateRequest();

        authService.createDoctorUser(dto);

        DoctorMail doctorMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(DoctorMail)e).findAny().get();


        //when and then
        assertThatThrownBy(()->{
            authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()), LocalDateTime.now().plusMinutes(4L));
        }).isInstanceOf(TimeOutException.class).hasMessage("인증 시간을 초과하였습니다");

        User user = userRepository
                .findByEmail(dto.getEmail()).orElse(null);

        assertThat(user).isNull();;


    }

    @DisplayName("이메일이 중복된 의사 회원가입을 테스트한다")
    @Test
    void createDuplicateDoctor() throws MessagingException {
        //given
        DoctorCreateRequest dto =
                doctorCreateRequest();

        DoctorCreateRequest dto2 =
                doctorCreateRequest();

        authService.createDoctorUser(dto);

        DoctorMail doctorMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(DoctorMail)e).findAny().get();

        authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()), LocalDateTime.now());

        //when and then
        assertThatThrownBy(()->{
            authService.createDoctorUser(dto2);
        }).isInstanceOf(EmailDuplicationException.class)
                .hasMessage("해당 이메일을 가진 유저가 존재합니다");

     }

     @DisplayName("회원가입을 하지 않은 의사회원의 메일을 인증한다")
     @Test
     void validateDoctorNotRegistered(){

         assertThatThrownBy(
                 ()->{
                     authService.validateDoctor(new CodeRequest("1sdad@naver.com","1123"), LocalDateTime.now());
                 }
         ).isInstanceOf(NoSuchElementException.class).hasMessage("id에 해당하는 회원이 없습니다");

      }


    @DisplayName("환자 회원가입을 테스트 한다")
    @Test
    void createPatientUserTest() throws MessagingException {
        //given
        PatientCreateRequest dto =
                patientCreateRequest();

        //when
        authService.createPatientUser(dto);
        PatientMail userMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(PatientMail)e).findAny().get();

        //then
        assertThat(userMail).isNotNull();
        assertThat(userMail)
                .extracting(
                        "contact","dateOfBirth","email","name","password"
                )
                .contains(
                       dto.getContact(),dto.getDateOfBirth(),dto.getEmail(),dto.getName(),
                        dto.getPassword()
                );
    }

    @DisplayName("환자 이메일 인증 번호가 올바르고 3분이내의 경우를 테스트 한다")
    @Test
    void validatePatientSuccessTest() throws MessagingException {
        //given
        PatientCreateRequest dto =
                patientCreateRequest();

        authService.createPatientUser(dto);

        PatientMail patientMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(PatientMail)e).findAny().get();


        //when
        authService.validatePatient(new CodeRequest(patientMail.getEmail(),patientMail.getCode()), LocalDateTime.now());


        //then
        User user = userRepository
                .findByEmail(patientMail.getEmail()).orElseThrow(
                        ()->
                                new IllegalStateException("해당하는 유저가 없습니다")
                );

        UserMail userMail = userMailRepository.findByEmail(user.getEmail()).orElse(null);
        assertThat(userMail).isNull();


    }

    @DisplayName("환자 인증번호가 틀린경우를 테스트한다")
    @Test
    void validatePatientWithWrongCode() throws MessagingException {
        //given
        PatientCreateRequest dto =
                patientCreateRequest();

        authService.createPatientUser(dto);

        PatientMail patientMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(PatientMail)e).findAny().get();


        //when and then
        assertThatThrownBy(()->{
            authService.validatePatient(new CodeRequest(patientMail.getEmail(),"21321413"), LocalDateTime.now());
        }).isInstanceOf(AuthenticationServiceException.class);

        User user = userRepository
                .findByEmail(dto.getEmail()).orElse(null);

        assertThat(user).isNull();;



    }

    @DisplayName("환자 인증번호가 시간이 지난 경우를 테스트한다")
    @Test
    void validatePatientTimeOut() throws MessagingException {
        //given
        PatientCreateRequest dto =
                patientCreateRequest();

        authService.createPatientUser(dto);

        PatientMail patientMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(PatientMail)e).findAny().get();


        //when and then
        assertThatThrownBy(()->{
            authService.validatePatient(new CodeRequest(patientMail.getEmail(),patientMail.getCode()), LocalDateTime.now().plusMinutes(4L));
        }).isInstanceOf(TimeOutException.class).hasMessage("인증 시간을 초과하였습니다");

        User user = userRepository
                .findByEmail(dto.getEmail()).orElse(null);

        assertThat(user).isNull();;


    }

    @DisplayName("이메일이 중복된 환자 회원가입을 테스트한다")
    @Test
    void createDuplicatePatient() throws MessagingException {
        //given
        DoctorCreateRequest dto =
                doctorCreateRequest();

        PatientCreateRequest dto2 =
                patientCreateRequest();

        authService.createDoctorUser(dto);

        DoctorMail doctorMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(DoctorMail)e).findAny().get();

        authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()), LocalDateTime.now());

        //when and then
        assertThatThrownBy(()->{
            authService.createPatientUser(dto2);
        }).isInstanceOf(EmailDuplicationException.class)
                .hasMessage("해당 이메일을 가진 유저가 존재합니다");

    }

    @DisplayName("회원가입을 하지 않은 환자의 메일을 인증한다")
    @Test
    void validatePatientNotRegistered(){

        assertThatThrownBy(
                ()->{
                    authService.validatePatient(new CodeRequest("1sdad@naver.com","1123"), LocalDateTime.now());
                }
        ).isInstanceOf(NoSuchElementException.class).hasMessage("id에 해당하는 회원이 없습니다");

    }








    private DoctorCreateRequest doctorCreateRequest(){
        return DoctorCreateRequest
                .builder()
                .address("서울시 마포구 서교동")
                .certificateAddress("http://localhost:8080")
                .contact("01025045779")
                .dateOfBirth(LocalDate.now())
                .email("1dilumn0@gmail.com")
                .hospitalName("서울대학병원")
                .medicalSpeciality(MedicalSpecialities.DENTISTRY)
                .password("dntjrdn78")
                .selfDescription("좋은 의사입니다")
                .name("우석우")
                .build();
    }

    private PatientCreateRequest patientCreateRequest(){
        return PatientCreateRequest
                .builder()
                .contact("01025045779")
                .dateOfBirth(LocalDate.now())
                .email("1dilumn0@gmail.com")
                .name("우석우")
                .password("dntjrdn78")
                .build();
    }
}