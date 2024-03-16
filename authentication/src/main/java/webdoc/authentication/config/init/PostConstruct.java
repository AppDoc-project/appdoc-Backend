package webdoc.authentication.config.init;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import webdoc.authentication.domain.entity.user.tutee.Tutee;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.enums.AuthenticationProcess;
import webdoc.authentication.repository.UserRepository;

import java.util.List;
/*
* 개발 환경 초기 데이터 삽입
 */

@RequiredArgsConstructor
public class PostConstruct {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final boolean init;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void devInit(){

            if (init) {
                Tutor tutor = Tutor.createTutor("tutor@gmail.com", passwordEncoder.encode("tutor1234"),
                        "은현장", "01025045779",
                        "http://localhost:8080", "안녕하세요 ㅎㅎㅎ");

                Tutor tutor1 = Tutor.createTutor("tutor1@gmail.com", passwordEncoder.encode("tutor1234"),
                        "은지원", "01025045779",
                        "http://localhost:8080", "안녕하세요 ㅎㅎㅎ");

                tutor.changeTutorState(AuthenticationProcess.AUTHENTICATION_SUCCESS);
                tutor1.changeTutorState(AuthenticationProcess.AUTHENTICATION_SUCCESS);

                Tutee tutee = Tutee.createTutee(
                        "tutee@gmail.com", passwordEncoder.encode("tutee1234"), "우석우", "01025045779"
                );

                userRepository.saveAll(List.of(tutee, tutor,tutor1));
            }


    }

    

}
