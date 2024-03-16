package webdoc.community.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.banned.Banned;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.community.CommunityResponse;
import webdoc.community.domain.entity.like.Bookmark;
import webdoc.community.domain.entity.like.Like;
import webdoc.community.domain.entity.post.Picture;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.Thread;
import webdoc.community.domain.entity.post.enums.PostSearchType;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.post.request.PostModifyRequest;
import webdoc.community.domain.entity.post.request.ThreadCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadOfThreadCreateRequest;
import webdoc.community.domain.entity.post.response.PostDetailResponse;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.post.response.ThreadResponse;
import webdoc.community.domain.entity.report.PostReport;
import webdoc.community.domain.entity.report.ThreadReport;
import webdoc.community.domain.entity.report.request.ReportCreateRequest;
import webdoc.community.domain.entity.user.Specialities;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.exceptions.ReportAlreadyExistsException;
import webdoc.community.domain.exceptions.UserBannedException;
import webdoc.community.repository.*;
import webdoc.community.utility.LocalDateTimeToString;

import java.time.LocalDateTime;
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
    BookmarkRepository bookmarkRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    BannedRepository bannedRepository;

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

         List<String> list = List.of("www","wwww");


         PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕하세요","ㅋㅋㅋ");

         when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                 .thenReturn(
                         Optional.of(createUser())
                 );




         //when
         Post post = communityService.createPost(request,1L);

         List<Picture> pictures = post.getPictures();


         //then
         assertThat(post).isNotNull()
                         .extracting("community","text","userId")
                                 .contains(com1,"안녕하세요",1L);

         // 사진이 올바르게 저장됨
         assertThat(pictures).extracting("address")
                 .containsExactlyInAnyOrder(
                         "www","wwww"
                 );

      }

      @DisplayName("존재하지 않는 게시판에 게시글을 등록하면 실패한다")
      @Test
      void createPostWithInvalidCommunity(){


          List<String> list = List.of("www","wwww");
          PostCreateRequest request = postCreateRequest(1L,list,"안녕하세요","반가워요");
          when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                  .thenReturn(
                          Optional.of(createUser())
                  );


          //when + then

          assertThatThrownBy(()->communityService.createPost(request,1L))
                  .isInstanceOf(NoSuchElementException.class);
       }



       @DisplayName("게시판에서 특정 개수만큼 게시물을 가져온다")
       @Test
       void fetchPostsWithLimit(){
           //given
           Community com1 = Community
                   .builder()
                   .name("외과")
                   .build();


           when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                   .thenReturn(
                           Optional.of(createUser())
                   );
           UserResponse user = createUser();
           communityRepository.save(com1);

           List<String> list = List.of("www","wwww");


           PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
           PostCreateRequest request1 = postCreateRequest(com1.getId(),null,"두번째","ㅋㅋㅋㅋ");
           PostCreateRequest request2 = postCreateRequest(com1.getId(),null,"세번째","ㅋㅋㅋㅋ");
           PostCreateRequest request3 = postCreateRequest(com1.getId(),null,"네번째","ㅋㅋㅋㅋ");
           PostCreateRequest request4 = postCreateRequest(com1.getId(),null,"다섯번째","ㅋㅋㅋㅋ");
           PostCreateRequest request5 = postCreateRequest(com1.getId(),null,"여섯번째","ㅋㅋㅋㅋ");
           PostCreateRequest request6 = postCreateRequest(com1.getId(),list,"일곱번째","ㅋㅋㅋㅋ");


           //when
           Post post = communityService.createPost(request,user.getId());
           Post post2 = communityService.createPost(request1,user.getId());
           Post post3 = communityService.createPost(request2,user.getId());
           Post post4 = communityService.createPost(request3,user.getId());
           Post post5 = communityService.createPost(request4,user.getId());
           Post post6 = communityService.createPost(request5,user.getId());
           Post post7 = communityService.createPost(request6,user.getId());

           //then
           List<PostResponse> list1 = communityService.getPostsWithLimit(com1.getId(),5);
           List<PostResponse> list2 = communityService.getPostsWithLimitAndIdAfter(com1.getId(),post7.getId(),5);
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

            when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                    .thenReturn(
                            Optional.of(createUser())
                    );

            UserResponse user = createUser();
            communityRepository.save(com1);

            List<String> list = List.of("www","wwww");
            PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕하세요","ㅋㅋㅋ");
            Post post = communityService.createPost(request,user.getId());



            //when
            PostDetailResponse response = communityService.getCertainPost(post.getId(),user.getId());


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
                            post.getPictures().size(), LocalDateTimeToString.convertToLocalDateTimeString(post.getCreatedAt()),user.getIsTutor(),
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
             when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                     .thenReturn(
                             Optional.of(createUser())
                     );

             UserResponse user = createUser();
             communityRepository.save(com1);
             PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
             Post post = communityService.createPost(request,user.getId());
             ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());

             //when
             Thread thread = communityService.createThread(user.getId(),threadCreateRequest);

             //then
             assertThat(thread).extracting("text","parent","userId","post")
                     .containsExactly(threadCreateRequest.getText(),null,
                     user.getId(),post);
             assertThat(thread).isNotNull();


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
               when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                       .thenReturn(
                               Optional.of(createUser())
                       );

               UserResponse user = createUser();
               communityRepository.save(com1);
               PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
               Post post = communityService.createPost(request,user.getId());
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
                when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                        .thenReturn(
                                Optional.of(createUser())
                        );


                communityRepository.save(com1);
                PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
                Post post = communityService.createPost(request,user.getId());
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
                 when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                         .thenReturn(
                                 Optional.of(createUser())
                         );


                 communityRepository.save(com1);
                 PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
                 Post post = communityService.createPost(request,user.getId());


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
                 List<ThreadResponse> threadResponses = communityService.getThreadByPostId(post.getId());
                 //then
                 assertThat(threadResponses).hasSize(3);
              }

    @DisplayName("전체 게시판 검색을 사용해서 특정 개수만큼 게시물을 가져온다")
    @Test
    void searchPostsWithLimit(){
        //given
        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        Community com2 = Community
                .builder()
                .name("관악기")
                .build();


        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        UserResponse user = createUser();
        communityRepository.save(com1);
        communityRepository.save(com2);

        List<String> list = List.of("www","wwww");


        PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
        PostCreateRequest request1 = postCreateRequest(com1.getId(),null,"안녕","ㅋㅋㅋㅋ");
        PostCreateRequest request2 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕ㅋㅋ","모지");
        PostCreateRequest request3 = postCreateRequest(com1.getId(),null,"네번째","잉?");
        PostCreateRequest request4 = postCreateRequest(com2.getId(),null,"다섯번째","ㅋㅋㅋㅋ");
        PostCreateRequest request5 = postCreateRequest(com2.getId(),null,"여섯번째","ㅋㅋㅋㅋ");
        PostCreateRequest request6 = postCreateRequest(com2.getId(),list,"ㅋㅋ안녕","ㅋㅋㅋㅋ");


        //when
        Post post = communityService.createPost(request,user.getId());
        Post post2 = communityService.createPost(request1,user.getId());
        Post post3 = communityService.createPost(request2,user.getId());
        Post post4 = communityService.createPost(request3,user.getId());
        Post post5 = communityService.createPost(request4,user.getId());
        Post post6 = communityService.createPost(request5,user.getId());
        Post post7 = communityService.createPost(request6,user.getId());

        //then
        List<PostResponse> list1 = communityService.entireSearchPost(5,"안녕", PostSearchType.CONTENT);
        List<PostResponse> list2 = communityService.entireSearchPostAfter(5,"ㅋ",post7.getId(),PostSearchType.TITLEANDCONTENT);
        assertThat(list1).hasSize(4);
        assertThat(list2).hasSize(5);
        list1.forEach(System.out::println);
    }

    @DisplayName("특정 게시판 검색을 사용해서 특정 개수만큼 게시물을 가져온다")
    @Test
    void searchCommunityPostsWithLimit(){
        //given
        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        Community com2 = Community
                .builder()
                .name("관악기")
                .build();


        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        UserResponse user = createUser();
        communityRepository.save(com1);
        communityRepository.save(com2);

        List<String> list = List.of("www","wwww");

        PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
        PostCreateRequest request1 = postCreateRequest(com1.getId(),null,"안녕","ㅋㅋㅋㅋ");
        PostCreateRequest request2 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕ㅋㅋ","모지");
        PostCreateRequest request3 = postCreateRequest(com1.getId(),null,"네번째","잉?");
        PostCreateRequest request4 = postCreateRequest(com2.getId(),null,"다섯번째","ㅋㅋㅋㅋ");
        PostCreateRequest request5 = postCreateRequest(com2.getId(),null,"여섯번째","ㅋㅋㅋㅋ");
        PostCreateRequest request6 = postCreateRequest(com2.getId(),list,"ㅋㅋ안녕","ㅋㅋㅋㅋ");


        //when
        Post post = communityService.createPost(request,user.getId());
        Post post2 = communityService.createPost(request1,user.getId());
        Post post3 = communityService.createPost(request2,user.getId());
        Post post4 = communityService.createPost(request3,user.getId());
        Post post5 = communityService.createPost(request4,user.getId());
        Post post6 = communityService.createPost(request5,user.getId());
        Post post7 = communityService.createPost(request6,user.getId());

        //then
        List<PostResponse> list1 = communityService.communitySearchPost(5,"안녕", com1.getId(), PostSearchType.CONTENT);
        List<PostResponse> list2 = communityService.communitySearchPostAfter(5,post7.getId(),"ㅋ",com2.getId(),PostSearchType.TITLE);
        assertThat(list1).hasSize(3);
        assertThat(list2).hasSize(2);
        list1.forEach(System.out::println);
    }

    @DisplayName("좋아요를 등록한다")
    @Test
    void enrollLike(){
        //given

        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());



        //when
        boolean success = communityService.enrollLike(user.getId(),post1.getId());


        //then
        assertThat(success).isTrue();
        Like like = likeRepository.findLikeByUserIdAndPostId(user.getId(), post1.getId()).orElse(null);
        assertThat(like).isNotNull();
     }

    @DisplayName("좋아요를 중복 등록하면 실패한다")
    @Test
    void enrollLikeDuplicate(){
        //given

        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());

        //when
        communityService.enrollLike(user.getId(),post1.getId());
        boolean success = communityService.enrollLike(user.getId(),post1.getId());


        //then
        assertThat(success).isFalse();
        Like like = likeRepository.findLikeByUserIdAndPostId(user.getId(), post1.getId()).orElse(null);
        assertThat(like).isNotNull();
    }

    @DisplayName("존재하지 않는 게시글을 좋아요 할 수 없다")
    @Test
    void failWhenEnrollLikeWithInvalidPost(){
        //given

        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        //when + then
        assertThatThrownBy(()->communityService.enrollLike(user.getId(),5L))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("북마크를 등록한다")
    @Test
    void toggleBookmark(){
        //given

        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());



        //when
        communityService.toggleBookmark(user.getId(),post1.getId());


        //then
        Bookmark bookmark = bookmarkRepository.findBookmarkByPostIdAndUserId(post1.getId(), user.getId()).orElse(null);
        assertThat(bookmark).isNotNull();
    }

    @DisplayName("북마크를 토클해서 해제한다")
    @Test
    void toggleBookmarkTwice(){
        //given

        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());



        //when
        communityService.toggleBookmark(user.getId(),post1.getId());
        communityService.toggleBookmark(user.getId(),post1.getId());


        //then
        Bookmark bookmark = bookmarkRepository.findBookmarkByPostIdAndUserId(post1.getId(), user.getId()).orElse(null);
        assertThat(bookmark).isNull();
    }

    @DisplayName("게시글을 삭제한다")
    @Test
    void deletePost(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());


        //when
        communityService.deletePost(user.getId(), post1.getId());


        //then
        Post post = postRepository.findById(post1.getId()).orElse(null);
        assertThat(post).isNull();
     }

    @DisplayName("본인의 게시글이 아닌 글은 삭제할 수 없다")
    @Test
    void cannotDeleteIfNotMyPost(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());


        //when + then

        assertThatThrownBy(()->communityService.deletePost(100L, post1.getId()))
                .isInstanceOf(IllegalStateException.class);
        Post post = postRepository.findById(post1.getId()).orElse(null);
        assertThat(post).isNotNull();

    }

    @DisplayName("댓글을 삭제한다")
    @Test
    void deleteThread(){
        //given
        Community com1 = Community
                .builder()
                .name("외과")
                .build();
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        UserResponse user = createUser();
        communityRepository.save(com1);
        PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
        Post post = communityService.createPost(request,user.getId());
        ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());

        Thread thread = communityService.createThread(user.getId(),threadCreateRequest);

        // when
        communityService.deleteThread(user.getId(),thread.getId());

        // then
        Thread thread1 = threadRepository.findById(thread.getId()).orElse(null);
        assertThat(thread1).isNull();
    }

    @DisplayName("댓글을 신고한다")
    @Test
    void reportThread(){
        //given
        Community com1 = Community
                .builder()
                .name("외과")
                .build();
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        UserResponse user = createUser();
        communityRepository.save(com1);
        PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
        Post post = communityService.createPost(request,user.getId());
        ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());
        Thread thread = communityService.createThread(user.getId(),threadCreateRequest);


        //when
        ReportCreateRequest reportCreateRequest = new ReportCreateRequest();
        reportCreateRequest.setId(thread.getId());
        reportCreateRequest.setReason("부적절한 글");
        communityService.reportThread(user.getId(),reportCreateRequest);


        //then
        ThreadReport foundThreadReport = reportRepository.findThreadReportByUserIdAndThreadId(user.getId(), thread.getId())
                .orElse(null);
        assertThat(foundThreadReport).isNotNull();
        assertThat(foundThreadReport).extracting("reason","userId")
                .containsExactly("부적절한 글",user.getId());
     }

    @DisplayName("댓글을 중복해서 신고할 수 없다")
    @Test
    void reportDuplicatedThread(){
        //given
        Community com1 = Community
                .builder()
                .name("외과")
                .build();
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        UserResponse user = createUser();
        communityRepository.save(com1);
        PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
        Post post = communityService.createPost(request,user.getId());
        ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());
        Thread thread = communityService.createThread(user.getId(),threadCreateRequest);




        //when + then
        ReportCreateRequest reportCreateRequest = new ReportCreateRequest();
        reportCreateRequest.setId(thread.getId());
        reportCreateRequest.setReason("부적절한 글");
        communityService.reportThread(user.getId(),reportCreateRequest);

        assertThatThrownBy(()->communityService.reportThread(user.getId(),reportCreateRequest))
                .isInstanceOf(ReportAlreadyExistsException.class);



    }

    @DisplayName("게시글을 신고한다")
    @Test
    void reportPost(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());


        //when
        ReportCreateRequest reportCreateRequest = new ReportCreateRequest();
        reportCreateRequest.setId(post1.getId());
        reportCreateRequest.setReason("매우 부적절");
        communityService.reportPost(user.getId(),reportCreateRequest);



        //then
        PostReport postReport = reportRepository.findPostReportByUserIdAndPostId(user.getId(), post1.getId())
                .orElse(null);
        assertThat(postReport).isNotNull();
        assertThat(postReport).extracting("userId","reason")
                .containsExactly(user.getId(),"매우 부적절");

     }

    @DisplayName("게시글을 중복 신고할 수 없다")
    @Test
    void reportDuplicatedPost(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());


        //when + then
        ReportCreateRequest reportCreateRequest = new ReportCreateRequest();
        reportCreateRequest.setId(post1.getId());
        reportCreateRequest.setReason("매우 부적절");
        communityService.reportPost(user.getId(),reportCreateRequest);
        assertThatThrownBy(()->communityService.reportPost(user.getId(),reportCreateRequest))
                .isInstanceOf(ReportAlreadyExistsException.class);


    }

    @DisplayName("게시글을 수정한다")
    @Test
    void modifyPost(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());


        //when
        PostModifyRequest request7 = postModifyRequest(post1.getId(),null,"뭐해","난 바쁨");
        communityService.modifyPost(request7,user.getId());


        //then
        Post post = postRepository.findById(post1.getId()).orElse(null);
        assertThat(post).isNotNull();
        assertThat(post).extracting(
                "userId","id","title","text"
        )
                .containsExactly(user.getId(),post1.getId(),"난 바쁨","뭐해");
    }

    @DisplayName("자신의 게시글이 아니면 수정할 수 없다")
    @Test
    void noModifyWhenNotOwnPost(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());


        //when + then
        PostModifyRequest request7 = postModifyRequest(post1.getId(),null,"뭐해","난 바쁨");
        assertThatThrownBy(()-> communityService.modifyPost(request7,100L))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("게시글의 사진을 수정한다")
    @Test
    void modifyPostsPictures(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        List<String> list = List.of("www","wwww");

        PostCreateRequest request6 = postCreateRequest(com1.getId(),list,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());




        //when

        PostModifyRequest request7 = postModifyRequest(post1.getId(),list = List.of("444","w22"),"뭐해","난 바쁨");
        communityService.modifyPost(request7,user.getId());


        //then
        Post post = postRepository.findById(post1.getId()).orElse(null);
        assertThat(post).isNotNull();
        assertThat(post).extracting(
                        "userId","id","title","text"
                )
                .containsExactly(user.getId(),post1.getId(),"난 바쁨","뭐해");

        // 사진이 올바르게 저장됨
        assertThat(post.getPictures()).extracting("address")
                .containsExactlyInAnyOrder(
                        "444"
                        ,"w22"
                );
    }

    @DisplayName("없는 게시글은 수정할 수 없다")
    @Test
    void noModifyWhenPostNotExists(){
        //given

        //when + then
        PostModifyRequest request7 = postModifyRequest(3L,null,"뭐해","난 바쁨");
        assertThatThrownBy(()-> communityService.modifyPost(request7,100L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("정지된 계정은 글 작성이 불가능 하다")
    @Test
    void bannedPostCreate(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        Banned banned = Banned.createBanned(user.getId(), LocalDateTime.now().plusDays(1L),"부적절한 글 작성");
        bannedRepository.save(banned);

        // when
        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");

        //then
        assertThatThrownBy(()->communityService.createPost(request6,user.getId()))
                .isInstanceOf(UserBannedException.class);
    }

    @DisplayName("정지된 계정은 게시글을 수정할 수 없다")
    @Test
    void bannedModifyPost(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        Community com1 = Community
                .builder()
                .name("현악기")
                .build();

        communityRepository.save(com1);
        UserResponse user = createUser();

        PostCreateRequest request6 = postCreateRequest(com1.getId(),null,"ㅋㅋ안녕","ㅋㅋㅋㅋ");
        Post post1 = communityService.createPost(request6,user.getId());

        Banned banned = Banned.createBanned(user.getId(), LocalDateTime.now().plusDays(1L),"부적절한 글 작성");
        bannedRepository.save(banned);

        //when
        PostModifyRequest request7 = postModifyRequest(post1.getId(),null,"뭐해","난 바쁨");



        //then
        assertThatThrownBy(()->communityService.modifyPost(request7,user.getId()))
                .isInstanceOf(UserBannedException.class);
        Post post = postRepository.findById(post1.getId()).orElse(null);
        assertThat(post).isNotNull();
        assertThat(post).extracting(
                        "userId","id","title","text"
                )
                .containsExactly(user.getId(),post1.getId(),"ㅋㅋㅋㅋ","ㅋㅋ안녕");
    }

    @DisplayName("정지된 계정은 댓글을 생성할 수 없다")
    @Test
    void bannedCreateThread(){
        //given
        Community com1 = Community
                .builder()
                .name("외과")
                .build();
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        UserResponse user = createUser();
        communityRepository.save(com1);
        PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
        Post post = communityService.createPost(request,user.getId());

        Banned banned = Banned.createBanned(user.getId(), LocalDateTime.now().plusDays(1L),"부적절한 글 작성");
        bannedRepository.save(banned);


        // when
        ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());

        // then
        assertThatThrownBy(()->communityService.createThread(user.getId(),threadCreateRequest))
                .isInstanceOf(UserBannedException.class);
    }

    @DisplayName("정지된 계정은 대댓글을 생성할 수 없다")
    @Test
    void bannedCreateChildThread(){
        //given
        Community com1 = Community
                .builder()
                .name("외과")
                .build();
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser())
                );

        UserResponse user = createUser();
        communityRepository.save(com1);
        PostCreateRequest request = postCreateRequest(com1.getId(),null,"안녕하세요","ㅋㅋㅋ");
        Post post = communityService.createPost(request,user.getId());

        // when


        ThreadCreateRequest threadCreateRequest = threadCreateRequest("안녕",post.getId());
        Thread thread = communityService.createThread(user.getId(),threadCreateRequest);

        Banned banned = Banned.createBanned(user.getId(), LocalDateTime.now().plusDays(1L),"부적절한 글 작성");
        bannedRepository.save(banned);
        ThreadOfThreadCreateRequest threadOfThreadCreateRequest =  threadOfThreadCreateRequest("ㅋㅋ", post.getId(),thread.getId());

        // then
        assertThatThrownBy(()->communityService.createThreadOfThread(threadOfThreadCreateRequest,user.getId()))
                .isInstanceOf(UserBannedException.class);
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



    private PostCreateRequest postCreateRequest(Long communityId, List<String> list,String text,String title){
        PostCreateRequest request =
                PostCreateRequest.builder()
                        .communityId(communityId)
                        .text(text)
                        .title(title)
                        .addresses(list)
                        .build();

        return  request;
    }

    private PostModifyRequest postModifyRequest(Long postId,List<String> list, String text, String title){
        PostModifyRequest request =
                PostModifyRequest.builder()
                        .text(text)
                        .title(title)
                        .postId(postId)
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
                .profile("naver.com")
                .specialities(List.of(Specialities.KEYBOARD_INSTRUMENT))
                .build();
    }

}