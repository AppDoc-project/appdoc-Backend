package webdoc.community.controller.community;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webdoc.community.domain.entity.community.CommunityResponse;
import webdoc.community.domain.entity.like.request.CreateRequestWithPostId;
import webdoc.community.domain.entity.post.enums.PostSearchType;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.post.request.PostModifyRequest;
import webdoc.community.domain.entity.post.request.ThreadCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadOfThreadCreateRequest;
import webdoc.community.domain.entity.post.response.PostDetailResponse;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.post.response.ThreadResponse;
import webdoc.community.domain.entity.report.request.ReportCreateRequest;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.exceptions.ReportAlreadyExistsException;
import webdoc.community.domain.exceptions.UserBannedException;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.domain.response.ArrayResponse;
import webdoc.community.domain.response.ObjectResponse;
import webdoc.community.service.CommunityService;
import webdoc.community.utility.messageprovider.CommonMessageProvider;
import webdoc.community.utility.messageprovider.ResponseCodeProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/community")
@Slf4j
@RequiredArgsConstructor
public class CommunityController {

    @Value("${file.dir}")
    private String path;


    private final CommunityService communityService;

    // 게시판 목록 가져오기
    @GetMapping("/list")
    public ArrayResponse<CommunityResponse> getAllCommunities(){
        List<CommunityResponse> ret = communityService.getAllCommunities();
        return ArrayResponse.of(ret,200);
    }

    // 게시판에 글 작성
    @PostMapping("/post")
    public CodeMessageResponse createPost(HttpServletRequest req, @RequestBody @Validated PostCreateRequest request,
                                          BindingResult result){
        if(result.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }

        try{
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.createPost(request,user.getId());

        }catch(IllegalArgumentException | UserBannedException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);
    }

    // 게시판에 글 수정
    @PatchMapping("/post")
    public CodeMessageResponse modifyPost(HttpServletRequest req, @RequestBody @Validated PostModifyRequest request,
                                          BindingResult result){
        if(result.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }


        try{
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.modifyPost(request,user.getId());

        }catch(IllegalArgumentException | UserBannedException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);
    }

    // 게시판에 글 불러오기
    @GetMapping("/post")
    public ArrayResponse<PostResponse> getPosts(@RequestParam boolean scroll, @RequestParam(required = false) Long postId,
                                                @RequestParam(required = true) int limit, @RequestParam Long communityId,HttpServletRequest req){
        try{
            String jwt = req.getHeader("Authorization");
            if (!scroll){
                return ArrayResponse.of(communityService.getPostsWithLimit(communityId, limit,jwt),200);
            }else{
                if (postId == null) throw new IllegalArgumentException("postId를 명시해야 합니다");
                return ArrayResponse.of(communityService.getPostsWithLimitAndIdAfter(communityId,postId,limit,jwt),200);
            }
        }catch(NoSuchElementException | IllegalArgumentException e){
            throw e;
        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    //선택된 게시글 불러오기
    @GetMapping("/post/{postId}")
    public ObjectResponse<PostDetailResponse> getCertainPost(HttpServletRequest req,@PathVariable("postId") Long postId){
        try{
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String jwt = req.getHeader("Authorization");
            return new ObjectResponse<>(communityService.getCertainPost(postId,user.getId(),jwt),200);
        }catch(NoSuchElementException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // 댓글 작성하기
    @PostMapping("/thread")
    public CodeMessageResponse createThread(@Validated @RequestBody ThreadCreateRequest request,
                                            BindingResult bindingResult,HttpServletRequest req){
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }
        try{
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.createThread(user.getId(),request);
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException | UserBannedException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 대댓글을 작성하기

    @PostMapping("/thread_thread")
    public CodeMessageResponse createThreadOfThread(@Validated @RequestBody ThreadOfThreadCreateRequest request,
                                                    BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }
        try{
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.createThreadOfThread(request,user.getId());
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException | UserBannedException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 특정 게시글의 댓글 가져오기

    @GetMapping("/thread/{postId}")
    public ArrayResponse<ThreadResponse> getThreads(@PathVariable("postId")Long postId, HttpServletRequest req){
        try{
            String jwt = req.getHeader("Authorization");
            List<ThreadResponse> threadResponses = communityService.getThreadByPostId(postId,jwt);
            return new ArrayResponse<>(threadResponses,200,threadResponses.size());
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 전체 게시판 검색하기
    @GetMapping("/search")
    public ArrayResponse<PostResponse> searchFromEntirePosts(@RequestParam boolean scroll, @RequestParam(required = false) Long postId, @RequestParam int limit, @RequestParam String keyword,
                                                             @RequestParam PostSearchType postSearchType, HttpServletRequest req){
        try{
            String jwt = req.getHeader("Authorization");
            if (!scroll){

                return ArrayResponse.of(communityService.entireSearchPost(limit,keyword,postSearchType,jwt),200);
            }else{
                if (postId == null) throw new IllegalArgumentException("postId를 명시해야 합니다");
                return ArrayResponse.of(communityService.entireSearchPostAfter(limit,keyword,postId,postSearchType,jwt),200);
            }
        }catch (IllegalArgumentException e){
            throw e;
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 특정 게시판 검색하기
    @GetMapping("/search/{communityId}")
    public ArrayResponse<PostResponse> searchFromCommunity(@PathVariable Long communityId,@RequestParam boolean scroll, @RequestParam(required = false) Long postId
            ,@RequestParam int limit, @RequestParam String keyword, @RequestParam PostSearchType postSearchType, HttpServletRequest req){
        try{
            String jwt = req.getHeader("Authorization");
            if (!scroll){

                return ArrayResponse.of(communityService.communitySearchPost(limit,keyword,communityId,postSearchType,jwt),200);
            }else{
                if (postId == null) throw new IllegalArgumentException("postId를 명시해야 합니다");
                return ArrayResponse.of(communityService.communitySearchPostAfter(limit,postId,keyword,communityId,postSearchType,jwt),200);
            }
        }catch(IllegalArgumentException e){
            throw e;
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 좋아요 등록하기

    @PostMapping("/like")
    public CodeMessageResponse enrollLike(@RequestBody CreateRequestWithPostId likeCreateRequest,HttpServletResponse res){

        if(likeCreateRequest.getPostId() == null){
            throw new IllegalArgumentException("postId는 null일 수 없습니다");
        }
        try{
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            boolean success = communityService.enrollLike(userResponse.getId(), likeCreateRequest.getPostId());
            if (success){
                return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
            }else{
                res.setStatus(400);
                return new CodeMessageResponse(CommonMessageProvider.LIKE_EXISTS,400,ResponseCodeProvider.ALREADY_EXISTS);
            }
        }catch(NoSuchElementException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 북마크 토글하기
    @PostMapping("/bookmark")
    public CodeMessageResponse toggleBookmark(@RequestBody CreateRequestWithPostId createRequest){

        if(createRequest.getPostId() == null){
            throw new IllegalArgumentException("postId는 null일 수 없습니다");
        }
        try{
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.toggleBookmark(userResponse.getId(), createRequest.getPostId());
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 게시글 삭제하기
    @DeleteMapping("/post")
    public CodeMessageResponse deletePost(@RequestParam Long postId){

        try{
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.deletePost(userResponse.getId(),postId);
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException | IllegalArgumentException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 댓글 삭제하기
    @DeleteMapping("/thread")
    public CodeMessageResponse deleteThread(@RequestParam Long threadId){
        try{
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.deleteThread(userResponse.getId(),threadId);
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException | IllegalArgumentException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 글 신고하기
    @PostMapping("/report/post")
    public CodeMessageResponse reportPost(@Validated @RequestBody ReportCreateRequest reportCreateRequest, BindingResult bindingResult,
                                          HttpServletResponse res){
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }


        try{
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.reportPost(userResponse.getId(),reportCreateRequest);
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
        }catch(ReportAlreadyExistsException e){
            res.setStatus(400);
            return new CodeMessageResponse(CommonMessageProvider.REPORT_EXISTS,400,ResponseCodeProvider.ALREADY_EXISTS);
        }catch(NoSuchElementException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    // 댓글 신고하기
    @PostMapping("/report/thread")
    public CodeMessageResponse reportThread(@Validated @RequestBody ReportCreateRequest reportCreateRequest, BindingResult bindingResult,
                                            HttpServletResponse res){
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }


        try{
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.reportThread(userResponse.getId(),reportCreateRequest);
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
        }catch(ReportAlreadyExistsException e){
            res.setStatus(400);
            return new CodeMessageResponse(CommonMessageProvider.REPORT_EXISTS,400,ResponseCodeProvider.ALREADY_EXISTS);
        }catch(NoSuchElementException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }






    // 이미지 등록하기
    @PostMapping("/images/{baseUrl}")
    public ArrayResponse<String> uploadImages(HttpServletResponse res, @RequestParam("files") List<MultipartFile> files,@PathVariable String baseUrl) {
        if (files.size()>5){
            throw new IllegalArgumentException("사진은 5개 까지만 전송할 수 있습니다");
        }
        List<String> addresses = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("바인딩 실패");
            }

            String uuid = UUID.randomUUID().toString();
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

            String[] supportedExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "tiff", "tif", "ico"};

            // 현재 파일의 확장자가 지원하는 형식인지 확인
            boolean isSupported = false;
            for (String supportedExtension : supportedExtensions) {
                if (extension.equals(supportedExtension)) {
                    isSupported = true;
                    break;
                }
            }

            if (!isSupported) {
                throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
            }

            String fullPath = path + "/" + uuid + "." + extension;

            try {
                file.transferTo(new File(fullPath));
                String imageUrl = "http://" + baseUrl + "/community/image"+"/" + uuid + "." + extension;
                addresses.add(imageUrl);
            } catch (IOException e) {
                // 파일 전송 중 오류 처리
                throw new RuntimeException(e);
            }
        }

        return new ArrayResponse<>(addresses,200,addresses.size());
    }





}
