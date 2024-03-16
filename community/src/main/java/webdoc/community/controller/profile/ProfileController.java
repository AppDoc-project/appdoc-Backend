package webdoc.community.controller.profile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webdoc.community.domain.entity.post.response.PostResponse;
import webdoc.community.domain.entity.tutor.response.TutorResponse;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.response.ArrayResponse;
import webdoc.community.domain.entity.user.response.CountResponse;
import webdoc.community.domain.response.ObjectResponse;
import webdoc.community.service.ProfileService;


/*
 * 프로필 관련 응답 처리
 */
@RestController
@RequestMapping("/community/profile")
@Slf4j
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    // 자신의 프로필 정보를 가져오기
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
    // 자기가 작성한 게시글을 가져오기
    @GetMapping("/post")
    public ArrayResponse<PostResponse> fetchOwnPost(HttpServletRequest req){
        try{
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ArrayResponse.of(profileService.ownPost(userResponse.getId()),200);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 자기가 작성한 댓글 가져오기
    @GetMapping("/thread")
    public ArrayResponse<PostResponse> fetchOwnThread(HttpServletRequest req){
        try{
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ArrayResponse.of(profileService.ownThread(userResponse.getId()),200);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 자기가 북마크한 글 가져오기
    @GetMapping("/bookmark")
    public ArrayResponse<PostResponse> fetchOwnBookmark(HttpServletRequest req){
        try{
            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ArrayResponse.of(profileService.ownBookmark(userResponse.getId()),200);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 자기가 찜한 글 가져오기
    @GetMapping("/pick")
    public ArrayResponse<TutorResponse> fetchOwnPick(HttpServletRequest req){
        try{

            UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userResponse.getIsTutor()){
                throw new IllegalStateException("비정상적인 접근 입니다");
            }

            return ArrayResponse.of(profileService.ownTutor(userResponse.getId()),200);

        }catch(IllegalStateException e){
            throw e;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
