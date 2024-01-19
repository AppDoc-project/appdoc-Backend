package webdoc.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.user.response.TuteeProfileResponse;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.entity.user.response.TutorProfileResponse;
import webdoc.community.repository.*;
import webdoc.community.utility.LocalDateTimeToString;

import java.util.List;
import java.util.stream.Collectors;

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

        return new TuteeProfileResponse(bookmarkCount,postCount,threadCount,
                user.getName(),user.getProfile());
    }

    // 본인이 작성한 글 fetch
    public List<PostResponse> ownPost(Long userId,String jwt){
        List<Post> posts = postRepository.getPostByUserIdOrderByIdDesc(userId);

        return mapToPostResponse(posts,jwt);
    }

    //  본인이 작성한 댓글이 달린 글
    public List<PostResponse> ownThread(Long userId, String jwt){

        // 내가 작성한 댓글이 담긴 게시글
        List<Post> posts = postRepository.getPostsWithMyThread(userId);

        return mapToPostResponse(posts,jwt);
    }

    // 본인이 북마크한 게시글을 가져온다
    public List<PostResponse> ownBookmark(Long userId, String jwt){

        // 내가 북마크한 게시글
        List<Post> posts = postRepository.getBookmarkedPosts(userId);

        return mapToPostResponse(posts,jwt);
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

    private List<PostResponse> mapToPostResponse(List<Post> list,String jwt){
        return
                list.stream()
                        .map(s->{
                            UserResponse user = userService.fetchUserResponseFromAuthServer(
                                    authServer+"/server/user/id/"+s.getUserId(),jwt,10_000,10_1000
                            ).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));

                            List<Integer> bltp = getBLTP(s.getId());
                            return new PostResponse(
                                    s.getId(),s.getUserId(),s.getTitle(),
                                    user.getNickName(),user.getProfile(),s.getText(),bltp.get(0),bltp.get(1),bltp.get(2),bltp.get(3),
                                    LocalDateTimeToString.convertToLocalDateTimeString(s.getCreatedAt()),user.getIsTutor(),s.getView(),s.getCommunity().getName(),s.getCommunity().getId()
                            );
                        }).collect(Collectors.toList());
    }






}
