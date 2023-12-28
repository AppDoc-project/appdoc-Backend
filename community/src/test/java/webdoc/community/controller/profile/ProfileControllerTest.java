package webdoc.community.controller.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import webdoc.community.securityConfig.WithMockCustomUser;
import webdoc.community.service.CommunityService;
import webdoc.community.service.ProfileService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class ProfileControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    CommunityService communityService;

    @MockBean
    ProfileService profileService;

    @DisplayName("profile을 fetch한다")
    @Test
    @WithMockCustomUser
    void fetchProfile() throws Exception {
        mockMvc.perform(get("/community/profile/info"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("본인이 쓴 글을 가져온다")
    @Test
    @WithMockCustomUser
    void fetchOwnPost() throws Exception {
        mockMvc.perform(get("/community/profile/post?limit=3&page=0"))
                .andDo(print())
                .andExpect(status().isOk());
     }

    @DisplayName("본인이 쓴 글을 가져올 때 limit를 명시해야 한다")
    @Test
    @WithMockCustomUser
    void fetchOwnPostWithOutLimit() throws Exception {
        mockMvc.perform(get("/community/profile/post?&page=0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("본인이 쓴 글을 가져올 때 page를 명시해야 한다")
    @Test
    @WithMockCustomUser
    void fetchOwnPostWithOutPage() throws Exception {
        mockMvc.perform(get("/community/profile/post?&limit=0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @DisplayName("본인이 쓴 댓글이 담긴 글을 가져온다")
    @Test
    @WithMockCustomUser
    void fetchOwnThread() throws Exception {
        mockMvc.perform(get("/community/profile/thread?limit=3&page=0"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("본인이 쓴 댓글이 담긴 글을 가져올 때 limit를 명시해야 한다")
    @Test
    @WithMockCustomUser
    void fetchOwnThreadWithOutLimit() throws Exception {
        mockMvc.perform(get("/community/profile/thread?&page=0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("본인이 쓴 댓글이 담긴 글을 가져올 때 page를 명시해야 한다")
    @Test
    @WithMockCustomUser
    void fetchOwnThreadWithOutPage() throws Exception {
        mockMvc.perform(get("/community/profile/thread?&limit=0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("본인이 북마크한 글을 가져온다")
    @Test
    @WithMockCustomUser
    void fetchOwnBookmark() throws Exception {
        mockMvc.perform(get("/community/profile/bookmark?limit=3&page=0"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("본인이 북마크한 글을 가져올 때 limit를 명시해야 한다")
    @Test
    @WithMockCustomUser
    void fetchOwnBookmarkWithOutLimit() throws Exception {
        mockMvc.perform(get("/community/profile/bookmark?&page=0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("본인이 북마크한 글을 가져올 때 page를 명시해야 한다")
    @Test
    @WithMockCustomUser
    void fetchOwnBookmarkWithOutPage() throws Exception {
        mockMvc.perform(get("/community/profile/bookmark?&limit=0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }



}