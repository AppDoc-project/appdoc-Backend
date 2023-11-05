package webdoc.community.controller;

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
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadCreateRequest;
import webdoc.community.domain.entity.post.request.ThreadOfThreadCreateRequest;
import webdoc.community.domain.entity.post.response.PostDetailResponse;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.post.response.ThreadResponse;
import webdoc.community.domain.entity.user.User;
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

    @Value("${server.add}")
    private String address;

    private final CommunityService communityService;

    // 게시판 목록 가져오기
    @GetMapping("/list")
    public ArrayResponse<CommunityResponse> getAllCommunities(){
        List<CommunityResponse> ret = communityService.getAllCommunities();
        return ArrayResponse.of(ret,200);
    }

    // 게시판에 글 작성
    @PostMapping("/post")
    public CodeMessageResponse createPost(HttpServletResponse res, @RequestBody @Validated PostCreateRequest request,
                                          BindingResult result){
        if(result.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }

        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.createPost(request,user.getId());

        }catch(IllegalArgumentException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);


    }

    // 게시판에 글 첫번째로 불러오기
    @GetMapping("/post")
    public ArrayResponse<PostResponse> getPosts(@RequestParam boolean scroll, @RequestParam(required = false) Long postId,
                                                @RequestParam(required = true) int limit, @RequestParam Long communityId){
        try{
            if (!scroll){
                return ArrayResponse.of(communityService.getPostsWithLimit(communityId, limit),200);
            }else{
                return ArrayResponse.of(communityService.getPostsWithLimitAndIdAfter(communityId,postId,limit),200);
            }
        }catch(NoSuchElementException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    //선택된 게시글 불러오기
    @GetMapping("/post/{postId}")
    public ObjectResponse<PostDetailResponse> getCertainPost(@PathVariable("postId") Long postId){
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return new ObjectResponse<>(communityService.getCertainPost(postId,user.getId()),200);
        }catch(NoSuchElementException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // 댓글 작성하기
    @PostMapping("/thread")
    public CodeMessageResponse createThread(@Validated @RequestBody ThreadCreateRequest request,
                                            BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.createThread(user.getId(),request);
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/thread_thread")
    public CodeMessageResponse createThreadOfThread(@Validated @RequestBody ThreadOfThreadCreateRequest request,
                                                    BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            communityService.createThreadOfThread(user.getId(),request);
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/thread/{postId}")
    public ArrayResponse<ThreadResponse> getThreads(@PathVariable("postId")Long postId){
        try{
            List<ThreadResponse> threadResponses = communityService.getThreadByPostId(postId);
            return new ArrayResponse<>(threadResponses,200,threadResponses.size());
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/images")
    public ArrayResponse<String> uploadImages(HttpServletResponse res, @RequestParam("files") List<MultipartFile> files) {
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
                String imageUrl = address + "/media/" + uuid + "." + extension;
                addresses.add(imageUrl);
            } catch (IOException e) {
                // 파일 전송 중 오류 처리
                throw new RuntimeException(e);
            }
        }

        return new ArrayResponse<>(addresses,200,addresses.size());
    }





}
