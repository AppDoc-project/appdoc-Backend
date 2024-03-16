package webdoc.authentication.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.UserMail;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.TutorMail;
import webdoc.authentication.domain.entity.user.tutor.enums.AuthenticationProcess;
import webdoc.authentication.domain.entity.user.tutor.request.TutorCreateRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.entity.user.tutee.TuteeMail;
import webdoc.authentication.domain.entity.user.tutee.request.TuteeCreateRequest;
import webdoc.authentication.domain.entity.user.tutee.Tutee;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.exceptions.EmailDuplicationException;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserMailRepository;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.utility.generator.FourDigitsNumberGenerator;
import webdoc.authentication.utility.generator.UUIDGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
/*
* 유저 인증  서비스
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService{
    private final UserRepository userRepository;
    private final UserMailRepository userMailRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisService redisService;

    // 비밀번호 찾기 메일 전송
    public void findPassword(String email) throws MessagingException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));

        String code = FourDigitsNumberGenerator.generateFourDigitsNumber();

        // 비밀번호 찾기 코드는 3분간 유효
        emailService.sendEmail(email,code,"BEATMATE 비밀번호 찾기 인증번호");
        redisService.setValues(user.getEmail()+"password",code, Duration.ofMinutes(3L));

    }

    // 비밀번호 찾기 코드 인증
    public String validateCodeForPasswordFind(String email, String code){
        String redisCode = redisService.getValues(email+"password");

        // 해당 이메일에 대한 인증 코드가 없는 경우
        if (redisCode.equals("false")){
            throw new TimeOutException("인증 시간을 초과하였습니다");
        }
        System.out.println(redisCode);
        if (redisCode.equals(code)){

            User user = userRepository.findUserByEmail(email)
                    .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));

            String uuid = UUIDGenerator.generateRandomUUID();

            redisService.setValues(user.getEmail()+"password_auth",uuid,Duration.ofMinutes(10L));

            return uuid;
        }else{
            throw new AuthenticationServiceException("잘못된 인증 코드입니다");
        }
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(String email, String token, String password){
        String redisCode = redisService.getValues(email + "password_auth");
        redisService.deleteValues(email + "password_auth");

        // 해당 이메일에 대한 인증 코드가 없는 경우
        if (redisCode.equals("false")){
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        if (redisCode.equals(token)){
            User user = userRepository.findUserByEmail(email)
                    .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));

            user.setPassword(passwordEncoder.encode(password));
        }else{
            throw new IllegalStateException("비정상적인 접근입니다");
        }


    }


    // 튜터 계정 생성
    @Transactional
    public TutorMail createTutorUser(TutorCreateRequest dto) throws MessagingException {
        User findUser = userRepository.findByEmail(dto.getEmail()).orElse(null);
        UserMail findUserMail = userMailRepository.findByEmail(dto.getEmail()).orElse(null);
        TutorMail tutor;

        if (findUser != null){
            if (findUser instanceof Tutor
                    && ((Tutor)findUser).getAuthenticationProcess().equals(AuthenticationProcess.AUTHENTICATION_DENIED)){
                userRepository.delete(findUser);
            }
            else{throw new EmailDuplicationException("해당 이메일을 가진 유저가 존재합니다");}
        }


        String code = FourDigitsNumberGenerator.generateFourDigitsNumber();
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(3L);

        // 비밀번호 암호화
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        tutor = TutorMail.dtoToMail(dto,code,expirationDateTime);

        if(findUserMail != null){
            userMailRepository.delete(findUserMail);
        }

        userMailRepository.save(tutor);
        emailService.sendEmail(dto.getEmail(),code,"BEATMATE 회원가입 인증번호");

        return tutor;
    }

    // 튜티 계정 생성
    @Transactional
    public TuteeMail createTuteeUser(TuteeCreateRequest dto) throws MessagingException {
        User findUser = userRepository.findByEmail(dto.getEmail()).orElse(null);
        UserMail findUserMail = userMailRepository.findByEmail(dto.getEmail()).orElse(null);
        TuteeMail tutee;

        if (findUser != null){
            throw new EmailDuplicationException("해당 이메일을 가진 유저가 존재합니다");
        }

        String code = FourDigitsNumberGenerator.generateFourDigitsNumber();
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(3L);

        // 비밀번호 암호화
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        tutee = TuteeMail.dtoToMail(dto,code,expirationDateTime);

        if(findUserMail != null){
            userMailRepository.delete(findUserMail);
        }

        userMailRepository.save(tutee);
        emailService.sendEmail(dto.getEmail(),code,"BEATMATE 회원가입 인증번호");

        return tutee;
    }

    // 튜티 계정 인증
    @Transactional
    public void validateTutee(CodeRequest dto,LocalDateTime time){
        TuteeMail tuteeMail = (TuteeMail) userMailRepository.findByEmail(dto.getEmail())
                .stream().filter(e->e instanceof TuteeMail)
                .findFirst()
                .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        // 정상 인증 프로세스
        if(tuteeMail.getCode().equals(dto.getCode())
                && tuteeMail.getExpirationDateTime().isAfter(time)){
            userMailRepository.delete(tuteeMail);
            userRepository.save(Tutee.tuteeMailToTutee(tuteeMail));
        // 인증 시간 초과
        }else if(tuteeMail.getExpirationDateTime().isBefore(time)){
            throw new TimeOutException("인증 시간을 초과하였습니다");
        // 인증 번호 틀림
        }else{
            throw new AuthenticationServiceException("잘못된 인증번호 입니다");
        }
    }

    // 튜터 인증
    @Transactional
    public void validateTutor(CodeRequest dto,LocalDateTime time){
        TutorMail tutorMail = (TutorMail) userMailRepository.findByEmail(dto.getEmail())
                .stream().filter(e->e instanceof TutorMail)
                .findFirst()
                .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        // 정상 인증 프로세스
        if(tutorMail.getCode().equals(dto.getCode())
                && tutorMail.getExpirationDateTime().isAfter(time)){
            userMailRepository.delete(tutorMail);
            userRepository.save(Tutor.tutorMailToTutor(tutorMail));
            // 인증 시간 초과
        }else if(tutorMail.getExpirationDateTime().isBefore(time)){
            throw new TimeOutException("인증 시간을 초과하였습니다");
            // 인증 번호 틀림
        }else{
            throw new AuthenticationServiceException("잘못된 인증번호 입니다");
        }
    }

    //로그 아웃
    @Transactional
    public void logOut(User user){
        User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));
        redisService.deleteValues(user.getEmail());
    }


    // 이메일 중복 확인 로직
    public boolean isEmailDuplicated(String email){
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null;
    }

    // 튜터 인증 상태를 ONGOING으로 설정
    @Transactional
    public void setTutorAuthenticationSuccess(Long id){
        Tutor tutor = userRepository.findTutorById(id).orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        tutor.changeTutorState(AuthenticationProcess.AUTHENTICATION_SUCCESS);
    }

    // 튜터 인증 상태를 DENINED로 설정
    @Transactional
    public void setTutorAuthenticationDenied(Long id){
        Tutor tutor = userRepository.findTutorById(id).orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        tutor.changeTutorState(AuthenticationProcess.AUTHENTICATION_DENIED);
    }

}
