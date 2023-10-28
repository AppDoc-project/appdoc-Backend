package webdoc.authentication.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.doctor.DoctorMail;
import webdoc.authentication.domain.entity.user.doctor.enums.MedicalSpecialities;
import webdoc.authentication.domain.entity.user.doctor.request.DoctorCreateRequest;
import webdoc.authentication.domain.entity.user.patient.PatientMail;
import webdoc.authentication.domain.entity.user.patient.request.PatientCreateRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.repository.UserMailRepository;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.service.EmailService;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;




@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EmailService emailService;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMailRepository userMailRepository;

    @DisplayName("환자 로그인이 성공하였을 경우에 200상태를 반환한다")
    @Test
    void patientLogin() throws Exception {
        PatientCreateRequest patientRequest
                = patientCreateRequest();

        PatientMail patientMail = authService.createPatientUser(patientRequest);
        authService.validatePatient(new CodeRequest(patientMail.getEmail(),patientMail.getCode()),LocalDateTime.now());



        mockMvc.perform(post("/auth/login")
                .header("email",patientMail.getEmail())
                .header("password","dntjrdn78"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));

     }

    @DisplayName("환자 로그인이 성공실패하였을 경우 400상태를 반환한다")
    @Test
    void patientLoginFail() throws Exception {
        PatientCreateRequest patientRequest
                = patientCreateRequest();

        PatientMail patientMail = authService.createPatientUser(patientRequest);
        authService.validatePatient(new CodeRequest(patientMail.getEmail(),patientMail.getCode()),LocalDateTime.now());


        mockMvc.perform(post("/auth/login")
                        .header("email",patientMail.getEmail())
                        .header("password","dntjrn78"))
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }

    @DisplayName("환자 로그인이 성공하였을 경우에 제한된 url을 접근할 수 있다")
    @Test
    void patientAccessLimitResource() throws Exception {
        PatientCreateRequest patientRequest
                = patientCreateRequest();

        PatientMail patientMail = authService.createPatientUser(patientRequest);
        authService.validatePatient(new CodeRequest(patientMail.getEmail(),patientMail.getCode()),LocalDateTime.now());


        MvcResult result = mockMvc.perform(post("/auth/login")
                        .header("email",patientMail.getEmail())
                        .header("password","dntjrdn78"))
                .andReturn();

        String jwt = result.getResponse().getHeader("Authorization");

        mockMvc.perform(get("/test")
                .header("authorization",jwt))
                .andDo(print())
                .andExpect(status().isOk());


     }

    @DisplayName("의사 로그인이 성공하였을 경우에 200상태를 반환한다")
    @Test
    void doctortLogin() throws Exception {
        DoctorCreateRequest doctorRequest
                = doctorCreateRequest();
        DoctorMail doctorMail = authService.createDoctorUser(doctorRequest);
        authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(doctorMail.getEmail()).orElse(null);
        authService.setDoctorAuthenticationSuccess(user.getId());

        mockMvc.perform(post("/auth/login")
                        .header("email",doctorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));

    }

    @DisplayName("의사 로그인이 성공실패하였을 경우 400상태를 반환한다")
    @Test
    void doctorLoginFail() throws Exception {
        DoctorCreateRequest doctorRequest
                = doctorCreateRequest();
        DoctorMail doctorMail = authService.createDoctorUser(doctorRequest);
        authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(doctorMail.getEmail()).orElse(null);
        authService.setDoctorAuthenticationSuccess(user.getId());


        mockMvc.perform(post("/auth/login")
                        .header("email",doctorMail.getEmail())
                        .header("password","dntjrn78"))
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }

    @DisplayName("의사 로그인이 성공하였을 경우에 제한된 url을 접근할 수 있다")
    @Test
    void doctorAccessLimitResource() throws Exception {
        DoctorCreateRequest doctorRequest
                = doctorCreateRequest();
        DoctorMail doctorMail = authService.createDoctorUser(doctorRequest);
        authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(doctorMail.getEmail()).orElse(null);
        authService.setDoctorAuthenticationSuccess(user.getId());

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .header("email",doctorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andReturn();

        String jwt = result.getResponse().getHeader("Authorization");

        mockMvc.perform(get("/test")
                        .header("authorization",jwt))
                .andDo(print())
                .andExpect(status().isOk());


    }

    @DisplayName("의사가 자격인증이 진행 중인 상태에서 로그인 하면 402코드를 반환한다")
    @Test
    void doctorLoginProcessOngoing() throws Exception {
        DoctorCreateRequest doctorRequest
                = doctorCreateRequest();
        DoctorMail doctorMail = authService.createDoctorUser(doctorRequest);
        authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(doctorMail.getEmail()).orElse(null);



        mockMvc.perform(post("/auth/login")
                        .header("email",doctorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(402));

    }

    @DisplayName("의사가 자격인증이 거부된 상태에서 로그인 하면 401코드를 반환한다")
    @Test
    void doctorLoginProcessDenied() throws Exception {
        DoctorCreateRequest doctorRequest
                = doctorCreateRequest();
        DoctorMail doctorMail = authService.createDoctorUser(doctorRequest);
        authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(doctorMail.getEmail()).orElse(null);
        authService.setDoctorAuthenticationDenied(user.getId());


        mockMvc.perform(post("/auth/login")
                        .header("email",doctorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(401));

    }

    @DisplayName("의사가 자격인증이 거부된 상태에서 다시 회원가입을 할 수 있다")
    @Test
    void doctorLoginProcessDeniedAndJoin() throws Exception {
        DoctorCreateRequest doctorRequest
                = doctorCreateRequest();
        DoctorMail doctorMail = authService.createDoctorUser(doctorRequest);
        authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(doctorMail.getEmail()).orElse(null);
        authService.setDoctorAuthenticationDenied(user.getId());

        doctorRequest.setName("김민교");
        authService.createDoctorUser(doctorRequest);

        assertThat(userMailRepository.findByEmail(doctorMail.getEmail()).orElse(null))
                .extracting("name")
                .isEqualTo("김민교");


    }

    @DisplayName("중복 로그인을 허용하지 않는다")
    @Test
    void noDuplicatedLogin() throws Exception {
        DoctorCreateRequest doctorRequest
                = doctorCreateRequest();
        DoctorMail doctorMail = authService.createDoctorUser(doctorRequest);
        authService.validateDoctor(new CodeRequest(doctorMail.getEmail(),doctorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(doctorMail.getEmail()).orElse(null);
        authService.setDoctorAuthenticationSuccess(user.getId());

        // 첫 번째 로그인 성공
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .header("email",doctorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andReturn();

        String jwt1 = result.getResponse().getHeader("Authorization");

        // 첫 번째 엔드포인트 접근
        mockMvc.perform(get("/test")
                        .header("authorization",jwt1))
                .andDo(print())
                .andExpect(status().isOk());

        // 두 번째 로그인 성공
        result = mockMvc.perform(post("/auth/login")
                        .header("email",doctorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andExpect(status().isOk())
                .andReturn();

        String jwt2 = result.getResponse().getHeader("Authorization");


        assertThat(jwt2).isNotEqualTo(jwt1);
        System.out.println(jwt1);
        System.out.println(jwt2);
        //기존의 jwt1으로 엔드포인트 접근 시 실패
        mockMvc.perform(get("/test")
                        .header("authorization",jwt1))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        //기존의 jwt2으로 엔드포인트 접근 시 성공
        mockMvc.perform(get("/test")
                        .header("authorization",jwt2))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

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
