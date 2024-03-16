package webdoc.community.service;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.Thread;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadOfThreadCreateRequest;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.user.Specialities;
import webdoc.community.domain.entity.user.response.TutorProfileResponse;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.repository.CommunityRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProfileServiceTest {

    @Autowired
    CommunityService communityService;

    @Autowired
    ProfileService profileService;

    @MockBean
    UserService userService;

    @Autowired
    CommunityRepository communityRepository;

    @DisplayName("본인이 작성한 글, 댓글, 북마크 수를 조회한다")
    @Test
    void fetchInfo(){
        //given
        Community com1 = Community
                .builder()
                .name("외과")
                .build();


        UserResponse userResponse = createUser();

        communityRepository.save(com1);

        List<String>list = List.of("www","wwww");

        PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕하세요","ㅋㅋㅋ");
        PostCreateRequest request1 = postCreateRequest(com1.getId(),null,"하이","헤이");
        PostCreateRequest request2 = postCreateRequest(com1.getId(),null,"핫","하하");

        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Post post = communityService.createPost(request, userResponse.getId());
        Post post1 = communityService.createPost(request1, userResponse.getId());
        Post post2 = communityService.createPost(request2,2L);

        // 글 작성 완료
        ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());
        Thread thread = communityService.createThread(userResponse.getId(),threadCreateRequest);

        ThreadCreateRequest threadCreateRequest1 = threadCreateRequest("안녕",post1.getId());
        Thread thread1 = communityService.createThread(userResponse.getId(),threadCreateRequest);

        ThreadCreateRequest threadCreateRequest2 = threadCreateRequest("안녕",post1.getId());
        Thread thread2 = communityService.createThread(4L,threadCreateRequest);

        // 댓글 작성 완료

        communityService.toggleBookmark(userResponse.getId(),post1.getId());
        communityService.toggleBookmark(userResponse.getId(),post1.getId());
        communityService.toggleBookmark(userResponse.getId(),post1.getId());
        communityService.toggleBookmark(userResponse.getId(),post2.getId());
        communityService.toggleBookmark(2L,post2.getId());



        //when
        TutorProfileResponse tutorProfileResponse = profileService.tutorProfileInfo(userResponse);


        //then
        assertThat(tutorProfileResponse)
                .extracting("postCount","threadCount","bookmarkCount","specialities","name")
                .containsExactly(2,2,2,
                        List.of(Specialities.KEYBOARD_INSTRUMENT),"우석우");
     }


     @DisplayName("본인이 작성한 글의 목록을 확인한다")
     @Test
     void fetchOwnPost(){
         //given
         Community com1 = Community
                 .builder()
                 .name("외과")
                 .build();


         UserResponse userResponse = createUser();

         communityRepository.save(com1);

         List<String>list = List.of("www","wwww");

         PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕하세요","ㅋㅋㅋ");
         PostCreateRequest request1 = postCreateRequest(com1.getId(),null,"하이","헤이");
         PostCreateRequest request2 = postCreateRequest(com1.getId(),null,"핫","하하");
         PostCreateRequest request3 = postCreateRequest(com1.getId(),null,"하이","헤이");
         PostCreateRequest request4 = postCreateRequest(com1.getId(),null,"핫","하하");
         PostCreateRequest request5 = postCreateRequest(com1.getId(),null,"하이","헤이");
         PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"핫","하하");

         when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                 .thenReturn(
                         Optional.of(createUser())
                 );

         Post post = communityService.createPost(request, userResponse.getId());
         Post post1 = communityService.createPost(request1, userResponse.getId());
         Post post2 = communityService.createPost(request2,2L);
         Post post3 = communityService.createPost(request3, userResponse.getId());
         Post post4 = communityService.createPost(request4,userResponse.getId());
         Post post5 = communityService.createPost(request5, userResponse.getId());
         Post post6 = communityService.createPost(request6,userResponse.getId());

         // 글 작성 완료
         ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());
         Thread thread = communityService.createThread(userResponse.getId(),threadCreateRequest);

         ThreadCreateRequest threadCreateRequest1 = threadCreateRequest("안녕",post1.getId());
         Thread thread1 = communityService.createThread(userResponse.getId(),threadCreateRequest);

         ThreadCreateRequest threadCreateRequest2 = threadCreateRequest("안녕",post1.getId());
         Thread thread2 = communityService.createThread(4L,threadCreateRequest);

         // 댓글 작성 완료

         communityService.toggleBookmark(userResponse.getId(),post1.getId());
         communityService.toggleBookmark(userResponse.getId(),post1.getId());
         communityService.toggleBookmark(userResponse.getId(),post1.getId());
         communityService.toggleBookmark(userResponse.getId(),post2.getId());
         communityService.toggleBookmark(2L,post2.getId());


         // when
         List<PostResponse> postResponses = profileService.ownPost(userResponse.getId());
         List<PostResponse> postResponses1 = profileService.ownPost(userResponse.getId());

//         // then
//         assertThat(postResponses).hasSize(5);
//         assertThat(postResponses)
//                 .extracting("text")
//                 .containsExactlyInAnyOrder(
//                         "핫","하이","하이","하이","핫"
//                 );
//
//         assertThat(postResponses1).hasSize(1);
//         assertThat(postResponses1)
//                 .extracting("text")
//                 .containsExactlyInAnyOrder("안녕하세요");

      }

    @DisplayName("본인이 작성한 댓글이 담긴 글 목록을 확인한다")
    @Test
    void fetchOwnThread(){
        //given
        Community com1 = Community
                .builder()
                .name("외과")
                .build();


        UserResponse userResponse = createUser();

        communityRepository.save(com1);

        List<String>list = List.of("www","wwww");

        PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕","ㅋㅋㅋ");
        PostCreateRequest request1 = postCreateRequest(com1.getId(),null,"하이","헤이");
        PostCreateRequest request2 = postCreateRequest(com1.getId(),null,"하","하하");
        PostCreateRequest request3 = postCreateRequest(com1.getId(),null,"세요","헤이");
        PostCreateRequest request4 = postCreateRequest(com1.getId(),null,"요","하하");
        PostCreateRequest request5 = postCreateRequest(com1.getId(),null,"하이","헤이");
        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"핫","하하");

        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Post post = communityService.createPost(request, userResponse.getId());
        Post post1 = communityService.createPost(request1, userResponse.getId());
        Post post2 = communityService.createPost(request2,2L);
        Post post3 = communityService.createPost(request3, userResponse.getId());
        Post post4 = communityService.createPost(request4,userResponse.getId());
        Post post5 = communityService.createPost(request5, userResponse.getId());
        Post post6 = communityService.createPost(request6,userResponse.getId());

        // 글 작성 완료
        ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());
        Thread thread = communityService.createThread(userResponse.getId(),threadCreateRequest);

        ThreadCreateRequest threadCreateRequest1 = threadCreateRequest("안녕",post.getId());
        Thread thread1 = communityService.createThread(userResponse.getId(),threadCreateRequest1);

        ThreadCreateRequest threadCreateRequest2 = threadCreateRequest("안녕",post1.getId());
        Thread thread2 = communityService.createThread(4L,threadCreateRequest2);

        ThreadCreateRequest threadCreateRequest3 = threadCreateRequest("안녕",post2.getId());
        Thread thread3 = communityService.createThread(userResponse.getId(),threadCreateRequest3);

        ThreadCreateRequest threadCreateRequest4 = threadCreateRequest("안녕",post2.getId());
        Thread thread4 = communityService.createThread(userResponse.getId(),threadCreateRequest4);

        ThreadCreateRequest threadCreateRequest5 = threadCreateRequest("안녕",post3.getId());
        Thread thread5 = communityService.createThread(userResponse.getId(),threadCreateRequest5);

        ThreadCreateRequest threadCreateRequest6 = threadCreateRequest("안녕",post4.getId());
        Thread thread6 = communityService.createThread(userResponse.getId(),threadCreateRequest6);

        // 댓글 작성 완료


        // when
        List<PostResponse> postResponses = profileService.ownThread(userResponse.getId());
        List<PostResponse> postResponses1 = profileService.ownThread(userResponse.getId());

//        // then
//        assertThat(postResponses).hasSize(3);
//        assertThat(postResponses)
//                .extracting("text")
//                .containsExactlyInAnyOrder(
//                        "요","하","세요"
//                );
//
//        assertThat(postResponses1).hasSize(1);
//        assertThat(postResponses1)
//                .extracting("text")
//                .containsExactlyInAnyOrder("안녕");

    }

    @DisplayName("본인이 북마크 한 글 목록을 확인한다")
    @Test
    void fetchOwnBookmark(){
        //given
        Community com1 = Community
                .builder()
                .name("외과")
                .build();


        UserResponse userResponse = createUser();

        communityRepository.save(com1);

        List<String>list = List.of("www","wwww");

        PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕","ㅋㅋㅋ");
        PostCreateRequest request1 = postCreateRequest(com1.getId(),null,"하이","헤이");
        PostCreateRequest request2 = postCreateRequest(com1.getId(),null,"하","하하");
        PostCreateRequest request3 = postCreateRequest(com1.getId(),null,"세요","헤이");
        PostCreateRequest request4 = postCreateRequest(com1.getId(),null,"요","하하");
        PostCreateRequest request5 = postCreateRequest(com1.getId(),null,"하이","헤이");
        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"핫","하하");

        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Post post = communityService.createPost(request, userResponse.getId());
        Post post1 = communityService.createPost(request1, userResponse.getId());
        Post post2 = communityService.createPost(request2,2L);
        Post post3 = communityService.createPost(request3, userResponse.getId());
        Post post4 = communityService.createPost(request4,userResponse.getId());
        Post post5 = communityService.createPost(request5, userResponse.getId());
        Post post6 = communityService.createPost(request6,userResponse.getId());

        // 글 작성 완료

        communityService.toggleBookmark(userResponse.getId(),post.getId());
        communityService.toggleBookmark(userResponse.getId(),post1.getId());
        communityService.toggleBookmark(userResponse.getId(),post1.getId());
        communityService.toggleBookmark(userResponse.getId(),post1.getId());
        communityService.toggleBookmark(userResponse.getId(),post2.getId());
        communityService.toggleBookmark(userResponse.getId(),post4.getId());
        communityService.toggleBookmark(userResponse.getId(),post2.getId());
        communityService.toggleBookmark(2L,post4.getId());
        communityService.toggleBookmark(userResponse.getId(),post3.getId());
        communityService.toggleBookmark(userResponse.getId(),post5.getId());
        communityService.toggleBookmark(userResponse.getId(),post6.getId());

        // when
        List<PostResponse> postResponses = profileService.ownBookmark(userResponse.getId());
        List<PostResponse> postResponses1 = profileService.ownBookmark(userResponse.getId());

        // then
//        assertThat(postResponses).hasSize(5);
//        assertThat(postResponses)
//                .extracting("text")
//                .containsExactlyInAnyOrder(
//                        "하이","요","세요","하이","핫"
//                );
//
//        assertThat(postResponses1).hasSize(1);
//        assertThat(postResponses1)
//                .extracting("text")
//                .containsExactlyInAnyOrder("안녕");

    }





    private ThreadCreateRequest threadCreateRequest(String text, Long postId){
        return ThreadCreateRequest.builder()
                .text(text)
                .postId(postId)
                .build();

    }

    private ThreadOfThreadCreateRequest threadOfThreadCreateRequest(String text, Long postId, Long parentId){
        return ThreadOfThreadCreateRequest.builder()
                .parentThreadId(parentId)
                .postId(postId)
                .text(text)
                .build();
    }



    private PostCreateRequest postCreateRequest(Long communityId, List<String> list, String text, String title){
        PostCreateRequest request =
                PostCreateRequest.builder()
                        .communityId(communityId)
                        .text(text)
                        .title(title)
                        .addresses(list)
                        .build();

        return  request;
    }


    public UserResponse createUser(){
        return UserResponse.builder()
                .contact("01025045779")
                .email("1dilumn0@gmail.com")
                .id(1L)
                .nickName("우석우")
                .name("우석우")
                .profile("naver.com")
                .specialities(List.of(Specialities.KEYBOARD_INSTRUMENT))
                .build();
    }



}