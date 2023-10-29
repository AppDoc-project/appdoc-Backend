package webdoc.community.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.community.CommunityResponse;
import webdoc.community.domain.entity.post.Picture;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.post.response.PostDetailResponse;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.user.User;
import webdoc.community.domain.entity.user.patient.Patient;
import webdoc.community.repository.CommunityRepository;
import webdoc.community.repository.PostRepository;
import webdoc.community.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommunityServiceTest {
    @Autowired
    CommunityService communityService;
    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    UserRepository userRepository;


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

         User user = patientCreate();

         userRepository.save(user);
         communityRepository.save(com1);

         List<PostCreateRequest.AddressAndPriority> list = new ArrayList<>();
         list.add(new PostCreateRequest.AddressAndPriority("www",1));
         list.add(new PostCreateRequest.AddressAndPriority("wwwt",2));

         PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕하세요","ㅋㅋㅋ");




         //when
         Post post = communityService.createPost(request,user.getId());

         List<Picture> pictures = post.getPictures();


         //then
         assertThat(post).isNotNull()
                         .extracting("community","text","user")
                                 .contains(com1,"안녕하세요",user);

         // 사진이 올바르게 저장됨
         assertThat(pictures).extracting("address","priority")
                 .containsExactlyInAnyOrder(
                         tuple("www",1),
                         tuple("wwwt",2)
                 );

      }

      @DisplayName("존재하지 않는 게시판에 게시글을 등록하면 실패한다")
      @Test
      void test(){
          User user = patientCreate();

          userRepository.save(user);

          List<PostCreateRequest.AddressAndPriority> list = new ArrayList<>();
          list.add(new PostCreateRequest.AddressAndPriority(null,1));
          list.add(new PostCreateRequest.AddressAndPriority("wwwt",2));

          PostCreateRequest request = postCreateRequest(1L,list,"안녕하세요","반가워요");


          //when + then
          assertThatThrownBy(()->communityService.createPost(request,user.getId()))
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

          User user = patientCreate();

          userRepository.save(user);
          communityRepository.save(com1);

          List<PostCreateRequest.AddressAndPriority> list = new ArrayList<>();
          list.add(new PostCreateRequest.AddressAndPriority(null,1));
          list.add(new PostCreateRequest.AddressAndPriority("wwwt",2));

          PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕하세요","반가워요");


          //when + then
          assertThatThrownBy(()->communityService.createPost(request,user.getId()))
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

           User user = patientCreate();

           userRepository.save(user);
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

            User user = patientCreate();

            userRepository.save(user);
            communityRepository.save(com1);

            List<PostCreateRequest.AddressAndPriority> pictures = new ArrayList<>();
            pictures.add(new PostCreateRequest.AddressAndPriority("Wwww",1));
            pictures.add(new PostCreateRequest.AddressAndPriority("wwwt",2));

            PostCreateRequest request = postCreateRequest(com1.getId(),pictures,"안녕하세요","ㅋㅋㅋ");
            Post post = communityService.createPost(request,user.getId());



            //when
            PostDetailResponse response = communityService.getCertainPost(post.getId(),user.getId());


            //then
            System.out.println(response);
            assertThat(response).isNotNull();
         }



    private Patient patientCreate(){
        return Patient.createPatient(
                "1dilumn0@gmail.com","dntjrdn78","우석우","01025045779",LocalDate.now()
        );
    }


    private PostCreateRequest postCreateRequest(Long communityId, List<PostCreateRequest.AddressAndPriority> list,String text,String title){
        PostCreateRequest request = new PostCreateRequest();
        request.setCommunityId(communityId);
        request.setText(text);
        request.setTitle(title);
        request.setAddresses(list);
        return  request;
    }

}