package webdoc.authentication.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.response.UserResponse;
import webdoc.authentication.service.AuthServerService;

import java.util.List;

/*
 * 다른 서비스에게 인증 서비스 제공
 */

@RestController
@RequestMapping("/auth/server")
@Slf4j
@RequiredArgsConstructor
public class AuthServerController {

    private final AuthServerService authServerService;

    // 특정 이름을 가진 강사를 fetch하는 api
    @GetMapping("/tutor/{name}")
    public List<UserResponse> fetchTutorsByName(@PathVariable String name){
        try{
            return authServerService.fetchTutorsByName(name);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }


    // id를 통해서 해당 유저의 정보를 fetch하는 api
    @GetMapping("/user/id/{id}")
    public UserResponse fetchUserInfo(@PathVariable long id){
        try{
            return authServerService.fetchUserById(id);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }


    // email을 통해서 해당 유저의 정보를 fetch하는 api
    @GetMapping("/user/email/{email}")
    public UserResponse fetchUserInfoByEmail(@PathVariable String email){
        try{
            return authServerService.fetchUserByEmail(email);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // 자기 자신의 이름을 포함한 정보를 fetch하는 api
    @GetMapping("/user/my")
    public UserResponse fetchMyInformation(){
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return authServerService.fetchFullUserById(user.getId());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }



    // 모든 강사 리스트를 fetch하는 api
    @GetMapping("/tutors")
    public List<UserResponse> fetchAllTutors(){
        return authServerService.fetchAllTutors();
    }
}
