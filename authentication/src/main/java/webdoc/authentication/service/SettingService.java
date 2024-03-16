package webdoc.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.tutee.Tutee;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.exceptions.HasReservationOrLessonException;
import webdoc.authentication.domain.exceptions.WrongPasswordException;
import webdoc.authentication.repository.UserRepository;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Transactional

/*
*  인증 정보 설정 서비스
 */
public class SettingService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RedisService redisService;

    private final ReservationService reservationService;

    private final LessonService lessonService;
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



    // 회원 탈퇴
    public void deleteAccount(Long userId, String password,String jwt){
        User user = userRepository.findUserById(userId)
                .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        if (passwordEncoder.matches(password, user.getPassword())){
            boolean hasReservation = reservationService.hasLeftReservation(jwt,userId,10_000,10_000);
            boolean hasLesson = lessonService.hasLeftLesson(jwt,userId,10_000,10_000);

            if(hasLesson || hasReservation ){
                throw new HasReservationOrLessonException("탈퇴할 수 없습니다");
            }
            userRepository.delete(user);
            redisService.deleteValues(user.getEmail());

        }else{
            throw new WrongPasswordException("잘못된 비밀번호 입니다");
        }
    }
}
