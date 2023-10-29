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
import webdoc.community.domain.entity.post.response.PostDetailResponse;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.post.response.ThreadResponse;
import webdoc.community.domain.entity.user.User;
import webdoc.community.domain.entity.user.doctor.Doctor;
import webdoc.community.domain.entity.user.patient.Patient;
import webdoc.community.repository.*;

import java.util.*;

import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {

    @Value("${basic.image}")
    String basicImage;

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private  final ThreadRepository threadRepository;
    private final LikeRepository likeRepository;

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

        User user = userRepository.findById(userId).orElseThrow(()->new NoSuchElementException("해당하는 유저가 없습니다"));
        Post post = postRepository.findById(postId).orElseThrow(()->new NoSuchElementException("해당하는 유저가 없습니다"));

        Thread thread = Thread.createThread(request.getText(),post,user);

        threadRepository.save(thread);
        return thread;
    }

    // 댓글의 댓글 작성
    @Transactional
    public Thread createThreadOfThread(Long userId, ThreadOfThreadCreateRequest request){
        Long postId = request.getPostId();

        User user = userRepository.findById(userId).orElseThrow(()->new NoSuchElementException("해당하는 유저가 없습니다"));
        Post post = postRepository.findById(postId).orElseThrow(()->new NoSuchElementException("해당하는 유저가 없습니다"));
        Thread parentThread = threadRepository.findById(request.getParentThreadId()).orElseThrow(()->new NoSuchElementException("해당하는 댓글이 없습니다"));

        // 대댓글에 대댓글을 달려하거나, 부모 댓글의 게시물과 현재 게시물이 다른경우 실패
        if(parentThread.getParent()!=null || parentThread.getPost() != post ){
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        Thread childThread = Thread.createThread(request.getText(),post,user);
        parentThread.setThisAsParent(childThread);

        threadRepository.save(childThread);

        return childThread;

    }


    // 게시판의 게시글 불러오기 처음 fetch할 때 사용
    @Transactional
    public List<PostResponse> getPostsWithLimit(Long communityId,Integer limit){

        Community community = communityRepository.findById(communityId)
                .orElseThrow(()->new NoSuchElementException("해당하는 게시판이 없습니다"));

        PageRequest pageRequest = PageRequest.of(0,limit, Sort.by(Sort.Direction.DESC,
                "id"));

        Slice<Post> page = postRepository.getPostByCommunityAndLimit(community,pageRequest);

        return mapToPostResponse(page.getContent());
    }

    // 게시판의 게시글 불러오기 이후 fetch에서 사용
    @Transactional
    public List<PostResponse> getPostsWithLimitAndIdAfter(Long communityId,Long postId,Integer limit){

        Community community = communityRepository.findById(communityId)
                .orElseThrow(()->new NoSuchElementException("해당하는 게시판이 없습니다"));

        PageRequest pageRequest = PageRequest.of(0,limit, Sort.by(Sort.Direction.DESC,
                "id"));

        Slice<Post> page = postRepository.getPostByCommunityAndLimitAndId(community,postId,pageRequest);

        return mapToPostResponse(page.getContent());
    }

    // 특정 글 불러오기
    public PostDetailResponse getCertainPost(Long postId,Long userId){
        Post post = postRepository.getCertainPost(postId).orElseThrow(()->new NoSuchElementException("해당하는 게시물이 없습니다"));
        User user = userRepository.findById(userId).orElseThrow(()-> new NoSuchElementException("해당하는 유저가 없습니다"));
        boolean bookmarkYN = bookmarkRepository.findBookmarkByPostAndUser(post, user).size() > 0;
        return mapToPostDetail(post,bookmarkYN);
    }


    @Transactional
    public Post createPost(PostCreateRequest request, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NoSuchElementException("비정상적인 접근입니다"));

        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(()-> new NoSuchElementException("비정상적인 접근입니다"));


        Post post = Post.CreatePost(user,request.getTitle(),request.getText(),community);

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

    private List<PostResponse> mapToPostResponse(List<Post> list){
        return
                list.stream()
                        .map(s->{
                            List<Integer> bltp = getBLTP(s);
                            return new PostResponse(
                                    s.getId(),s.getUser().getId(),s.getTitle(),
                                    s.getUser().getNickName(),s.getUser().getProfile(),bltp.get(0),bltp.get(1),bltp.get(2),bltp.get(3),
                                    s.getCreatedAt(),s.getUser().isDoctor()
                            );
                        }).collect(Collectors.toList());
    }

    private PostDetailResponse mapToPostDetail(Post post,boolean myBookmark){
        List<Integer> bltp = getBLTP(post);
        List<String> pictures = post.getPictures()
                .stream().sorted(Comparator.comparingInt(Picture::getPriority)
                ).map(Picture::getAddress).toList();

        return new PostDetailResponse(
                post.getId(),post.getUser().getId(),post.getTitle(),
                post.getUser().getNickName(),post.getUser().getProfile(),
                bltp.get(0),bltp.get(1),bltp.get(2),bltp.get(3),
                post.getCreatedAt(),post.getUser().isDoctor(),myBookmark,post.getText()
                ,pictures

        );
    }

    private List<Integer> getBLTP(Post post){
        int bookmark = bookmarkRepository.countBookmarkByPost(post);
        int like = likeRepository.countLikeByPost(post);
        int thread = threadRepository.countThreadByPost(post);
        int picture = pictureRepository.countPictureByPost(post);

        return List.of(bookmark,like,thread,picture);

    }



//    List<ThreadResponse> threads = new ArrayList<>();
//        post.getThreads().stream()
//            .collect(Collectors.groupingBy(
//            Thread::getParent,
//                     Collectors.mapping(
//                             thread -> thread.getParent() == null ? null : thread,
//            Collectors.toList()
//            )
//            ))
//            .forEach((k,v)->{
//        ThreadResponse threadResponse = new ThreadResponse(
//                null,k.getId(),k.get
//        );
//    });

}
