package webdoc.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.pick.Pick;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.tutor.response.TutorResponse;
import webdoc.community.domain.entity.user.response.TuteeProfileResponse;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.entity.user.response.TutorProfileResponse;
import webdoc.community.repository.*;
import webdoc.community.utility.LocalDateTimeToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 프로필 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final BookmarkRepository bookmarkRepository;

    private final PostRepository postRepository;

    private final ThreadRepository threadRepository;

    private final UserService userService;

    private final LikeRepository likeRepository;

    private final PictureRepository pictureRepository;

    private final PickRepository pickRepository;

    private final StatisticsService statisticsService;

    private final ReviewRepository reviewRepository;

    @Value("${authentication.server}")
    private String authServer;

    // 튜터 프로필 정보
    public TutorProfileResponse tutorProfileInfo(UserResponse user){

        int bookmarkCount = bookmarkRepository.countBookmarkByUserId(user.getId());
        int postCount = postRepository.countPostsByUserId(user.getId());
        int threadCount = threadRepository.countThreadByUserId(user.getId());

        return new TutorProfileResponse(bookmarkCount,postCount,threadCount,
                user.getName(),user.getSpecialities(),user.getProfile());
    }

    // 튜티 프로필 정보
    public TuteeProfileResponse tuteeProfileInfo(UserResponse user){
        int bookmarkCount = bookmarkRepository.countBookmarkByUserId(user.getId());
        int postCount = postRepository.countPostsByUserId(user.getId());
        int threadCount = threadRepository.countThreadByUserId(user.getId());
        int pickCount = pickRepository.countByTuteeId(user.getId());
        return new TuteeProfileResponse(bookmarkCount,postCount,threadCount,pickCount,
                user.getName(),user.getProfile());
    }

    // 본인이 작성한 글 fetch
    public List<PostResponse> ownPost(Long userId){
        List<Post> posts = postRepository.getPostByUserIdOrderByIdDesc(userId);

        return mapToPostResponse(posts);
    }

    //  본인이 작성한 댓글이 달린 글
    public List<PostResponse> ownThread(Long userId){

        // 내가 작성한 댓글이 담긴 게시글
        List<Post> posts = postRepository.getPostsWithMyThread(userId);

        return mapToPostResponse(posts);
    }

    // 본인이 북마크한 게시글을 가져온다
    public List<PostResponse> ownBookmark(Long userId ){

        // 내가 북마크한 게시글
        List<Post> posts = postRepository.getBookmarkedPosts(userId);

        return mapToPostResponse(posts);
    }
    // 본인이 찜한 강사를 가져온다
    public List<TutorResponse> ownTutor(Long userId){
        List<Pick> picks = pickRepository.findByTuteeId(userId);

        List<UserResponse> userResponses =
            picks.stream()
                    .map(e->{
                UserResponse user = userService.fetchUserResponseFromAuthServer(
                        authServer+"/server/user/id/"+e.getTutorId(),10_000,10_1000
                ).filter(s->!s.getNickName().equals("탈퇴회원")).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));
                return user;
            }).toList();

        return mapToTutor(userResponses,userId);


    }


    // 게시글의 북마크, 좋아요, 댓글, 사진 수를 가져오기
    private List<Integer> getBLTP(Long postId){
        int bookmark = bookmarkRepository.countBookmarkByPostId(postId);
        int like = likeRepository.countLikeByPostId(postId);
        int thread = threadRepository.countThreadByPostId(postId);
        int picture = pictureRepository.countPictureByPostId(postId);


        return List.of(bookmark,like,thread,picture);

    }


    // post -> postResponse 변환 함수

    private List<PostResponse> mapToPostResponse(List<Post> list){
        return
                list.stream()
                        .map(s->{
                            UserResponse user = userService.fetchUserResponseFromAuthServer(
                                    authServer+"/server/user/id/"+s.getUserId(),10_000,10_1000
                            ).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));

                            List<Integer> bltp = getBLTP(s.getId());
                            return new PostResponse(
                                    s.getId(),s.getUserId(),s.getTitle(),
                                    user.getNickName(),user.getProfile(),s.getText(),bltp.get(0),bltp.get(1),bltp.get(2),bltp.get(3),
                                    LocalDateTimeToString.convertToLocalDateTimeString(s.getCreatedAt()),user.getIsTutor(),s.getView(),s.getCommunity().getName(),s.getCommunity().getId()
                            );
                        }).collect(Collectors.toList());
    }

    // 도메인 객체를 응답 객체로 변환
    private List<TutorResponse> mapToTutor(List<UserResponse> users,Long userId){

        List<TutorResponse> tutorResponses = users.stream()
                .filter(UserResponse::getIsTutor)
                .map(e-> new TutorResponse(e.getNickName(),e.getId(),e.getSpecialities(),e.getProfile(), reviewRepository.countReviewByTutorId(e.getId())))
                .toList();
        tutorResponses.forEach(e->{
            // 내가 찜했는 지 아닌 지 여부
            Pick pick = pickRepository.findByTutorIdAndTuteeId(e.getId(),userId);



            Integer lessons = statisticsService.getLessonCount(e.getId());
            if(lessons == null){
                lessons = 0;
            }

            Double score = statisticsService.getReviewScore(e.getId());
            if(score == null){
                score = 0.0;
            }

            e.setScore(score);
            e.setLessonCount(lessons);
            e.setPickYn(pick != null);


        });

        return tutorResponses;


    }






}
