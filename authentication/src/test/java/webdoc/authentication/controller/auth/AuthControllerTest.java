package webdoc.authentication.controller.auth;


import com.fasterxml.jackson.core.JsonProcessingException;
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
import webdoc.authentication.domain.entity.user.doctor.enums.MedicalSpecialities;
import webdoc.authentication.domain.entity.user.doctor.request.DoctorCreateRequest;
import webdoc.authentication.domain.entity.user.patient.request.PatientCreateRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.entity.user.request.EmailRequest;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import webdoc.authentication.utility.messageprovider.CommonMessageProvider;

import java.sql.SQLException;
import java.time.LocalDate;

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


    @DisplayName("의사회원가입을 테스트 한다")
    @Test
    void doctorJoinTest() throws Exception {
        //given
        DoctorCreateRequest doctorRequest =
                doctorCreateRequest();

        mockMvc.perform(post("/auth/join/doctor")
                        .content(objectMapper.writeValueAsString(doctorRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("의사회원가입은 값검증을 수행한다")
    @Test
    void doctorJoinTestWithInvalidValues() throws Exception {
        DoctorCreateRequest doctorRequest =
                doctorCreateRequest();
        doctorRequest.setName("우");

        mockMvc.perform(post("/auth/join/doctor")
                        .content(objectMapper.writeValueAsString(doctorRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("의사회원가입에서 이메일이 중복된 경우 상태코드 400을 반환한다")
    @Test
    void doctorJoinEmailDuplication() throws Exception {
        DoctorCreateRequest doctorRequest =
                doctorCreateRequest();
        doctorRequest.setName("우");

        when(authService.createDoctorUser(any())).thenThrow(new IllegalStateException());

        mockMvc.perform(post("/auth/join/doctor")
                        .content(objectMapper.writeValueAsString(doctorRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(AuthMessageProvider.BINDING_FAILURE));
    }

    @DisplayName("의사회원가입에서 서버에러가 발생할 경우 상태코드 500을 반환한다")
    @Test
    void doctorJoinInternalServerError() throws Exception {
        DoctorCreateRequest doctorRequest =
                doctorCreateRequest();


        when(authService.createDoctorUser(any())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/auth/join/doctor")
                        .content(objectMapper.writeValueAsString(doctorRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").value(CommonMessageProvider.INTERNAL_SERVER_ERROR));
    }

    @DisplayName("의사회원이 이메일 인증을 수행한다")
    @Test
    void validateDoctor() throws Exception {

        CodeRequest codeRequest = new CodeRequest("1dilumn0@gmail.com","4123");



        mockMvc.perform(post("/auth/validate/doctor")
                        .content(objectMapper.writeValueAsString(codeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.httpStatus").value(200));
    }

    @DisplayName("의사회원이 이메일 인증할 때 시간이 초과하면 401코드를 반환한다")
    @Test
    void validateDoctorTimeout() throws Exception {

        CodeRequest codeRequest = new CodeRequest("1dilumn0@gmail.com","4123");

        doThrow(new TimeOutException("시간초과")).when(authService).validateDoctor(any(),any());
        mockMvc.perform(post("/auth/validate/doctor")
                        .content(objectMapper.writeValueAsString(codeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.code").value(401));
    }

    @DisplayName("의사회원이 이메일 인증할 때 코드가 잘못되면 402코드를 반환한다")
    @Test
    void validateDoctorWrongCode() throws Exception {

        CodeRequest codeRequest = new CodeRequest("1dilumn0@gmail.com","4123");

        doThrow(new AuthenticationServiceException("인증번호 틀림")).when(authService).validateDoctor(any(),any());
        mockMvc.perform(post("/auth/validate/doctor")
                        .content(objectMapper.writeValueAsString(codeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.code").value(402));
    }


    @DisplayName("환자회원가입을 테스트 한다")
    @Test
    void patientJoinTest() throws Exception {

        PatientCreateRequest patientRequest =
                patientCreateRequest();

        mockMvc.perform(post("/auth/join/patient")
                        .content(objectMapper.writeValueAsString(patientRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("환자회원가입은 값검증을 수행한다")
    @Test
    void patientJoinTestWithInvalidValues() throws Exception {
        PatientCreateRequest patientRequest =
                patientCreateRequest();
        patientRequest.setContact("01013");

        mockMvc.perform(post("/auth/join/patient")
                        .content(objectMapper.writeValueAsString(patientRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("환자회원가입에서 이메일이 중복된 경우 상태코드 400을 반환한다")
    @Test
    void patientJoinEmailDuplication() throws Exception {
        PatientCreateRequest patientRequest =
                patientCreateRequest();
        patientRequest.setName("우");

        when(authService.createPatientUser(any())).thenThrow(new IllegalStateException());

        mockMvc.perform(post("/auth/join/patient")
                        .content(objectMapper.writeValueAsString(patientRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(AuthMessageProvider.BINDING_FAILURE));
    }

    @DisplayName("환자회원가입에서 서버에러가 발생할 경우 상태코드 500을 반환한다")
    @Test
    void patientJoinInternalServerError() throws Exception {
        PatientCreateRequest patientRequest =
                patientCreateRequest();


        when(authService.createPatientUser(any())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/auth/join/patient")
                        .content(objectMapper.writeValueAsString(patientRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").value(CommonMessageProvider.INTERNAL_SERVER_ERROR));
    }

    @DisplayName("환자회원이 이메일 인증할 때 시간이 초과하면 401코드를 반환한다")
    @Test
    void validatePatientTimeout() throws Exception {

        CodeRequest codeRequest = new CodeRequest("1dilumn0@gmail.com","4123");

        doThrow(new TimeOutException("시간초과")).when(authService).validatePatient(any(),any());
        mockMvc.perform(post("/auth/validate/patient")
                        .content(objectMapper.writeValueAsString(codeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.code").value(401));
    }

    @DisplayName("환자회원이 이메일 인증할 때 코드가 잘못되면 402코드를 반환한다")
    @Test
    void validatePateintWrongCode() throws Exception {

        CodeRequest codeRequest = new CodeRequest("1dilumn0@gmail.com","4123");

        doThrow(new AuthenticationServiceException("인증번호 틀림")).when(authService).validatePatient(any(),any());
        mockMvc.perform(post("/auth/validate/patient")
                        .content(objectMapper.writeValueAsString(codeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.code").value(402));
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
                .andExpect(jsonPath("$.code").value(200));

     }

    @DisplayName("이메일 중복을 검사할 때 적절한 이메일을 보내지 않으면 400코드를 반환한다")
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
                .andExpect(jsonPath("$.code").value(400));

    }

    @DisplayName("이메일 중복을 검사할 때 중복된 이메일일 겨우 401코드를 반환한다")
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
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(AuthMessageProvider.EMAIL_EXISTS));

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