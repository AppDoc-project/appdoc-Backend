package webdoc.community.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webdoc.community.domain.entity.community.CommunityResponse;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.user.User;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.domain.response.DataResponse;
import webdoc.community.service.CommunityService;
import webdoc.community.utility.messageprovider.CommonMessageProvider;
import webdoc.community.utility.messageprovider.ResponseCodeProvider;

import java.util.List;

@RestController
@RequestMapping("/community")
@Slf4j
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    @GetMapping("/list")
    public DataResponse<CommunityResponse> getAllCommunities(){
        List<CommunityResponse> ret = communityService.getAllCommunities();
        return DataResponse.of(ret,200);
    }

    @PostMapping("/post")
    public CodeMessageResponse createPost(SecurityContext securityContext,HttpServletResponse res, @RequestBody @Validated PostCreateRequest request,
                                          BindingResult result){
        if(result.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }

        try{

            User user = (User) securityContext.getAuthentication().getPrincipal();
            communityService.createPost(request,user.getId());

        }catch(Exception e){
            if (e instanceof IllegalArgumentException){
                throw e;
            }
            else{
                throw new RuntimeException(e);
            }
        }

        return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);


    }




}
