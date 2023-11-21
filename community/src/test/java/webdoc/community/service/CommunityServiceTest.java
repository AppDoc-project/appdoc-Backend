package webdoc.community.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.community.CommunityResponse;
import webdoc.community.domain.entity.post.Picture;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.Thread;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadOfThreadCreateRequest;
import webdoc.community.domain.entity.post.response.PostDetailResponse;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.post.response.ThreadResponse;
import webdoc.community.domain.entity.user.UserResponse;
import webdoc.community.domain.entity.user.tutor.enums.Specialities;
import webdoc.community.repository.CommunityRepository;
import webdoc.community.repository.PostRepository;
import webdoc.community.repository.ThreadRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommunityServiceTest {
    @Autowired
    CommunityService communityService;
    @Autowired
    CommunityRepository communityRepository;

    @MockBean
    UserService userService;
    @Autowired
    ThreadRepository threadRepository;


    @Autowired
    PostRepository postRepository;

    @DisplayName("모든 게시판 리스트를 가져온다")
    @Test
    void fetchAllCommunities(){
        //given

        Community com1 = Community
                .builder()
                .name("외과")
                .build();
        Community com2 = Community
                .builder()
                .name("정형외과")
                .build();
        Community com3 = Community
                .builder()
                .name("내과")
                .build();

        communityRepository.saveAll(List.of(com1,com2,com3));

        //when
        List<CommunityResponse> responses = communityService.getAllCommunities();

        //then
        assertThat(responses).hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrder("외과", "정형외과", "내과");

        assertThat(responses)
                .extracting("id")
                .allSatisfy(id -> {
                    assertThat(id).isNotNull();
                    assertThat(id).isInstanceOf(Long.class);

                });
     }

     @DisplayName("게시글을 등록한다")
     @Test
     void createPost(){
         //given
         Community com1 = Community
                 .builder()
                 .name("외과")
                 .build();



         communityRepository.save(com1);

         List<PostCreateRequest.AddressAndPriority> list = new ArrayList<>();
         list.add(new PostCreateRequest.AddressAndPriority("www",1));
         list.add(new PostCreateRequest.AddressAndPriority("wwwt",2));

         PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕하세요","ㅋㅋㅋ");

         when(userService.fetchUserResponseFromAuthServer(any(),any(),any(),any()))
                 .thenReturn(
                         Optional.of(createUser())
                 );




         //when
         Post post = communityService.createPost(request,1L,"");

         List<Picture> pictures = post.getPictures();


         //then
         assertThat(post).isNotNull()
                         .extracting("community","text","userId")
                                 .contains(com1,"안녕하세요",1L);

         // 사진이 올바르게 저장됨
         assertThat(pictures).extracting("address","priority")
                 .containsExactlyInAnyOrder(
                         tuple("www",1),
                         tuple("wwwt",2)
                 );

      }

      @DisplayName("존재하지 않는 게시판에 게시글을 등록하면 실패한다")
      @Test
      void createPostWithInvalidCommunity(){


          List<PostCreateRequest.AddressAndPriority> list = new ArrayList<>();
          list.add(new PostCreateRequest.AddressAndPriority(null,1));
          list.add(new PostCreateRequest.AddressAndPriority("wwwt",2));

          PostCreateRequest request = postCreateRequest(1L,list,"안녕하세요","반가워요");
          when(userService.fetchUserResponseFromAuthServer(any(),any(),any(),any()))
                  .thenReturn(
                          Optional.of(createUser())
                  );


          //when + then

          assertThatThrownBy(()->communityService.createPost(request,1L,""))
                  .isInstanceOf(NoSuchElementException.class);
       }

      @DisplayName("우선 순위또는 주소가 없으면 게시글 등록에 실패한다")
      @Test
      void createPostWithoutAddressOrPriority(){
          //given
          Community com1 = Community
                  .builder()
                  .name("외과")
                  .build();

          when(userService.fetchUserResponseFromAuthServer(any(),any(),any(),any()))
                  .thenReturn(
                          Optional.of(createUser())
                  );
          communityRepository.save(com1);

          List<PostCreateRequest.AddressAndPriority> list = new ArrayList<>();
          list.add(new PostCreateRequest.AddressAndPriority(null,1));
          list.add(new PostCreateRequest.AddressAndPriority("wwwt",2));

          PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕하세요","반가워요");


          //when + then
          assertThatThrownBy(()->communityService.createPost(request,1L,""))
                  .isInstanceOf(IllegalArgumentException.class);
       }

       @DisplayName("게시판에서 특정 개수만큼 게시물을 가져온다")
       @Test
       void fetchPostsWithLimit(){
           //given
           Community com1 = Community
                   .builder()
                   .name("외과")
                   .build();


           when(userService.fetchUserResponseFromAuthServer(any(),any(),any(),any()))
                   .thenReturn(
                           Optional.of(createUser())
                   );
           UserResponse user = createUser();
           communityRepository.save(com1);

           List<PostCreateRequest.AddressAndPriority> pictures = new ArrayList<>();
           pictures.add(new PostCreateRequest.AddressAndPriority("Wwww",1));
           pictures.add(new PostCreateRequest.AddressAndPriority("wwwt",2));


           PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
           PostCreateRequest request1 = postCreateRequest(com1.getId(),null,"두번째","ㅋㅋㅋㅋ");
           PostCreateRequest request2 = postCreateRequest(com1.getId(),null,"세번째","ㅋㅋㅋㅋ");
           PostCreateRequest request3 = postCreateRequest(com1.getId(),null,"네번째","ㅋㅋㅋㅋ");
           PostCreateRequest request4 = postCreateRequest(com1.getId(),null,"다섯번째","ㅋㅋㅋㅋ");
           PostCreateRequest request5 = postCreateRequest(com1.getId(),null,"여섯번째","ㅋㅋㅋㅋ");
           PostCreateRequest request6 = postCreateRequest(com1.getId(),pictures,"일곱번째","ㅋㅋㅋㅋ");


           //when
           Post post = communityService.createPost(request,user.getId(),"");
           Post post2 = communityService.createPost(request1,user.getId(),"");
           Post post3 = communityService.createPost(request2,user.getId(),"");
           Post post4 = communityService.createPost(request3,user.getId(),"");
           Post post5 = communityService.createPost(request4,user.getId(),"");
           Post post6 = communityService.createPost(request5,user.getId(),"");
           Post post7 = communityService.createPost(request6,user.getId(),"");

           //then
           List<PostResponse> list1 = communityService.getPostsWithLimit(com1.getId(),5,"");
           List<PostResponse> list2 = communityService.getPostsWithLimitAndIdAfter(com1.getId(),post7.getId(),5,"");
           assertThat(list1).hasSize(5);
           assertThat(list2).hasSize(5);
           list2.forEach(System.out::println);
        }

        @DisplayName("특정 게시글을 불러온다")
        @Test
        void fetchPostById(){
            //given
            Community com1 = Community
                    .builder()
                    .name("외과")
                    .build();

            when(userService.fetchUserResponseFromAuthServer(any(),any(),any(),any()))
                    .thenReturn(
                            Optional.of(createUser())
                    );

            UserResponse user = createUser();
            communityRepository.save(com1);

            List<PostCreateRequest.AddressAndPriority> pictures = new ArrayList<>();
            pictures.add(new PostCreateRequest.AddressAndPriority("Wwww",1));
            pictures.add(new PostCreateRequest.AddressAndPriority("wwwt",2));

            PostCreateRequest request = postCreateRequest(com1.getId(),pictures,"안녕하세요","ㅋㅋㅋ");
            Post post = communityService.createPost(request,user.getId(),"");



            //when
            PostDetailResponse response = communityService.getCertainPost(post.getId(),user.getId(),"");


            //then
            assertThat(response).isNotNull();
            assertThat(response)
                    .extracting(
                            "id","userId","title",
                            "nickName","profile","bookmarkCount",
                            "likeCount","threadCount","mediaCount",
                            "createdAt","isTutor","bookmarkYN","text","view")
                    .containsExactly(
                        post.getId(),post.getUserId(),post.getTitle(),
                            user.getNickName(),user.getProfile(),
                            post.getBookmarks().size(), post.getLikes().size(), post.getThreads().size(),
                            post.getPictures().size(), post.getCreatedAt(),user.getIsTutor(),
                            false,post.getText(),1L
                    );
         }

         @DisplayName("댓글을 작성한다")
         @Test
         void createThread(){
             //given
             Community com1 = Community
                     .builder()
                     .name("외과")
                     .build();
             when(userService.fetchUserResponseFromAuthServer(any(),any(),any(),any()))
                     .thenReturn(
                             Optional.of(createUser())
                     );

             UserResponse user = createUser();
             communityRepository.save(com1);
             PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
             Post post = communityService.createPost(request,user.getId(),"");
             ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());

             //when
             Thread thread = communityService.createThread(user.getId(),threadCreateRequest);

             //then
             assertThat(thread).extracting("text","parent","userId","post")
                     .containsExactly(threadCreateRequest.getText(),null,
                     user.getId(),post);
             assertThat(thread.getId()).isNotNull();
          }

          @DisplayName("유효하지 않은 게시글 아이디로 댓글을 등록할 수 없다")
          @Test
          void createThreadWithWrongPostId(){
              //given
              UserResponse user = createUser();
              ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",5L);

              //when + then
              assertThatThrownBy(()->{
                  communityService.createThread(user.getId(),threadCreateRequest);
              }).isInstanceOf(NoSuchElementException.class);

           }
           @DisplayName("대댓글을 작성한다")
           @Test
           void createThreadOfThread(){
               //given
               Community com1 = Community
                       .builder()
                       .name("외과")
                       .build();
               when(userService.fetchUserResponseFromAuthServer(any(),any(),any(),any()))
                       .thenReturn(
                               Optional.of(createUser())
                       );

               UserResponse user = createUser();
               communityRepository.save(com1);
               PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
               Post post = communityService.createPost(request,user.getId(),"");
               ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());
               Thread thread = communityService.createThread(user.getId(),threadCreateRequest);


               ThreadOfThreadCreateRequest threadOfThreadCreateRequest = threadOfThreadCreateRequest("안녕", post.getId(), thread.getId());


               //when
               Thread child = communityService.createThreadOfThread(threadOfThreadCreateRequest,user.getId());

               //then
               assertThat(child).extracting("text","parent","userId","post")
                       .containsExactly(threadOfThreadCreateRequest.getText(),thread,
                               user.getId(),post);
               assertThat(thread.getId()).isNotNull();

            }

            @DisplayName("유효하지 않은 parentId로 대댓글을 작성할 수 없다")
            @Test
            void createThreadOfThreadWithInvalidParentId(){
                //given
                Community com1 = Community
                        .builder()
                        .name("외과")
                        .build();

                UserResponse user = createUser();
                when(userService.fetchUserResponseFromAuthServer(any(),any(),any(),any()))
                        .thenReturn(
                                Optional.of(createUser())
                        );


                communityRepository.save(com1);
                PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
                Post post = communityService.createPost(request,user.getId(),"");
                ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());
                communityService.createThread(user.getId(),threadCreateRequest);


                ThreadOfThreadCreateRequest threadOfThreadCreateRequest = threadOfThreadCreateRequest("안녕", post.getId(), 1000L);


                //when
                assertThatThrownBy(()->{
                    communityService.createThreadOfThread(threadOfThreadCreateRequest,user.getId());
                }).isInstanceOf(NoSuchElementException.class);
             }
             @DisplayName("특정 게시물의 댓글리스트를 불러온다")
             @Test
             void fetchThreadList(){
                 //given
                 Community com1 = Community
                         .builder()
                         .name("외과")
                         .build();

                 UserResponse user = createUser();
                 when(userService.fetchUserResponseFromAuthServer(any(),any(),any(),any()))
                         .thenReturn(
                                 Optional.of(createUser())
                         );


                 communityRepository.save(com1);
                 PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
                 Post post = communityService.createPost(request,user.getId(),"");


                 ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());
                 ThreadCreateRequest threadCreateRequest2 = threadCreateRequest("안녕하",post.getId());
                 ThreadCreateRequest threadCreateRequest3 = threadCreateRequest("안녕하세요",post.getId());

                 Thread thread = communityService.createThread(user.getId(),threadCreateRequest);
                 Thread thread2 = communityService.createThread(user.getId(),threadCreateRequest2);
                 Thread thread3 = communityService.createThread(user.getId(),threadCreateRequest3);

                 ThreadOfThreadCreateRequest threadOfThreadCreateRequest = threadOfThreadCreateRequest("안녕", post.getId(), thread.getId());
                 ThreadOfThreadCreateRequest threadOfThreadCreateRequest2 = threadOfThreadCreateRequest("안녕ㅇㅇ", post.getId(), thread.getId());
                 ThreadOfThreadCreateRequest threadOfThreadCreateRequest3 = threadOfThreadCreateRequest("안녕ㅇㅇㅇ", post.getId(), thread2.getId());

                 Thread child = communityService.createThreadOfThread(threadOfThreadCreateRequest,user.getId());
                 Thread child2 =  communityService.createThreadOfThread(threadOfThreadCreateRequest2,user.getId());
                 Thread child3 =  communityService.createThreadOfThread(threadOfThreadCreateRequest3,user.getId());


                 //when
                 List<ThreadResponse> threadResponses = communityService.getThreadByPostId(post.getId(),"");
                 //then
                 assertThat(threadResponses).hasSize(3);
              }




    private ThreadCreateRequest threadCreateRequest(String text,Long postId){
        return ThreadCreateRequest.builder()
                .text(text)
                .postId(postId)
                .build();

    }

    private ThreadOfThreadCreateRequest threadOfThreadCreateRequest(String text,Long postId, Long parentId){
        return ThreadOfThreadCreateRequest.builder()
                .parentThreadId(parentId)
                .postId(postId)
                .text(text)
                .build();
    }



    private PostCreateRequest postCreateRequest(Long communityId, List<PostCreateRequest.AddressAndPriority> list,String text,String title){
        PostCreateRequest request =
                PostCreateRequest.builder()
                        .communityId(communityId)
                        .text(text)
                        .title(title)
                        .addressAndPriorities(list)
                        .build();

        return  request;
    }

    public UserResponse createUser(){
        return UserResponse.builder()
                .contact("01025045779")
                .email("1dilumn0@gmail.com")
                .id(1L)
                .nickName("우석우")
                .profile("naver.com")
                .specialities(List.of(Specialities.KEYBOARD_INSTRUMENT))
                .build();
    }

}