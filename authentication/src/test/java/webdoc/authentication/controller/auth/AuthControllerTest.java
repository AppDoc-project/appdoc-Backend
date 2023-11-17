package webdoc.authentication.controller.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.domain.entity.user.tutor.request.TutorCreateRequest;
import webdoc.authentication.domain.entity.user.tutee.request.TuteeCreateRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.entity.user.request.EmailRequest;
import webdoc.authentication.domain.exceptions.EmailDuplicationException;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import webdoc.authentication.utility.messageprovider.CommonMessageProvider;
import webdoc.authentication.utility.messageprovider.ResponseCodeProvider;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

    @MockBean
    UserRepository userRepository;


    @DisplayName("튜터회원가입을 테스트 한다")
    @Test
    void tutorJoinTest() throws Exception {
        //given
        TutorCreateRequest tutorRequest =
                tutorCreateRequest();

        mockMvc.perform(post("/auth/join/tutor")
                        .content(objectMapper.writeValueAsString(tutorRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("튜터회원가입은 값검증을 수행한다")
    @Test
    void tutorJoinTestWithInvalidValues() throws Exception {
        TutorCreateRequest tutorRequest =
                tutorCreateRequest();
        tutorRequest.setName("우");

        mockMvc.perform(post("/auth/join/tutor")
                        .content(objectMapper.writeValueAsString(tutorRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("튜터회원가입에서 이메일이 중복된 경우 상태코드 그에 맞는 코드를 반환한다")
    @Test
    void tutorJoinEmailDuplication() throws Exception {
        TutorCreateRequest tutorRequest =
                tutorCreateRequest();


        when(authService.createTutorUser(any())).thenThrow(new EmailDuplicationException("이메일 중복"));

        mockMvc.perform(post("/auth/join/tutor")
                        .content(objectMapper.writeValueAsString(tutorRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(AuthMessageProvider.EMAIL_EXISTS));
    }

    @DisplayName("튜터회원가입에서 서버에러가 발생할 경우 상태코드 500을 반환한다")
    @Test
    void tutorJoinInternalServerError() throws Exception {
        TutorCreateRequest tutorRequest =
                tutorCreateRequest();


        when(authService.createTutorUser(any())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/auth/join/tutor")
                        .content(objectMapper.writeValueAsString(tutorRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").value(CommonMessageProvider.INTERNAL_SERVER_ERROR));
    }

    @DisplayName("튜터회원이 이메일 인증을 수행한다")
    @Test
    void validateTutor() throws Exception {

        CodeRequest codeRequest = new CodeRequest("1dilumn0@gmail.com","4123");



        mockMvc.perform(post("/auth/validate/tutor")
                        .content(objectMapper.writeValueAsString(codeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.httpStatus").value(200));
    }

    @DisplayName("튜터회원이 이메일 인증할 때 시간이 초과하면 그에 맞는 코드를 반환한다")
    @Test
    void validateTutorTimeout() throws Exception {

        CodeRequest codeRequest = new CodeRequest("1dilumn0@gmail.com","4123");

        doThrow(new TimeOutException("시간초과")).when(authService).validateTutor(any(),any());
        mockMvc.perform(post("/auth/validate/tutor")
                        .content(objectMapper.writeValueAsString(codeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.code").value(ResponseCodeProvider.VALIDATION_EXPIRED));
    }

    @DisplayName("튜터회원이 이메일 인증할 때 코드가 잘못되면 그에 맞는 코드를 반환한다")
    @Test
    void validateTutorWrongCode() throws Exception {

        CodeRequest codeRequest = new CodeRequest("1dilumn0@gmail.com","4123");

        doThrow(new AuthenticationServiceException("인증번호 틀림")).when(authService).validateTutor(any(),any());
        mockMvc.perform(post("/auth/validate/tutor")
                        .content(objectMapper.writeValueAsString(codeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.code").value(ResponseCodeProvider.WRONG_CODE));
    }


    @DisplayName("튜티회원가입을 테스트 한다")
    @Test
    void tuteeJoinTest() throws Exception {

        TuteeCreateRequest tuteeRequest =
                tuteeCreateRequest();

        mockMvc.perform(post("/auth/join/tutee")
                        .content(objectMapper.writeValueAsString(tuteeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("튜티회원가입은 값검증을 수행한다")
    @Test
    void tuteeJoinTestWithInvalidValues() throws Exception {
        TuteeCreateRequest tuteeRequest =
                tuteeCreateRequest();
        tuteeRequest.setContact("01013");

        mockMvc.perform(post("/auth/join/tutee")
                        .content(objectMapper.writeValueAsString(tuteeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("튜티회원가입에서 이메일이 중복된 경우 상태코드 400을 반환한다")
    @Test
    void tuteeJoinEmailDuplication() throws Exception {
        TuteeCreateRequest tuteeRequest =
                tuteeCreateRequest();

        when(authService.createTuteeUser(any())).thenThrow(new EmailDuplicationException("중복된 이메일"));

        mockMvc.perform(post("/auth/join/tutee")
                        .content(objectMapper.writeValueAsString(tuteeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(AuthMessageProvider.EMAIL_EXISTS));
    }

    @DisplayName("튜티회원가입에서 서버에러가 발생할 경우 상태코드 500을 반환한다")
    @Test
    void tuteeJoinInternalServerError() throws Exception {
        TuteeCreateRequest tuteeRequest =
                tuteeCreateRequest();


        when(authService.createTuteeUser(any())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/auth/join/tutee")
                        .content(objectMapper.writeValueAsString(tuteeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").value(CommonMessageProvider.INTERNAL_SERVER_ERROR));
    }

    @DisplayName("튜티회원이 이메일 인증할 때 시간이 초과하면 그에 맞는 코드를 반환한다")
    @Test
    void validateTuteeTimeout() throws Exception {

        CodeRequest codeRequest = new CodeRequest("1dilumn0@gmail.com","4123");

        doThrow(new TimeOutException("시간초과")).when(authService).validateTutee(any(),any());
        mockMvc.perform(post("/auth/validate/tutee")
                        .content(objectMapper.writeValueAsString(codeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.code").value(ResponseCodeProvider.VALIDATION_EXPIRED));
    }

    @DisplayName("튜티회원이 이메일 인증할 때 코드가 잘못되면 그에 맞는 코드를 반환한다")
    @Test
    void validatePateintWrongCode() throws Exception {

        CodeRequest codeRequest = new CodeRequest("1dilumn0@gmail.com","4123");

        doThrow(new AuthenticationServiceException("인증번호 틀림")).when(authService).validateTutee(any(),any());
        mockMvc.perform(post("/auth/validate/tutee")
                        .content(objectMapper.writeValueAsString(codeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.code").value(ResponseCodeProvider.WRONG_CODE));
    }

    @DisplayName("이메일 중복을 검사한다")
    @Test
    void emailDuplicationTest() throws Exception {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("1dilumn0@gmail.com");

        mockMvc.perform(post("/auth/join/duplication")
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.code").value(ResponseCodeProvider.SUCCESS));

     }

    @DisplayName("이메일 중복을 검사할 때 적절한 이메일을 보내지 않으면 그에 맞는 코드를 반환한다")
    @Test
    void emailInvalid() throws Exception {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("1dilum");

        mockMvc.perform(post("/auth/join/duplication")
                        .content(objectMapper.writeValueAsString(emailRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.code").value(ResponseCodeProvider.BINDING_FAILURE));

    }

    @DisplayName("이메일 중복을 검사할 때 중복된 이메일일 경우 그에 맞는 코드를 반환한다")
    @Test
    void duplicatedEmail() throws Exception {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("1dilum0@gmail.com");

        when(authService.isEmailDuplicated(any())).thenReturn(true);

        mockMvc.perform(post("/auth/join/duplication")
                        .content(objectMapper.writeValueAsString(emailRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.code").value(ResponseCodeProvider.EMAIL_EXISTS))
                .andExpect(jsonPath("$.message").value(AuthMessageProvider.EMAIL_EXISTS));

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