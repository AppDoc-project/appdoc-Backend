package webdoc.authentication.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.request.*;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.enums.AuthenticationProcess;
import webdoc.authentication.domain.exceptions.HasReservationOrLessonException;
import webdoc.authentication.domain.exceptions.WrongPasswordException;
import webdoc.authentication.domain.response.CodeMessageResponse;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.SettingService;
import webdoc.authentication.utility.messageprovider.CommonMessageProvider;
import webdoc.authentication.utility.messageprovider.ResponseCodeProvider;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

/*
* 유저 정보 설정 관련 응답 처리
 */
@RestController
@RequestMapping("/auth/setting")
@Slf4j
@RequiredArgsConstructor
public class AuthSettingController {

    @Value("${server.url}")
    private String url;

    private final SettingService settingService;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Value("${file.dir}")
    private String path;


    @PatchMapping("/password")
    public CodeMessageResponse changePassword(@Validated @RequestBody PasswordChangeRequest passwordChangeRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("값 검증에 실패하였습니다");
        }
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            settingService.changePassword(passwordChangeRequest.getCurrentPassword(),passwordChangeRequest.getChangedPassword(),user.getId());
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException | WrongPasswordException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/contact")
    public CodeMessageResponse changeContact(@Validated @RequestBody ContactChangeRequest contactChangeRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("값 검증에 실패하였습니다");
        }
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            settingService.changeContact(contactChangeRequest.getCurrentPassword(), contactChangeRequest.getContact(), user.getId());
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException | WrongPasswordException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/selfdescription")
    public CodeMessageResponse changeSelfDescription(@Validated @RequestBody SelfDescriptionChangeRequest selfDescriptionChangeRequest,
                                                     BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("값 검증에 실패하였습니다");
        }
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            settingService.changeSelfDescription(selfDescriptionChangeRequest.getSelfDescription(), user.getId());
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException | IllegalStateException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/nickname")
    public CodeMessageResponse changeNickName(@Validated @RequestBody NickNameChangeRequest nickNameChangeRequest,
                                              BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("값 검증에 실패하였습니다");
        }

        try{

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            settingService.changeNickName(nickNameChangeRequest.getNickName(), user.getId());
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);

        }catch(IllegalStateException | NoSuchElementException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/profile")
    public CodeMessageResponse changeProfile(@Validated @RequestBody ProfileChangeRequest profileChangeRequest,
                                             BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("값 검증에 실패하였습니다");
        }

        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            settingService.changeProfile(profileChangeRequest.getProfile(),user.getId());
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }



    @PostMapping("/removal")
    public CodeMessageResponse deleteAccount(@Validated @RequestBody AccountClosureRequest request, BindingResult bindingResult,
                                             HttpServletRequest req){
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("값 검증에 실패하였습니다");
        }

        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String jwt = req.getHeader("Authorization");
            settingService.deleteAccount(user.getId(),request.getPassword(),jwt);
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException | WrongPasswordException| HasReservationOrLessonException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }



    @PostMapping("/image")
    public CodeMessageResponse postProfile(@RequestParam("file") MultipartFile file){

            if (file.isEmpty()) {
                throw new IllegalArgumentException("바인딩 실패");
            }
            String imageUrl;
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
                imageUrl = url + "/auth/image" + "/" + uuid + "." + extension;
            } catch (IOException e) {
                // 파일 전송 중 오류 처리
                throw new RuntimeException(e);
            }


        return new CodeMessageResponse(imageUrl,200,ResponseCodeProvider.SUCCESS);
    }



    Tutor createTutor(String email,  String password,String name,String contact, String selfDescription){
        return
                Tutor.builder()
                        .authenticationAddress("http://hello/sdas")
                        .authenticationProcess(AuthenticationProcess.AUTHENTICATION_SUCCESS)
                        .email(email)
                        .name(name)
                        .password(password)
                        .role("ROLE_TUTOR")
                        .contact(contact)
                        .selfDescription(selfDescription).build();

    }
}
