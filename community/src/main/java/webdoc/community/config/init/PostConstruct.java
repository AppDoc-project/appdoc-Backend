package webdoc.community.config.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadOfThreadCreateRequest;
import webdoc.community.repository.CommunityRepository;
import webdoc.community.service.CommunityService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;




@RequiredArgsConstructor
public class PostConstruct {
    private final CommunityRepository communityRepository;
    private final CommunityService communityService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void devCommunityInit(){
        Community piano = Community.createCommunity("피아노");
        Community guitar = Community.createCommunity("기타");
        Community vocal = Community.createCommunity("보컬");
        Community drum = Community.createCommunity("드럼");
        Community bass = Community.createCommunity("베이스");
        Community composition = Community.createCommunity("작곡");
        Community windInst = Community.createCommunity("관악기");
        Community stringInst = Community.createCommunity("현악기");
        Community keyboardInst = Community.createCommunity("건반악기");
        Community freeBoard = Community.createCommunity("자유게시판");

        communityRepository.saveAll(List.of(
                piano,guitar,vocal,drum,bass,composition,windInst,stringInst,keyboardInst,freeBoard
        ));

    }
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void devPostInit() {
        Random random = new Random();

        for (int communityId = 1; communityId <= 10; communityId++) {
            for (int i = 0; i < 52; i++) {
                List<PostCreateRequest.AddressAndPriority> pictures = new ArrayList<>();
                pictures.add(new PostCreateRequest.AddressAndPriority("Wwww", 1));
                pictures.add(new PostCreateRequest.AddressAndPriority("wwwt", 2));

                String text = generateRandomString(10);
                String title = generateRandomString(10);

                Long userId = (long) (random.nextInt(2) + 1); // 랜덤하게 1 또는 2 선택

                PostCreateRequest request = postCreateRequest((long) communityId, pictures, text, title, userId);

                communityService.createPost(request, userId);
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void devThreadInit() {
        Random random = new Random();
        for (int i = 0 ; i < 52; i++){
            for (int j = 0; j<20; j++){
                String commentText = generateRandomString(11);
                ThreadCreateRequest threadCreateRequest = threadCreateRequest(commentText, (long) (i+1));
                Long userId = (long) (random.nextInt(2) + 1); // 랜덤하게 1 또는 2 선택
                communityService.createThread(userId,threadCreateRequest);
            }
        }
    }


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void devThreadOfThreadInit() {
        Random random = new Random();
        for (int i = 0 ; i < 52; i++){
            for (int j = 0; j<20; j++){

                ThreadOfThreadCreateRequest threadOfThreadCreateRequest = threadOfThreadCreateRequest("안녕", (long) (i+1), (long) ((i)*20 + (j+1)));
                Long userId = (long) (random.nextInt(2) + 1); // 랜덤하게 1 또는 2 선택
                communityService.createThreadOfThread(threadOfThreadCreateRequest,userId);

            }
        }
    }





    private String generateRandomString(int length) {
        Random random = new Random();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder randomString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }

        return randomString.toString();
    }


    private ThreadCreateRequest threadCreateRequest(String text, Long postId){
        return ThreadCreateRequest.builder()
                .text(text)
                .postId(postId)
                .build();

    }
    private PostCreateRequest postCreateRequest(Long communityId, List<PostCreateRequest.AddressAndPriority> list, String text, String title, Long userId){
        PostCreateRequest request =
                PostCreateRequest.builder()
                        .communityId(communityId)
                        .text(text)
                        .title(title)
                        .addressAndPriorities(list)
                        .build();

        return  request;
    }

    private ThreadOfThreadCreateRequest threadOfThreadCreateRequest(String text, Long postId, Long parentId){
        return ThreadOfThreadCreateRequest.builder()
                .parentThreadId(parentId)
                .postId(postId)
                .text(text)
                .build();
    }

}
