package webdoc.community.controller.profile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.response.ArrayResponse;
import webdoc.community.domain.response.CountResponse;
import webdoc.community.domain.response.ObjectResponse;
import webdoc.community.service.ProfileService;

@RestController
@RequestMapping("/community/profile")
@Slf4j
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;


    @GetMapping("/info")
    public ObjectResponse<CountResponse> fetchInfo(){
        try{
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (userResponse.getIsTutor()){
                return new ObjectResponse<>(profileService.tutorProfileInfo(userResponse),200);
            }else{
                return new ObjectResponse<>(profileService.tuteeProfileInfo(userResponse),200);
            }


        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/post")
    public ArrayResponse<PostResponse> fetchOwnPost(@RequestParam int page, @RequestParam int limit, HttpServletRequest req){
        try{
            String jwt = req.getHeader("Authorization");
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ArrayResponse.of(profileService.ownPost(userResponse.getId(),page,limit,jwt),200);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/thread")
    public ArrayResponse<PostResponse> fetchOwnThread(@RequestParam int page, @RequestParam int limit, HttpServletRequest req){
        try{
            String jwt = req.getHeader("Authorization");
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ArrayResponse.of(profileService.ownThread(userResponse.getId(),page,limit,jwt),200);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/bookmark")
    public ArrayResponse<PostResponse> fetchOwnBookmark(@RequestParam int page, @RequestParam int limit, HttpServletRequest req){
        try{
            String jwt = req.getHeader("Authorization");
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ArrayResponse.of(profileService.ownBookmark(userResponse.getId(),page,limit,jwt),200);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }







}
