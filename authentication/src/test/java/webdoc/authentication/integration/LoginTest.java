package webdoc.authentication.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import webdoc.authentication.domain.entity.user.tutor.TutorMail;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.domain.entity.user.tutor.request.TutorCreateRequest;
import webdoc.authentication.domain.entity.user.tutee.TuteeMail;
import webdoc.authentication.domain.entity.user.tutee.request.TuteeCreateRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.repository.UserMailRepository;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.service.EmailService;
import webdoc.authentication.utility.messageprovider.ResponseCodeProvider;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @DisplayName("튜티 로그인이 성공하였을 경우에 200상태를 반환한다")
    @Test
    void tuteeLogin() throws Exception {
        TuteeCreateRequest request
                = tuteeCreateRequest();

        TuteeMail tuteeMail = authService.createTuteeUser(request);
        authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()),LocalDateTime.now());



        mockMvc.perform(post("/auth/login")
                .header("email",tuteeMail.getEmail())
                .header("password","dntjrdn78"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));

     }

    @DisplayName("튜티 로그인이 성공실패하였을 경우 400상태를 반환한다")
    @Test
    void tuteeLoginFail() throws Exception {
        TuteeCreateRequest request
                = tuteeCreateRequest();

        TuteeMail tuteeMail = authService.createTuteeUser(request);
        authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()),LocalDateTime.now());


        mockMvc.perform(post("/auth/login")
                        .header("email",tuteeMail.getEmail())
                        .header("password","dntjrn78"))
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }

    @DisplayName("튜티 로그인이 성공하였을 경우에 제한된 url을 접근할 수 있다")
    @Test
    void tuteeAccessLimitResource() throws Exception {
        TuteeCreateRequest request
                = tuteeCreateRequest();

        TuteeMail tuteeMail = authService.createTuteeUser(request);
        authService.validateTutee(new CodeRequest(tuteeMail.getEmail(),tuteeMail.getCode()),LocalDateTime.now());


        MvcResult result = mockMvc.perform(post("/auth/login")
                        .header("email",tuteeMail.getEmail())
                        .header("password","dntjrdn78"))
                .andReturn();

        String jwt = result.getResponse().getHeader("Authorization");

        mockMvc.perform(get("/test")
                .header("authorization",jwt))
                .andDo(print())
                .andExpect(status().isOk());


     }

    @DisplayName("튜터 로그인이 성공하였을 경우에 200상태를 반환한다")
    @Test
    void tutorLogin() throws Exception {
        TutorCreateRequest request
                = tutorCreateRequest();
        TutorMail tutorMail = authService.createTutorUser(request);
        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(tutorMail.getEmail()).orElse(null);
        authService.setTutorAuthenticationSuccess(user.getId());

        mockMvc.perform(post("/auth/login")
                        .header("email",tutorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));

    }

    @DisplayName("튜터 로그인이 성공실패하였을 경우 400상태를 반환한다")
    @Test
    void tutorLoginFail() throws Exception {
        TutorCreateRequest tutorRequest
                = tutorCreateRequest();
        TutorMail tutorMail = authService.createTutorUser(tutorRequest);
        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(tutorMail.getEmail()).orElse(null);
        authService.setTutorAuthenticationSuccess(user.getId());


        mockMvc.perform(post("/auth/login")
                        .header("email",tutorMail.getEmail())
                        .header("password","dntjrn78"))
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }

    @DisplayName("튜터 로그인이 성공하였을 경우에 제한된 url을 접근할 수 있다")
    @Test
    void tutorAccessLimitResource() throws Exception {
        TutorCreateRequest request
                = tutorCreateRequest();
        TutorMail tutorMail = authService.createTutorUser(request);
        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(tutorMail.getEmail()).orElse(null);
        authService.setTutorAuthenticationSuccess(user.getId());

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .header("email",tutorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andReturn();

        String jwt = result.getResponse().getHeader("Authorization");

        mockMvc.perform(get("/test")
                        .header("authorization",jwt))
                .andDo(print())
                .andExpect(status().isOk());


    }

    @DisplayName("튜터가 자격인증이 진행 중인 상태에서 로그인 하면 그에 맞는 코드를 반환한다")
    @Test
    void tutorLoginProcessOngoing() throws Exception {
        TutorCreateRequest tutorRequest
                = tutorCreateRequest();
        TutorMail tutorMail = authService.createTutorUser(tutorRequest);
        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(tutorMail.getEmail()).orElse(null);



        mockMvc.perform(post("/auth/login")
                        .header("email",tutorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(ResponseCodeProvider.AUTHENTICATION_ONGOING));

    }

    @DisplayName("튜터가 자격인증이 거부된 상태에서 로그인 하면 그에 맞는 코드를 반환한다")
    @Test
    void tutorLoginProcessDenied() throws Exception {
        TutorCreateRequest tutorRequest
                = tutorCreateRequest();
        TutorMail tutorMail = authService.createTutorUser(tutorRequest);
        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(tutorMail.getEmail()).orElse(null);
        authService.setTutorAuthenticationDenied(user.getId());


        mockMvc.perform(post("/auth/login")
                        .header("email",tutorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(ResponseCodeProvider.AUTHENTICATION_DENIED));

    }

    @DisplayName("튜터가 자격인증이 거부된 상태에서 다시 회원가입을 할 수 있다")
    @Test
    void tutorLoginProcessDeniedAndJoin() throws Exception {
        TutorCreateRequest tutorRequest
                = tutorCreateRequest();
        TutorMail tutorMail = authService.createTutorUser(tutorRequest);
        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(tutorMail.getEmail()).orElse(null);
        authService.setTutorAuthenticationDenied(user.getId());

        tutorRequest.setName("김민교");
        authService.createTutorUser(tutorRequest);

        assertThat(userMailRepository.findByEmail(tutorMail.getEmail()).orElse(null))
                .extracting("name")
                .isEqualTo("김민교");


    }

    @DisplayName("중복 로그인을 허용하지 않는다")
    @Test
    void noDuplicatedLogin() throws Exception {
        TutorCreateRequest tutorRequest
                = tutorCreateRequest();
        TutorMail tutorMail = authService.createTutorUser(tutorRequest);
        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()),LocalDateTime.now());
        User user = userRepository.findByEmail(tutorMail.getEmail()).orElse(null);
        authService.setTutorAuthenticationSuccess(user.getId());

        // 첫 번째 로그인 성공
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .header("email",tutorMail.getEmail())
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
                        .header("email",tutorMail.getEmail())
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
