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
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.entity.user.tutee.request.TuteeCreateRequest;
import webdoc.authentication.domain.entity.user.tutor.TutorMail;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.domain.entity.user.tutor.request.TutorCreateRequest;
import webdoc.authentication.repository.UserMailRepository;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.service.EmailService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthServerTest {

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
    @DisplayName("자기 자신의 정보를 fetch할 수 있다")
    @Test
    void fetchMyOwnData() throws Exception {
        TutorCreateRequest request
                = tutorCreateRequest();
        TutorMail tutorMail = authService.createTutorUser(request);
        authService.validateTutor(new CodeRequest(tutorMail.getEmail(),tutorMail.getCode()), LocalDateTime.now());
        User user = userRepository.findByEmail(tutorMail.getEmail()).orElse(null);
        authService.setTutorAuthenticationSuccess(user.getId());

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .header("email",tutorMail.getEmail())
                        .header("password","dntjrdn78"))
                .andReturn();

        String jwt = result.getResponse().getHeader("Authorization");

        MvcResult ret = mockMvc.perform(get("/auth/server/user/my")
                        .header("authorization",jwt))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        System.out.println(ret.getResponse().getContentAsString());


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
