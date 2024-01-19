package webdoc.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
import webdoc.community.domain.entity.post.response.ChildThreadResponse;
import webdoc.community.domain.entity.post.response.PostDetailResponse;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.post.response.ThreadResponse;
import webdoc.community.domain.entity.report.PostReport;
import webdoc.community.domain.entity.report.ThreadReport;
import webdoc.community.domain.entity.report.request.ReportCreateRequest;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.exceptions.ReportAlreadyExistsException;
import webdoc.community.domain.exceptions.UserBannedException;
import webdoc.community.repository.*;
import webdoc.community.utility.LocalDateTimeToString;

import java.time.LocalDateTime;
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
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final PictureRepository pictureRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BannedRepository bannedRepository;

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

        // 차단 시 댓글 작성 불가
        Banned banned = bannedRepository.findBannedByUserIdAndUntilWhenAfter(userId, LocalDateTime.now())
                .orElse(null);

        if (banned != null){
            throw new UserBannedException("정지된 유저 입니다",banned.getUntilWhen());
        }

        Post post = postRepository.findById(postId).orElseThrow(()->new NoSuchElementException("해당하는 게시글이 없습니다"));
        Thread thread = Thread.createThread(request.getText(),post,userId);
        threadRepository.save(thread);
        return thread;

    }

    // 댓글의 댓글 작성
    @Transactional
    public Thread createThreadOfThread(ThreadOfThreadCreateRequest request,Long userId){
        Long postId = request.getPostId();

        // 차단 시 글 작성 불가
        Banned banned = bannedRepository.findBannedByUserIdAndUntilWhenAfter(userId, LocalDateTime.now())
                .orElse(null);

        if (banned != null){
            throw new UserBannedException("정지된 유저 입니다",banned.getUntilWhen());
        }


        Post post = postRepository.findById(postId).orElseThrow(()->new NoSuchElementException("해당하는 유저가 없습니다"));
        Thread parentThread = threadRepository.findById(request.getParentThreadId()).orElseThrow(()->new NoSuchElementException("해당하는 댓글이 없습니다"));

        // 대댓글에 대댓글을 달려하거나, 부모 댓글의 게시물과 현재 게시물이 다른경우 실패
        if(parentThread.getParent()!=null || parentThread.getPost() != post ){
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        Thread childThread = Thread.createThread(request.getText(),post,userId);
        parentThread.setThisAsParent(childThread);
        threadRepository.save(childThread);
        return childThread;

    }


    // 게시판의 게시글 불러오기 처음 fetch할 때 사용 : 최신순
    public List<PostResponse> getPostsWithLimit(Long communityId, Integer limit, String jwt){
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
    @Transactional
    public PostDetailResponse getCertainPost(Long postId,Long userId,String jwt){
        Post post = postRepository.getCertainPost(postId).orElseThrow(()->new NoSuchElementException("해당하는 게시물이 없습니다"));
        post.viewPlus();

        boolean bookmarkYN = bookmarkRepository.findBookmarkByPostIdAndUserId(postId, userId).orElse(null) != null;
        return mapToPostDetail(post,bookmarkYN,jwt);
    }


    // 게시글 만들기
    @Transactional
    public Post createPost(PostCreateRequest request, Long userId){
        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(()-> new NoSuchElementException("비정상적인 접근입니다"));

        // 차단 시 게시글 작성 불가
        Banned banned = bannedRepository.findBannedByUserIdAndUntilWhenAfter(userId, LocalDateTime.now())
                .orElse(null);

        if (banned != null){
            throw new UserBannedException("정지된 유저 입니다",banned.getUntilWhen());
        }


        Post post = Post.CreatePost(userId,request.getTitle(),request.getText(),community);
        if(request.getAddresses() != null){
            request.getAddresses()
                    .forEach(e->{
                        Picture picture = Picture.createPicture(e);
                        post.addPictures(picture);
                    });
        }

        return postRepository.save(post);

    }

    // 게시글 수정하기
    @Transactional
    public Post modifyPost(PostModifyRequest request, long userId){
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(()->new NoSuchElementException("해당하는 게시글이 없습니다"));
        if (userId != post.getUserId()){
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        // 차단 시 게시글 수정 불가
        Banned banned = bannedRepository.findBannedByUserIdAndUntilWhenAfter(userId, LocalDateTime.now())
                .orElse(null);

        if (banned != null){
            throw new UserBannedException("정지된 유저 입니다",banned.getUntilWhen());
        }


        post.modifyPost(request);
        if(request.getAddresses() != null){
            List<Picture> pictures = post.getPictures();
            pictures.clear();
            request.getAddresses()
                    .forEach(e->{
                        Picture picture = Picture.createPicture(e);
                        post.addPictures(picture);
                    });
        }

        return post;
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
                            LocalDateTimeToString.convertToLocalDateTimeString(e.getCreatedAt()),
                            e.getText(),
                            user.getNickName(),
                            user.getIsTutor(),
                            user.getProfile()
                    );
                }).collect(Collectors.toList());
    }

    // 게시글 전체 검색 & 처음 검색에서 사용
    public List<PostResponse> entireSearchPost(Integer limit, String keyword, PostSearchType searchType , String jwt){
        PageRequest pageRequest = PageRequest.of(0,limit, Sort.by(Sort.Direction.DESC,
                "id"));


        Slice<Post> page;
        if (searchType == PostSearchType.TITLE){
           page = postRepository.getPostByTitleAndLimit(keyword,pageRequest);
        }else if(searchType == PostSearchType.CONTENT){
            page = postRepository.getPostByContentAndLimit(keyword,pageRequest);
        }else{
            page = postRepository.getPostByContentAndTitleAndLimit(keyword,pageRequest);
        }

        return mapToPostResponse(page.getContent(),jwt);
    }

    // 게시글 전체 검색 & 이후 검색에서 사용
    public List<PostResponse> entireSearchPostAfter(Integer limit, String keyword,Long postId, PostSearchType searchType , String jwt){
        PageRequest pageRequest = PageRequest.of(0,limit, Sort.by(Sort.Direction.DESC,
                "id"));
        Slice<Post> page;
        if (searchType == PostSearchType.TITLE){
            page = postRepository.getPostByTitleAndLimitAndId(keyword,postId,pageRequest);
        }else if(searchType == PostSearchType.CONTENT){
            page = postRepository.getPostByContentAndLimitAndId(keyword,postId,pageRequest);
        }else{
            page = postRepository.getPostByContentAndTitleAndLimitAndId(keyword,postId,pageRequest);
        }

        return mapToPostResponse(page.getContent(),jwt);
    }

    // 게시판 별 검색 & 처음 검색에서 사용
    public List<PostResponse> communitySearchPost(Integer limit, String keyword,Long communityId, PostSearchType searchType , String jwt){
        PageRequest pageRequest = PageRequest.of(0,limit, Sort.by(Sort.Direction.DESC,
                "id"));
        communityRepository.findById(communityId)
                .orElseThrow(()->new NoSuchElementException("해당하는 게시판이 없습니다"));

        Slice<Post> page;
        if (searchType == PostSearchType.TITLE){
            page = postRepository.getPostByCommunityAndTitleAndLimit(keyword,communityId,pageRequest);
        }else if(searchType == PostSearchType.CONTENT){
            page = postRepository.getPostByCommunityAndContentAndLimit(keyword,communityId,pageRequest);
        }else{
            page = postRepository.getPostByCommunityAndContentAndTitleAndLimit(keyword,communityId,pageRequest);
        }

        return mapToPostResponse(page.getContent(),jwt);
    }

    // 게시판 별 검색 & 처음 검색에서 사용
    public List<PostResponse> communitySearchPostAfter(Integer limit, Long postId ,String keyword,Long communityId, PostSearchType searchType , String jwt){
        PageRequest pageRequest = PageRequest.of(0,limit, Sort.by(Sort.Direction.DESC,
                "id"));
        communityRepository.findById(communityId)
                .orElseThrow(()->new NoSuchElementException("해당하는 게시판이 없습니다"));

        Slice<Post> page;
        if (searchType == PostSearchType.TITLE){
            page = postRepository.getPostByCommunityAndTitleAndLimitAndId(keyword,communityId,postId,pageRequest);
        }else if(searchType == PostSearchType.CONTENT){
            page = postRepository.getPostByCommunityAndContentAndLimitAndId(keyword,communityId,postId,pageRequest);
        }else{
            page = postRepository.getPostByCommunityAndContentAndTitleAndLimitAndId(keyword,communityId,postId,pageRequest);
        }

        return mapToPostResponse(page.getContent(),jwt);
    }

    // 게시글 좋아요
    @Transactional
    public boolean enrollLike(Long userId,Long postId){
        Like like = likeRepository.findLikeByUserIdAndPostId(userId,postId).orElse(null);
        Post post = postRepository.getCertainPost(postId)
                .orElseThrow(()-> new NoSuchElementException("해당하는 게시글이 존재하지 않습니다"));
        if (like != null) return false;
        Like.createLike(userId,post);
        return true;
    }

    // 게시글 북마크
    @Transactional
    public void toggleBookmark(Long userId, Long postId){
        Post post = postRepository.getCertainPost(postId)
                .orElseThrow(()-> new NoSuchElementException("해당하는 게시글이 존재하지 않습니다"));

        Bookmark bookmark = bookmarkRepository.findBookmarkByPostIdAndUserId(postId,userId)
                .orElse(null);

        if (bookmark != null){
            post.getBookmarks().remove(bookmark);
        }else{
            Bookmark.createBookmark(userId,post);
        }
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(long userId, Long postId){
        // 게시글 존재 여부 확인
        Post post = postRepository.getCertainPost(postId)
                .orElseThrow(()-> new NoSuchElementException("해당하는 게시글이 존재하지 않습니다"));

        // 게시글이 userId에 해당하는 user의 것이면 삭제
        if (post.getUserId() != userId){
            throw new IllegalStateException("삭제하려는 글의 작성자가 아닙니다");
        }
        postRepository.delete(post);


    }

    // 댓글 삭제
    @Transactional

    public void deleteThread(Long userId, Long threadId){
        // 댓글 존재여부 확인
        Thread thread = threadRepository.getThreadById(threadId)
                .orElseThrow(()-> new NoSuchElementException("해당하는 댓글이 존재하지 않습니다"));

        if (thread.getUserId() != userId){
            throw new IllegalStateException("삭제하려는 댓글의 작성자가 아닙니다");
        }
        thread.getPost().getThreads().remove(thread);
        threadRepository.delete(thread);

    }

    // 댓글 신고
    @Transactional
    public ThreadReport reportThread(Long userId, ReportCreateRequest request){
        Thread thread = threadRepository.findById(request.getId())
                .orElseThrow(()->new NoSuchElementException("해당하는 댓글이 없습니다"));

        ThreadReport threadReport =
                reportRepository.findThreadReportByUserIdAndThreadId(userId,request.getId())
                        .orElse(null);
        if(threadReport!=null){
            throw new ReportAlreadyExistsException("이미 신고한 댓글입니다");
        }

        threadReport = ThreadReport.createThreadReport(request.getReason(),userId,thread);
        reportRepository.save(threadReport);

        return threadReport;
    }

    // 게시글 신고
    @Transactional
    public PostReport reportPost(Long userId, ReportCreateRequest request){
        Post post = postRepository.findById(request.getId())
                .orElseThrow(()->new NoSuchElementException("해당하는 글이이 없습니다"));

        PostReport postReport =
                reportRepository.findPostReportByUserIdAndPostId(userId,request.getId())
                        .orElse(null);
        if(postReport!=null){
            throw new ReportAlreadyExistsException("이미 신고한 글입니다");
        }

        postReport = PostReport.createPostReport(request.getReason(),userId,post);
        reportRepository.save(postReport);

        return postReport;
    }

    // 불러온 자식 댓글 리스트를 응답객체로 반환하기
    private List<ChildThreadResponse> mapToChildThreadResponse(List<Thread> threads,UserResponse user){
        return threads.stream()
                .map(e->{
                    return new ChildThreadResponse(
                        e.getId(),
                        user.getId(),
                        LocalDateTimeToString.convertToLocalDateTimeString(e.getCreatedAt()),
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
                                    user.getNickName(),user.getProfile(),s.getText(),bltp.get(0),bltp.get(1),bltp.get(2),bltp.get(3),
                                    LocalDateTimeToString.convertToLocalDateTimeString(s.getCreatedAt()),user.getIsTutor(),s.getView(),s.getCommunity().getName(),s.getCommunity().getId()
                            );
                        }).collect(Collectors.toList());
    }

    // 불러온 게시물을 응답객체로 반환하기

    private PostDetailResponse mapToPostDetail(Post post,boolean myBookmark,String jwt){
        List<Integer> bltp = getBLTP(post.getId());
        List<String> pictures = post.getPictures()
                .stream().sorted(Comparator.comparingLong(Picture::getId)
                ).map(Picture::getAddress).toList();
        UserResponse user = userService.fetchUserResponseFromAuthServer(
                authServer+"/server/user/id/"+post.getUserId(),jwt,10_000,10_1000
        ).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));


        return new PostDetailResponse(
                post.getId(),post.getUserId(),post.getTitle(),
                user.getNickName(),user.getProfile(),
                bltp.get(0),bltp.get(1),bltp.get(2),bltp.get(3),
                LocalDateTimeToString.convertToLocalDateTimeString(post.getCreatedAt()),user.getIsTutor(),myBookmark,post.getText()
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
