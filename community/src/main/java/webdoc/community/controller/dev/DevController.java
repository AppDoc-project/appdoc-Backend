package webdoc.community.controller.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.pick.Pick;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.review.Review;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.repository.CommunityRepository;
import webdoc.community.repository.PickRepository;
import webdoc.community.repository.PostRepository;
import webdoc.community.repository.ReviewRepository;
import webdoc.community.service.StatisticsService;
import java.util.Random;

/*
* 개발 데이터 삽입용 컨트롤러
*/
@RestController
@RequestMapping("/community/init")
@RequiredArgsConstructor
@Transactional
public class DevController {
    private final PickRepository pickRepository;
    private final ReviewRepository reviewRepository;
    private final StatisticsService statisticsService;

    private final PostRepository postRepository;

    private final CommunityRepository communityRepository;



    @GetMapping("/pick")
    // 특정 강사에게 몇개의 찜을 부여할 것 인지...
    public CodeMessageResponse insertPick(@RequestParam Long tutorId, @RequestParam Integer count){

        for(int i = 90 ; i <= 90 + count ; i++ ){
            Pick pick = Pick.createPick((long) i,tutorId);
            pickRepository.save(pick);
        }

        return new CodeMessageResponse("개발 데이터 삽입성공",200,200);
    }

    @GetMapping("/review")
    public CodeMessageResponse insertReview(@RequestParam Long tutorId, @RequestParam Integer count){

        for(int i = 90 ; i <= 90 + count ; i++ ){
            Random random = new Random();

            int randomNumber = random.nextInt(6);

            Review review = Review.createReview((long) i, tutorId, "너무 친절한 것 같아요",randomNumber);
            reviewRepository.save(review);

        }
        return new CodeMessageResponse("개발 데이터 삽입성공",200,200);
    }

    @GetMapping("/post")
    public CodeMessageResponse insertPost(@RequestParam Long userId,@RequestParam Long communityId){
        Community community = communityRepository.findById(communityId).orElse(null);
        postRepository.save(
                Post.CreatePost(userId,generateRandomString(10),generateRandomString(10),community)
        );
        return new CodeMessageResponse("개발 데이터 삽입성공",200,200);
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();

        char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length);

            sb.append(characters[randomIndex]);
        }

        return sb.toString();
    }






}
