package webdoc.community.controller.community;


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
import webdoc.community.domain.entity.like.request.CreateRequestWithPostId;
import webdoc.community.domain.entity.post.request.PostModifyRequest;
import webdoc.community.domain.entity.post.request.ThreadCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadOfThreadCreateRequest;
import webdoc.community.domain.entity.report.request.ReportCreateRequest;
import webdoc.community.securityConfig.WithMockCustomUser;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.service.CommunityService;
import java.util.List;
import java.util.NoSuchElementException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class CommunityControllerTest {


    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    CommunityService communityService;

    @DisplayName("게시글을 불러온다")
    @Test
    @WithMockCustomUser
    void fetchPostsFirst() throws Exception {

        mockMvc.perform(get("/community/post?scroll=false&limit=5&communityId=1"))
                .andDo(print())
                .andExpect(status().isOk());


     }
    @DisplayName("게시글을 불러올 때, scroll 이 없으면 안된다")
    @Test
    @WithMockCustomUser
    void fetchPostsWithoutScroll() throws Exception {
        mockMvc.perform(get("/community/post?limit=5&communityId=1"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("게시글을 불러올 때, 없는 communityId를 넣을 수 없다")
    @Test
    @WithMockCustomUser
    void fetchPostsWithInvalidCommunityId() throws Exception {
        when(communityService.getPostsWithLimit(any(),any(),any())).thenThrow(new NoSuchElementException(""));
        mockMvc.perform(get("/community/post?limit=5&communityId=1"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus").value(400));

     }

    @DisplayName("커뮤니티 리스트 불러오기를 수행한다")
    @Test
    @WithMockUser
    void fetchCommunities() throws Exception {
        mockMvc.perform(get("/community/list"))
                .andDo(print())
                .andExpect(status().isOk());
     }
    @DisplayName("게시글 작성하기를 수행한다")
    @WithMockCustomUser
    @Test
    void createPost() throws Exception {
        PostCreateRequest postCreateRequest =
                postCreateRequest();

        mockMvc.perform(post("/community/post")
                        .content(objectMapper.writeValueAsString(postCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("게시글에 5개 이상의 사진을 넣을 수 없다")
    @WithMockCustomUser
    @Test
    void createPostWithMoreThan5pictures() throws Exception {
        PostCreateRequest postCreateRequest =
                postCreateRequest();

        postCreateRequest.setAddresses(
                List.of(
                        "a","b","c","d","e","f"
                )
        );

        mockMvc.perform(post("/community/post")
                        .content(objectMapper.writeValueAsString(postCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError());
     }

    @DisplayName("게시글 생성은 값 검증을 수행한다")
    @WithMockCustomUser
    @Test
    void validateWhenCreatePost() throws Exception {
        PostCreateRequest postCreateRequest =
                postCreateRequest();
        postCreateRequest.setTitle(null);

        mockMvc.perform(post("/community/post")
                        .content(objectMapper.writeValueAsString(postCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("특정 글을 불러 올 수 있다")
    @Test
    @WithMockCustomUser
    void fetchCertainPost() throws Exception {

        mockMvc.perform(get("/community/post/4"))
                .andDo(print())
                .andExpect(status().isOk());
     }

     @DisplayName("특정 글을 불러 올 때 없는 postId로 불러 올 수 없다")
     @Test
     @WithMockCustomUser
     void fetchCertainPostWithInvalidPostId() throws Exception {
         when(communityService.getCertainPost(any(),any(),any())).thenThrow(new NoSuchElementException(""));
         mockMvc.perform(get("/community/post/4"))
                 .andDo(print())
                 .andExpect(status().is4xxClientError());
      }

    @DisplayName("특정 글을 불러 올 때 유효하지 않은 postId를 입력하면 안된다")
    @Test
    @WithMockCustomUser
    void fetchCertainPostWithWrongPostId() throws Exception {

        mockMvc.perform(get("/community/post/hey"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
    @DisplayName("댓글을 작성할 수 있다")
    @Test
    @WithMockCustomUser
    void createThread() throws Exception {
        ThreadCreateRequest request = threadCreateRequest("안녕안녕",3L);
        mockMvc.perform(post("/community/thread")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
     }

    @DisplayName("빈 댓글을 작성할 수 없다")
    @Test
    @WithMockCustomUser
    void createThreadWithBlank() throws Exception {
        ThreadCreateRequest request = threadCreateRequest("",3L);
        mockMvc.perform(post("/community/thread")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
     @DisplayName("댓글은 500자를 초과할 수 없다")
     @Test
     @WithMockCustomUser
     void createThreadWithMoreThan500Text() throws Exception {
         ThreadCreateRequest request = threadCreateRequest("안녕안안안녕안안안녕안안녕안안녕안안안녕안안녕안안녕안안녕안안안녕안안녕안안녕안" +
                 "안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕" +
                 "안녕안녕안안녕안안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕" +
                 "안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안안녕안안안녕안안녕안안녕안안녕안안안녕안안녕안안녕안" +
                 "안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕안안녕안안녕안" +
                 "녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕안안녕안안녕안안안녕안안녕안안녕안안녕안안안녕" +
                 "안안녕안안녕안안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕안안녕안안녕안녕안녕안녕녕안녕안" +
                 "녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안안녕안안안녕안안녕안안녕안안녕안안안녕안안녕안안녕안안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕" +
                 "안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕안안녕안안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕",3L);
         mockMvc.perform(post("/community/thread")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                 .andDo(print())
                 .andExpect(status().is4xxClientError())
                 .andExpect(jsonPath("$.code").value(400));
     }

    @DisplayName("대댓글을 작성할 수 있다")
    @Test
    @WithMockCustomUser
    void createThreadOfThread() throws Exception {
        ThreadOfThreadCreateRequest request = threadOfThreadCreateRequest("안녕안녕",3L,2L);
        mockMvc.perform(post("/community/thread_thread")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("빈 댓글을 작성할 수 없다")
    @Test
    @WithMockCustomUser
    void createThreadOfThreadWithBlank() throws Exception {
        ThreadOfThreadCreateRequest request = threadOfThreadCreateRequest("",3L,2L);
        mockMvc.perform(post("/community/thread_thread")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
    @DisplayName("댓글은 500자를 초과할 수 없다")
    @Test
    @WithMockCustomUser
    void createThreadOfThreadWithMoreThan500Text() throws Exception {
        ThreadOfThreadCreateRequest request = threadOfThreadCreateRequest("안녕안안안녕안안안녕안안녕안안녕안안안녕안안녕안안녕안안녕안안안녕안안녕안안녕안" +
                "안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕" +
                "안녕안녕안안녕안안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕" +
                "안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안안녕안안안녕안안녕안안녕안안녕안안안녕안안녕안안녕안" +
                "안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕안안녕안안녕안" +
                "녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕안안녕안안녕안안안녕안안녕안안녕안안녕안안안녕" +
                "안안녕안안녕안안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕안안녕안안녕안녕안녕안녕녕안녕안" +
                "녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안안녕안안안녕안안녕안안녕안안녕안안안녕안안녕안안녕안안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕" +
                "안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕안안녕안안녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕안녕녕안녕안녕녕안녕안녕녕안녕안녕",3L,2L);
        mockMvc.perform(post("/community/thread_thread")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(400));
    }

    @DisplayName("전체 게시글을 검색한다")
    @Test
    @WithMockCustomUser
    void searchAll() throws Exception {

        mockMvc.perform(get("/community/search?scroll=false&limit=5&keyword=ㅋㅋㅋ&postSearchType=TITLE"))
                .andDo(print())
                .andExpect(status().isOk());


    }

    @DisplayName("전체 게시글을 스크롤로 검색한다")
    @Test
    @WithMockCustomUser
    void searchAllWithScroll() throws Exception {

        mockMvc.perform(get("/community/search?scroll=true&postId=4&limit=5&keyword=ㅋㅋㅋ&postSearchType=TITLE"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("전체 게시글을 스크롤로 검색할 때, postId를 넣지 않으면 예외가 발생한다")
    @Test
    @WithMockCustomUser
    void searchAllWithScrollWithoutPostId() throws Exception {

        mockMvc.perform(get("/community/search?scroll=true&limit=5&keyword=ㅋㅋㅋ&postSearchType=TITLE"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("게시판 별 게시글을 검색한다")
    @Test
    @WithMockCustomUser
    void searchCommunity() throws Exception {

        mockMvc.perform(get("/community/search/1?scroll=false&limit=5&communityId=4&keyword=ㅋㅋㅋ&postSearchType=TITLE"))
                .andDo(print())
                .andExpect(status().isOk());


    }



    @DisplayName("게시판 별 게시글을 스크롤로 검색한다")
    @Test
    @WithMockCustomUser
    void searchCommunityWithScroll() throws Exception {

        mockMvc.perform(get("/community/search?scroll=true&postId=4&communityId=3&limit=5&keyword=ㅋㅋㅋ&postSearchType=TITLE"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("게시판 별 게시글을 스크롤로 검색할 때, postId를 넣지 않으면 예외가 발생한다")
    @Test
    @WithMockCustomUser
    void searchCommuntiyWithScrollWithoutPostId() throws Exception {

        mockMvc.perform(get("/community/search?scroll=true&limit=5&communityId=3&keyword=ㅋㅋㅋ&postSearchType=TITLE"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("좋아요를 등록한다")
    @Test
    @WithMockCustomUser
    void enrollLike() throws Exception {
        when(communityService.enrollLike(any(),any())).thenReturn(true);
        CreateRequestWithPostId likeCreateRequest = new CreateRequestWithPostId();
        likeCreateRequest.setPostId(5L);
        mockMvc.perform(post("/community/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeCreateRequest)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
     }

    @DisplayName("좋아요를 등록할 때 postID값이 없으면 실패한다")
    @Test
    @WithMockCustomUser
    void enrollLikeWithoutPostId() throws Exception {
        CreateRequestWithPostId likeCreateRequest = new CreateRequestWithPostId();
        mockMvc.perform(post("/community/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeCreateRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("북마크를 등록한다")
    @Test
    @WithMockCustomUser
    void toggleBookmark() throws Exception {
        CreateRequestWithPostId bookmarkCreateRequest = new CreateRequestWithPostId();
        bookmarkCreateRequest.setPostId(5L);
        mockMvc.perform(post("/community/bookmark")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookmarkCreateRequest)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("북마크를 등록할 때 postId값이 없으면 실패한다")
    @Test
    @WithMockCustomUser
    void toggleBookmarkWithoutPostId() throws Exception {
        CreateRequestWithPostId bookmarkCreateRequest = new CreateRequestWithPostId();
        mockMvc.perform(post("/community/bookmark")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookmarkCreateRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("게시글을 삭제한다")
    @Test
    @WithMockCustomUser
    void deletePost() throws Exception {
        mockMvc.perform(delete("/community/post?postId=4"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
     }

    @DisplayName("postId 없이 게시글을 삭제할 수 없다")
    @Test
    @WithMockCustomUser
    void deletePostWithoutPostId() throws Exception {
        mockMvc.perform(delete("/community/post"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("댓글을 삭제한다")
    @Test
    @WithMockCustomUser
    void deleteThread() throws Exception {
        mockMvc.perform(delete("/community/thread?threadId=4"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("theadId가 없으면 댓글을 삭제할 수 없다")
    @Test
    @WithMockCustomUser
    void deleteThreadWithOutThreadId() throws Exception {
        mockMvc.perform(delete("/community/thread"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("게시글을 신고한다")
    @Test
    @WithMockCustomUser
    void reportPost() throws Exception {
        ReportCreateRequest request = new ReportCreateRequest();
        request.setReason("하하");
        request.setId(5L);
        mockMvc.perform(post("/community/report/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
     }

    @DisplayName("신고 사유가 100자를 넘을 수 없다")
    @Test
    @WithMockCustomUser
    void reportPostWithMoreThan100Chars() throws Exception {
        ReportCreateRequest request = new ReportCreateRequest();
        request.setReason("하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하");
        request.setId(5L);
        mockMvc.perform(post("/community/report/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("신고할 때 id를 제시해야 한다 ")
    @Test
    @WithMockCustomUser
    void reportPostWithoutPostId() throws Exception {
        ReportCreateRequest request = new ReportCreateRequest();
        request.setReason("하하하하하하하하하하하하하하하하하하하하");
        mockMvc.perform(post("/community/report/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
    @DisplayName("댓글을 신고한다")
    @Test
    @WithMockCustomUser
    void reportThread() throws Exception {
        ReportCreateRequest request = new ReportCreateRequest();
        request.setReason("하하");
        request.setId(5L);
        mockMvc.perform(post("/community/report/thread")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("신고 사유가 100자를 넘을 수 없다")
    @Test
    @WithMockCustomUser
    void reportThreadWithMoreThan100Chars() throws Exception {
        ReportCreateRequest request = new ReportCreateRequest();
        request.setReason("하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하");
        request.setId(5L);
        mockMvc.perform(post("/community/report/thread")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("신고할 때 id를 제시해야 한다 ")
    @Test
    @WithMockCustomUser
    void reportThreadWithoutPostId() throws Exception {
        ReportCreateRequest request = new ReportCreateRequest();
        request.setReason("하하하하하하하하하하하하하하하하하하하하");
        mockMvc.perform(post("/community/report/thread")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("게시글 수정하기를 수행한다")
    @WithMockCustomUser
    @Test
    void modifyPost() throws Exception {
        PostModifyRequest postCreateRequest =
                postModifyRequest();


        mockMvc.perform(patch("/community/post")
                        .content(objectMapper.writeValueAsString(postCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("게시글에 5개 이상의 사진을 넣을 수 없다")
    @WithMockCustomUser
    @Test
    void modifyPostWithMoreThan5pictures() throws Exception {
        PostModifyRequest postModifyRequest =
                postModifyRequest();

        postModifyRequest.setAddresses(
                List.of(
                        "A","b","c","d","e","f","g"
                )
        );

        mockMvc.perform(post("/community/post")
                        .content(objectMapper.writeValueAsString(postModifyRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("게시글 수정은 값 검증을 수행한다")
    @WithMockCustomUser
    @Test
    void validateWhenModifyPost() throws Exception {
        PostModifyRequest postModifyRequest =
                postModifyRequest();
        postModifyRequest.setTitle(null);

        mockMvc.perform(post("/community/post")
                        .content(objectMapper.writeValueAsString(postModifyRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
    private ThreadOfThreadCreateRequest threadOfThreadCreateRequest(String text, Long postId, Long parentId){
        return ThreadOfThreadCreateRequest.builder()
                .parentThreadId(parentId)
                .postId(postId)
                .text(text)
                .build();
    }



    private PostCreateRequest postCreateRequest(){
        return PostCreateRequest
                .builder()
                .communityId(1L)
                .text("asdsds")
                .title("안녕하세요")
                .addresses(List.of("a","B"))
                .build();
    }

    private ThreadCreateRequest threadCreateRequest(String text, Long postId){
        return ThreadCreateRequest.builder()
                .text(text)
                .postId(postId)
                .build();

    }

    private PostModifyRequest postModifyRequest(){
        PostModifyRequest request =
                PostModifyRequest.builder()
                        .text("안녕")
                        .title("하세요")
                        .postId(5L)
                        .addresses(null)
                        .build();

        return  request;
    }





}
