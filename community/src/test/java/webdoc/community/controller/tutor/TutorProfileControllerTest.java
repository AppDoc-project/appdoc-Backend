package webdoc.community.controller.tutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import webdoc.community.domain.entity.pick.request.PickToggleRequest;
import webdoc.community.securityConfig.WithMockCustomUser;
import webdoc.community.service.TutorProfileService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class TutorProfileControllerTest {

    @MockBean
    TutorProfileService tutorProfileService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;


    @DisplayName("찜하기를 수행한다")
    @Test
    @WithMockCustomUser
    void tutorPickToggle() throws Exception {

        PickToggleRequest pickToggleRequest = new PickToggleRequest();
        pickToggleRequest.setTutorId(2L);

        mockMvc.perform(post("/tutor/pick")
                        .content(objectMapper.writeValueAsString(pickToggleRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("찜하기는 값검증을 수행한다")
    @Test
    @WithMockCustomUser
    void tutorPickToggleValidation() throws Exception {

        PickToggleRequest pickToggleRequest = new PickToggleRequest();

        mockMvc.perform(post("/tutor/pick")
                        .content(objectMapper.writeValueAsString(pickToggleRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }



}
