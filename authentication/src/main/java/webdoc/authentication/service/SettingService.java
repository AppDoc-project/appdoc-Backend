package webdoc.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.TutorSpeciality;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.tutee.Tutee;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.domain.exceptions.WrongPasswordException;
import webdoc.authentication.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SettingService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RedisService redisService;

    // 비밀번호 변경

    public void changePassword(String currentPassword, String changedPassword,Long userId){
        User user =
                userRepository.findUserById(userId)
                        .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));

        if (!passwordEncoder.matches(currentPassword,user.getPassword())){
            throw new WrongPasswordException("잘못된 비밀번호 입니다");
        }
        user.setPassword(passwordEncoder.encode(changedPassword));

    }

    // 연락처 변경

    public void changeContact(String currentPassword, String contact,Long userId){

        User user =
                userRepository.findUserById(userId)
                        .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));

        if (!passwordEncoder.matches(currentPassword,user.getPassword())){
            throw new WrongPasswordException("잘못된 비밀번호 입니다");
        }
        user.setContact(contact);

    }

    // 자기소개 변경

    public void changeSelfDescription(String selfDescription, Long userId){
        User user =
                userRepository.findUserById(userId)
                        .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        if (! (user instanceof Tutor)){
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        ((Tutor) user).setSelfDescription(selfDescription);
    }

    // 닉네임 변경


    public void changeNickName(String nickName, Long userId){
        User user =
                userRepository.findUserById(userId)
                        .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        if (! (user instanceof Tutee)){
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        ((Tutee) user).setNickName(nickName);
    }

    // 프로필 변경
    public void changeProfile(String profile, Long userId){
        User user =
                userRepository.findUserById(userId)
                        .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));

        user.setProfile(profile);
    }

    // 자격 변경

    public void changeSpecialities(Long userId,String authenticationAddress, Set<Specialities> specialitiesList){

        Tutor tutor = (Tutor) userRepository
                .findUserById(userId)
                .stream().filter(e->e instanceof Tutor)
                .findAny()
                .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));

        tutor.setAuthenticationAddress(authenticationAddress);
        tutor.setChangeRequests(specialitiesList);

    }

    // 회원 탈퇴
    public void deleteAccount(Long userId, String password){
        User user = userRepository.findUserById(userId)
                .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        if (passwordEncoder.matches(password, user.getPassword())){
            userRepository.delete(user);
            redisService.deleteValues(user.getEmail());

        }else{
            throw new WrongPasswordException("잘못된 비밀번호 입니다");
        }
    }
}
