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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import webdoc.authentication.domain.entity.user.request.*;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.domain.entity.user.tutor.request.TutorCreateRequest;
import webdoc.authentication.domain.entity.user.tutor.request.TutorSpecialityRequest;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.securityConfig.WithMockCustomUser;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.service.SettingService;
import webdoc.authentication.utility.messageprovider.ResponseCodeProvider;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class AuthSettingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

    @MockBean
    SettingService settingService;

    @MockBean
    UserRepository userRepository;

    @DisplayName("비밀번호 변경을 테스트한다")
    @Test
    @WithMockCustomUser
    void changePassword() throws Exception {

        PasswordChangeRequest passwordChangeRequest = passwordChangeRequest("dntjrdn78!","dsadasd!2");

        mockMvc.perform(patch("/auth/setting/password")
                        .content(objectMapper.writeValueAsString(passwordChangeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
     }

     @DisplayName("비밀번호 변경은 값검증을 수행한다")
     @Test
     @WithMockCustomUser
     void noChangeWithOutPassword() throws Exception {
         PasswordChangeRequest passwordChangeRequest = passwordChangeRequest("dntjrdn78","dsadasd!2");

         mockMvc.perform(patch("/auth/setting/password")
                         .content(objectMapper.writeValueAsString(passwordChangeRequest))
                         .contentType(MediaType.APPLICATION_JSON))
                 .andDo(print())
                 .andExpect(status().is4xxClientError())
                 .andExpect(jsonPath("$.code").value(ResponseCodeProvider.BINDING_FAILURE));
      }

   @DisplayName("연락처 변경을 테스트 한다")
   @Test
   @WithMockCustomUser
   void changeContact() throws Exception {
        ContactChangeRequest contactChangeRequest
                = contactChangeRequest("01042325779","asdsda!2");
       mockMvc.perform(patch("/auth/setting/contact")
                       .content(objectMapper.writeValueAsString(contactChangeRequest))
                       .contentType(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("연락처 변경은 값검증을 수행한다")
    @Test
    @WithMockCustomUser
    void changeContactWithInvalid() throws Exception {
        ContactChangeRequest contactChangeRequest
                = contactChangeRequest("0104232571279","sd");
        mockMvc.perform(patch("/auth/setting/contact")
                        .content(objectMapper.writeValueAsString(contactChangeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("자기소개를 수정한다")
    @Test
    @WithMockCustomUser
    void changeSelfDescription() throws Exception {
        SelfDescriptionChangeRequest selfDescriptionChangeRequest
                = selfDescriptionChangeRequest("kkkk");

        mockMvc.perform(patch("/auth/setting/selfdescription")
                        .content(objectMapper.writeValueAsString(selfDescriptionChangeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("자기소개 수정은 값 검증을 수행한다")
    @Test
    @WithMockCustomUser
    void changeSelfDescriptionWithInvalid() throws Exception {
        SelfDescriptionChangeRequest selfDescriptionChangeRequest
                = selfDescriptionChangeRequest("");

        mockMvc.perform(patch("/auth/setting/selfdescription")
                        .content(objectMapper.writeValueAsString(selfDescriptionChangeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("닉네임을 수정한다")
    @Test
    @WithMockCustomUser
    void changeNickName() throws Exception {
        NickNameChangeRequest nickNameChangeRequest =
                nickNameChangeRequest("하하하");

        mockMvc.perform(patch("/auth/setting/nickname")
                        .content(objectMapper.writeValueAsString(nickNameChangeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("닉네임을 수정은 값검증을 수행한다")
    @Test
    @WithMockCustomUser
    void changeNickNameWithInvalid() throws Exception {
        NickNameChangeRequest nickNameChangeRequest =
                nickNameChangeRequest("?!!@");



        mockMvc.perform(patch("/auth/setting/nickname")
                        .content(objectMapper.writeValueAsString(nickNameChangeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        nickNameChangeRequest = nickNameChangeRequest("aaaaaaaaaaasd");

        mockMvc.perform(patch("/auth/setting/nickname")
                        .content(objectMapper.writeValueAsString(nickNameChangeRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("튜터 전공을 변경한다")
    @Test
    @WithMockCustomUser
    void changeSpecialities() throws Exception {
        TutorSpecialityRequest request
                = tutorSpecialityRequest("adsdsd",List.of(Specialities.DRUM,Specialities.COMPOSITION));

        mockMvc.perform(patch("/auth/setting/speciality")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
     }

    @DisplayName("튜터 전공변경은 값검증을 수행한다")
    @Test
    @WithMockCustomUser
    void changeSpecialitiesWithInvalid() throws Exception {
        TutorSpecialityRequest request
                = tutorSpecialityRequest("adsdsd",null);

        mockMvc.perform(patch("/auth/setting/speciality")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        request = tutorSpecialityRequest("Sdasd",List.of(Specialities.DRUM,Specialities.DRUM));
        mockMvc.perform(patch("/auth/setting/speciality")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("회원탈퇴를 수행한다")
    @Test
    @WithMockCustomUser
    void deleteAccount() throws Exception {
        AccountClosureRequest accountClosureRequest = accountClosureRequest("a!aa1aaaa");
        mockMvc.perform(post("/auth/setting/account")
                        .content(objectMapper.writeValueAsString(accountClosureRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
     }

     AccountClosureRequest accountClosureRequest(String password){
        return AccountClosureRequest
                .builder()
                .password(password)
                .build();
     }
     TutorSpecialityRequest tutorSpecialityRequest(String address, List<Specialities> specialities){
        return TutorSpecialityRequest
                .builder()
                .authenticationAddress(address)
                .specialities(specialities)
                .build();
     }

    ContactChangeRequest contactChangeRequest(String contact,String password){
        return ContactChangeRequest
                .builder()
                .contact(contact)
                .currentPassword(password)
                .build();
    }
     PasswordChangeRequest passwordChangeRequest(String cur,String cha){
        return PasswordChangeRequest.builder()
                .changedPassword(cha)
                .currentPassword(cur)
                .build();
     }

     NickNameChangeRequest nickNameChangeRequest(String nickName){
        return NickNameChangeRequest
                .builder()
                .nickName(nickName)
                .build();
     }

     SelfDescriptionChangeRequest selfDescriptionChangeRequest(String selfDescription){
        return SelfDescriptionChangeRequest
                .builder()
                .selfDescription(selfDescription)
                .build();
     }

}
