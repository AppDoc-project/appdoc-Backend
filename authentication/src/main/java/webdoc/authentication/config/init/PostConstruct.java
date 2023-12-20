package webdoc.authentication.config.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.tutee.Tutee;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
public class PostConstruct {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void devInit(){

        Tutor tutor = Tutor.createTutor("tutor@gmail.com","tutor1234",
                "은현장","01025045779",
                "http://localhost:8080","안녕하세요 ㅎㅎㅎ");

        Tutee tutee = Tutee.createTutee(
                "tutee@gmail.com",passwordEncoder.encode("tutee1234"),"우석우","01025045779"
        );

        userRepository.saveAll(List.of(tutee,tutor));

    }

}
