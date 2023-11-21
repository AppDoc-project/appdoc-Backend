package webdoc.authentication.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import webdoc.authentication.domain.entity.user.response.UserResponse;
import webdoc.authentication.service.AuthServerService;

@RestController
@RequestMapping("/auth/server")
@Slf4j
@RequiredArgsConstructor
public class AuthServerController {

    private final AuthServerService authServerService;

    @GetMapping("/user/id/{id}")
    public UserResponse fetchUserInfo(@PathVariable long id){
        try{
            return authServerService.fetchUserById(id);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/user/email/{email}")
    public UserResponse fetchUserInfoByEmail(@PathVariable String email){
        try{
            return authServerService.fetchUserByEmail(email);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
