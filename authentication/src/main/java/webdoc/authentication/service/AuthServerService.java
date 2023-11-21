package webdoc.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.response.UserResponse;
import webdoc.authentication.domain.entity.user.tutee.Tutee;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServerService {
    private final UserRepository userRepository;

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
                            .nickName("")
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
                            .stream().map(s->s.getSpecialities())
                            .collect(Collectors.toList());
            return
                    UserResponse.builder()
                            .contact(tutor.getContact())
                            .email(tutor.getEmail())
                            .id(tutor.getId())
                            .isTutor(true)
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
                            .nickName("")
                            .profile("")
                            .selfDescription("")
                            .specialities(null)
                            .build();
        }

        else if (user instanceof Tutor){
            Tutor tutor = (Tutor) user;

            List<Specialities> specialities =
                    tutor.getSpecialities()
                            .stream().map(s->s.getSpecialities())
                            .collect(Collectors.toList());
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
}
