package webdoc.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.community.CommunityResponse;
import webdoc.community.domain.entity.post.Picture;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.Thread;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadOfThreadCreateRequest;
import webdoc.community.domain.entity.post.response.ChildThreadResponse;
import webdoc.community.domain.entity.post.response.PostDetailResponse;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.post.response.ThreadResponse;
import webdoc.community.domain.entity.user.UserResponse;
import webdoc.community.repository.*;

import java.util.*;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {
    @Value("${authentication.server}")
    private String authServer;
    private final CommunityRepository communityRepository;
    private final PostRepository postRepository;
    private  final ThreadRepository threadRepository;
    private final LikeRepository likeRepository;

    private final UserService userService;
    private final PictureRepository pictureRepository;

    private final BookmarkRepository bookmarkRepository;

    public List<CommunityResponse> getAllCommunities(){
        return
                communityRepository.findAll()
                        .stream()
                        .map(s->new CommunityResponse(s.getName(),s.getId()))
                        .collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public Thread createThread(Long userId, ThreadCreateRequest request){
        Long postId = request.getPostId();

        Post post = postRepository.findById(postId).orElseThrow(()->new NoSuchElementException("해당하는 게시글이 없습니다"));

        Thread thread = Thread.createThread(request.getText(),post,userId);

        threadRepository.save(thread);
        return thread;
    }

    // 댓글의 댓글 작성
    @Transactional
    public Thread createThreadOfThread(ThreadOfThreadCreateRequest request,Long userId){
        Long postId = request.getPostId();

        Post post = postRepository.findById(postId).orElseThrow(()->new NoSuchElementException("해당하는 유저가 없습니다"));
        Thread parentThread = threadRepository.findById(request.getParentThreadId()).orElseThrow(()->new NoSuchElementException("해당하는 댓글이 없습니다"));

        // 대댓글에 대댓글을 달려하거나, 부모 댓글의 게시물과 현재 게시물이 다른경우 실패
        if(parentThread.getParent()!=null || parentThread.getPost() != post ){
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        Thread childThread = Thread.createThread(request.getText(),post,userId);
        parentThread.setThisAsParent(childThread);

        return childThread;

    }


    // 게시판의 게시글 불러오기 처음 fetch할 때 사용 : 최신순
    public List<PostResponse> getPostsWithLimit(Long communityId, Integer limit,String jwt){
        communityRepository.findById(communityId)
                .orElseThrow(()->new NoSuchElementException("해당하는 게시판이 없습니다"));

        PageRequest pageRequest = PageRequest.of(0,limit, Sort.by(Sort.Direction.DESC,
                "id"));
        Slice<Post> page = postRepository.getPostByCommunityAndLimit(communityId,pageRequest);

        return mapToPostResponse(page.getContent(),jwt);
    }

    // 게시판의 게시글 불러오기 이후 fetch에서 사용 : 최신순
    @Transactional
    public List<PostResponse> getPostsWithLimitAndIdAfter(Long communityId,Long postId,Integer limit,String jwt){

        communityRepository.findById(communityId)
                .orElseThrow(()->new NoSuchElementException("해당하는 게시판이 없습니다"));

        PageRequest pageRequest = PageRequest.of(0,limit, Sort.by(
                Sort.Order.desc("id")
        ));

        Slice<Post> page = postRepository.getPostByCommunityAndLimitAndId(communityId,postId,pageRequest);

        return mapToPostResponse(page.getContent(),jwt);
    }

    // 특정 글 불러오기
    public PostDetailResponse getCertainPost(Long postId,Long userId,String jwt){
        Post post = postRepository.getCertainPost(postId).orElseThrow(()->new NoSuchElementException("해당하는 게시물이 없습니다"));
        post.viewPlus();
        UserResponse user = userService.fetchUserResponseFromAuthServer(
                authServer+"/server/user/id/"+userId,jwt,10_000,10_1000
        ).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));

        boolean bookmarkYN = bookmarkRepository.findBookmarkByPostIdAndUserId(postId, userId).size() > 0;
        return mapToPostDetail(post,bookmarkYN,user);
    }


    // 게시글 만들기
    @Transactional
    public Post createPost(PostCreateRequest request, Long userId,String jwt){
        UserResponse user = userService.fetchUserResponseFromAuthServer(
                authServer+"/server/user/id/"+userId,jwt,10_000,10_1000
        ).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));

        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(()-> new NoSuchElementException("비정상적인 접근입니다"));

        Post post = Post.CreatePost(userId,request.getTitle(),request.getText(),community);
        if(request.getAddresses() != null){
            request.getAddresses().stream()
                    .forEach(e->{
                        if(!StringUtils.hasText(e.getAddress()) || e.getPriority() == null){
                            throw new IllegalArgumentException("바인딩에 실패하였습니다");
                        }
                        Picture picture = Picture.createPicture(e.getAddress(),e.getPriority());
                        post.addPictures(picture);
                    });
        }


        return postRepository.save(post);

    }

    // 특정 게시글 댓글 불러오기

    public List<ThreadResponse> getThreadByPostId(long postId,String jwt){
        List<Thread> threads = threadRepository.getThreadByPostId(postId);


        return threads.stream()
                .map(e->{
                    UserResponse user = userService.fetchUserResponseFromAuthServer(
                            authServer+"/server/user/id/"+e.getUserId(),jwt,10_000,10_1000
                    ).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));
                    return new ThreadResponse(
                            mapToChildThreadResponse(e.getChilds(),user),
                            e.getId(),
                            user.getId(),
                            e.getCreatedAt(),
                            e.getText(),
                            user.getNickName(),
                            user.getIsTutor(),
                            user.getProfile()
                    );
                }).collect(Collectors.toList());
    }

    // 불러온 자식 댓글 리스트를 응답객체로 반환하기
    private List<ChildThreadResponse> mapToChildThreadResponse(List<Thread> threads,UserResponse user){
        return threads.stream()
                .map(e->{
                    return new ChildThreadResponse(
                        e.getId(),
                        user.getId(),
                        e.getCreatedAt(),
                        e.getText(),
                        user.getNickName(),
                        user.getIsTutor(),
                        user.getProfile());
                }).collect(Collectors.toList());
    }



    // 불러온 게시물 리스트를 응답객체로 변환하기

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
                                    user.getNickName(),user.getProfile(),bltp.get(0),bltp.get(1),bltp.get(2),bltp.get(3),
                                    s.getCreatedAt(),user.getIsTutor(),s.getView()
                            );
                        }).collect(Collectors.toList());
    }

    // 불러온 게시물을 응답객체로 반환하기

    private PostDetailResponse mapToPostDetail(Post post,boolean myBookmark,UserResponse user){
        List<Integer> bltp = getBLTP(post.getId());
        List<String> pictures = post.getPictures()
                .stream().sorted(Comparator.comparingInt(Picture::getPriority)
                ).map(Picture::getAddress).toList();


        return new PostDetailResponse(
                post.getId(),post.getUserId(),post.getTitle(),
                user.getNickName(),user.getProfile(),
                bltp.get(0),bltp.get(1),bltp.get(2),bltp.get(3),
                post.getCreatedAt(),user.getIsTutor(),myBookmark,post.getText()
                ,post.getView(),pictures

        );
    }

    // 게시글의 북마크, 좋아요, 댓글, 사진 수를 가져오기
    private List<Integer> getBLTP(Long postId){
        int bookmark = bookmarkRepository.countBookmarkByPostId(postId);
        int like = likeRepository.countLikeByPostId(postId);
        int thread = threadRepository.countThreadByPostId(postId);
        int picture = pictureRepository.countPictureByPostId(postId);


        return List.of(bookmark,like,thread,picture);

    }





}
