package webdoc.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.TutorSpeciality;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.response.UserResponse;
import webdoc.authentication.domain.entity.user.tutee.Tutee;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.enums.AuthenticationProcess;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 다른 서버를 위한 인증서비스 제공 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServerService {
    private final UserRepository userRepository;

    // 튜터 이름을 통해 튜터 정보 반환
    public List<UserResponse> fetchTutorsByName(String name){
        List<Tutor> tutors = userRepository.findTutorByName(name);

        return tutors.stream()
                .filter(e->e.getAuthenticationProcess() == AuthenticationProcess.AUTHENTICATION_SUCCESS)
                .map(e->{

                    List<Specialities> specialities =
                            e.getSpecialities()
                                    .stream().map(TutorSpeciality::getSpecialities)
                                    .toList()
                                    .stream().collect(Collectors.toSet())
                                    .stream().collect(Collectors.toList());

                    return UserResponse.builder()
                            .contact(e.getContact())
                            .email(e.getEmail())
                            .id(e.getId())
                            .isTutor(true)
                            .name(e.getName())
                            .nickName(e.getName())
                            .profile(e.getProfile())
                            .role("ROLE_TUTOR")
                            .selfDescription(e.getSelfDescription())
                            .specialities(specialities)
                            .build();


                }).collect(Collectors.toList());

    }
    // 유저 id를 통해 user의 본명까지 반환
    public UserResponse fetchFullUserById(Long id){

        User user = userRepository.findUserById(id)
                .orElse(null);

        if (user == null){
            return
                    UserResponse.builder()
                            .contact("")
                            .name("탈퇴회원")
                            .email("")
                            .id(null)
                            .isTutor(false)
                            .nickName("탈퇴회원")
                            .profile("")
                            .selfDescription("")
                            .specialities(null)
                            .role("")
                            .build();
        }

        else if (user instanceof Tutor){
            Tutor tutor = (Tutor) user;

            List<Specialities> specialities =
                    tutor.getSpecialities()
                            .stream().map(TutorSpeciality::getSpecialities)
                            .toList()
                            .stream().collect(Collectors.toSet())
                            .stream().collect(Collectors.toList());
            return
                    UserResponse.builder()
                            .contact(tutor.getContact())
                            .email(tutor.getEmail())
                            .id(tutor.getId())
                            .isTutor(true)
                            .name(tutor.getName())
                            .nickName(tutor.getName())
                            .profile(tutor.getProfile())
                            .role(tutor.getRole())
                            .specialities(specialities)
                            .build();
        }

        else{
            Tutee tutee = (Tutee) user;

            return
                    UserResponse.builder()
                            .contact(tutee.getContact())
                            .email(tutee.getEmail())
                            .id(tutee.getId())
                            .isTutor(false)
                            .name(tutee.getName())
                            .nickName(tutee.getNickName())
                            .profile(tutee.getProfile())
                            .specialities(null)
                            .role(tutee.getRole())
                            .build();
        }

    }

    // 유저 아이디로 성명 제외 정보 반환

    public UserResponse fetchUserById(Long id){

        User user = userRepository.findUserById(id)
                .orElse(null);

        if (user == null){
            return
                    UserResponse.builder()
                            .contact("")
                            .email("")
                            .id(null)
                            .isTutor(false)
                            .nickName("탈퇴회원")
                            .profile("")
                            .selfDescription("")
                            .specialities(null)
                            .role("")
                            .build();
        }

        else if (user instanceof Tutor){
            Tutor tutor = (Tutor) user;

            List<Specialities> specialities =
                    tutor.getSpecialities()
                            .stream().map(TutorSpeciality::getSpecialities)
                            .toList()
                            .stream().collect(Collectors.toSet())
                            .stream().collect(Collectors.toList());
            return
                    UserResponse.builder()
                            .contact(tutor.getContact())
                            .email(tutor.getEmail())
                            .id(tutor.getId())
                            .isTutor(true)
                            .selfDescription(tutor.getSelfDescription())
                            .nickName(tutor.getName())
                            .profile(tutor.getProfile())
                            .role(tutor.getRole())
                            .specialities(specialities)
                            .build();
        }

        else{
            Tutee tutee = (Tutee) user;

            return
                    UserResponse.builder()
                            .contact(tutee.getContact())
                            .email(tutee.getEmail())
                            .id(tutee.getId())
                            .isTutor(false)
                            .nickName(tutee.getNickName())
                            .profile(tutee.getProfile())
                            .specialities(null)
                            .role(tutee.getRole())
                            .build();
        }

    }

    // 이메일로 유저 성명 정보 제외 반환
    public UserResponse fetchUserByEmail(String email){

        User user = userRepository.findUserByEmail(email)
                .orElse(null);

        if (user == null){
            return
                    UserResponse.builder()
                            .contact("")
                            .email("")
                            .id(null)
                            .isTutor(false)
                            .nickName("탈퇴회원")
                            .profile("")
                            .selfDescription("")
                            .specialities(null)
                            .build();
        }

        else if (user instanceof Tutor){
            Tutor tutor = (Tutor) user;

            List<Specialities> specialities =
                    tutor.getSpecialities()
                            .stream().map(TutorSpeciality::getSpecialities)
                            .toList()
                            .stream().collect(Collectors.toSet())
                            .stream().collect(Collectors.toList());
            return
                    UserResponse.builder()
                            .contact(tutor.getContact())
                            .email(tutor.getEmail())
                            .id(tutor.getId())
                            .isTutor(true)
                            .nickName(tutor.getName())
                            .profile(tutor.getProfile())
                            .specialities(specialities)
                            .build();
        }

        else{
            Tutee tutee = (Tutee) user;

            return
                    UserResponse.builder()
                            .contact(tutee.getContact())
                            .email(tutee.getEmail())
                            .id(tutee.getId())
                            .isTutor(false)
                            .nickName(tutee.getNickName())
                            .profile(tutee.getProfile())
                            .specialities(null)
                            .build();
        }

    }

    // 모든 강사 목록을 가져옴
    public List<UserResponse> fetchAllTutors(){

        return userRepository.findAll()
                .stream().filter(e-> e instanceof Tutor && ((Tutor) e).getAuthenticationProcess() == AuthenticationProcess.AUTHENTICATION_SUCCESS)
                .map(e->{
                    Tutor tutor = (Tutor) e;

                    List<Specialities> specialities =
                            tutor.getSpecialities()
                                    .stream().map(TutorSpeciality::getSpecialities)
                                    .toList()
                                    .stream().collect(Collectors.toSet())
                                    .stream().collect(Collectors.toList());

                    return UserResponse.builder()
                            .contact(tutor.getContact())
                            .email(tutor.getEmail())
                            .id(tutor.getId())
                            .isTutor(true)
                            .nickName(tutor.getName())
                            .profile(tutor.getProfile())
                            .specialities(specialities)
                            .build();

                }).collect(Collectors.toList());






    }
}
