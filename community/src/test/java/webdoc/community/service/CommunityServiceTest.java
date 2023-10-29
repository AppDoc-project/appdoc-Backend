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
import webdoc.community.domain.entity.user.User;
import webdoc.community.domain.entity.user.patient.Patient;
import webdoc.community.repository.CommunityRepository;
import webdoc.community.repository.PostRepository;
import webdoc.community.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
         list.add(new PostCreateRequest.AddressAndPriority("www",1L));
         list.add(new PostCreateRequest.AddressAndPriority("wwwt",2L));

         PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕하세요");




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
                         tuple("www",1L),
                         tuple("wwwt",2L)
                 );

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
          list.add(new PostCreateRequest.AddressAndPriority(null,1L));
          list.add(new PostCreateRequest.AddressAndPriority("wwwt",2L));

          PostCreateRequest request = postCreateRequest(com1.getId(),list,"안녕하세요");


          //when + then
          assertThatThrownBy(()->communityService.createPost(request,user.getId()))
                  .isInstanceOf(IllegalArgumentException.class);
       }

    private Patient patientCreate(){
        return Patient.createPatient(
                "1dilumn0@gmail.com","dntjrdn78","우석우","01025045779",LocalDate.now()
        );
    }

    private PostCreateRequest postCreateRequest(Long communityId, List<PostCreateRequest.AddressAndPriority> list,String text){
        PostCreateRequest request = new PostCreateRequest();
        request.setCommunityId(communityId);
        request.setText(text);
        request.setAddresses(list);
        return  request;
    }

}