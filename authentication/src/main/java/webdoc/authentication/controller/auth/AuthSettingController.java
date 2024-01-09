package webdoc.authentication.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.request.*;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.domain.entity.user.tutor.request.TutorSpecialityRequest;
import webdoc.authentication.domain.exceptions.WrongPasswordException;
import webdoc.authentication.domain.response.CodeMessageResponse;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.SettingService;
import webdoc.authentication.utility.messageprovider.CommonMessageProvider;
import webdoc.authentication.utility.messageprovider.ResponseCodeProvider;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/setting")
@Slf4j
@RequiredArgsConstructor
public class AuthSettingController {
    private final SettingService settingService;
    private final UserRepository userRepository;

    @Value("${file.dir}")
    private String path;
    @Value("${server.add}")
    private String address;

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

    @PatchMapping("/speciality")
    public CodeMessageResponse changeSpeciality(@Validated @RequestBody TutorSpecialityRequest request, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("값 검증에 실패하였습니다");
        }

        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            settingService.changeSpecialities(user.getId(),request.getAuthenticationAddress(), new HashSet<>(request.getSpecialities()));
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);
        }catch(NoSuchElementException e){
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
                imageUrl = address + "/" + uuid + "." + extension;
            } catch (IOException e) {
                // 파일 전송 중 오류 처리
                throw new RuntimeException(e);
            }


        return new CodeMessageResponse(imageUrl,200,ResponseCodeProvider.SUCCESS);
    }


}
