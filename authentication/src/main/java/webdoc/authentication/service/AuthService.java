package webdoc.authentication.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.UserMail;
import webdoc.authentication.domain.entity.user.doctor.Doctor;
import webdoc.authentication.domain.entity.user.doctor.DoctorMail;
import webdoc.authentication.domain.entity.user.doctor.enums.AuthenticationProcess;
import webdoc.authentication.domain.entity.user.doctor.request.DoctorCreateRequest;
import webdoc.authentication.domain.entity.user.request.CodeRequest;
import webdoc.authentication.domain.entity.user.patient.PatientMail;
import webdoc.authentication.domain.entity.user.patient.request.PatientCreateRequest;
import webdoc.authentication.domain.entity.user.patient.Patient;
import webdoc.authentication.domain.entity.user.Token;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserMailRepository;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.utility.generator.FourDigitsNumberGenerator;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService{
    private final UserRepository userRepository;
    private final UserMailRepository userMailRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 의사 계정 생성
    @Transactional
    public DoctorMail createDoctorUser(DoctorCreateRequest dto) throws MessagingException {
        User findUser = userRepository.findByEmail(dto.getEmail()).orElse(null);
        UserMail findUserMail = userMailRepository.findByEmail(dto.getEmail()).orElse(null);
        DoctorMail doctor;


        if (findUser != null){
            if (findUser instanceof Doctor
                    && ((Doctor)findUser).getAuthenticationProcess().equals(AuthenticationProcess.AUTHENTICATION_DENIED)){
                userRepository.delete(findUser);
            }
            else{throw new IllegalStateException("해당 이메일을 가진 유저가 존재합니다");}
        }



        String code = FourDigitsNumberGenerator.generateFourDigitsNumber();
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(3L);

        // 비밀번호 암호화
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        doctor = DoctorMail.dtoToMail(dto,code,expirationDateTime);

        if(findUserMail != null){
            userMailRepository.delete(findUserMail);
        }

        userMailRepository.save(doctor);
        emailService.sendEmail(dto.getEmail(),code);

        return doctor;
    }


    // 환자 계정 생성
    @Transactional
    public PatientMail createPatientUser(PatientCreateRequest dto) throws MessagingException {
        User findUser = userRepository.findByEmail(dto.getEmail()).orElse(null);
        UserMail findUserMail = userMailRepository.findByEmail(dto.getEmail()).orElse(null);
        PatientMail patient;


        if (findUser != null){
            throw new IllegalStateException("해당 이메일을 가진 유저가 존재합니다");
        }

        String code = FourDigitsNumberGenerator.generateFourDigitsNumber();
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(3L);

        // 비밀번호 암호화
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        patient = PatientMail.dtoToMail(dto,code,expirationDateTime);

        if(findUserMail != null){
            userMailRepository.delete(findUserMail);
        }

        userMailRepository.save(patient);
        emailService.sendEmail(dto.getEmail(),code);

        return patient;
    }

    //환자 인증
    @Transactional
    public void validatePatient(CodeRequest dto,LocalDateTime time){
        PatientMail patientMail = (PatientMail) userMailRepository.findByEmail(dto.getEmail())
                .stream().filter(e->e instanceof PatientMail)
                .findFirst()
                .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        // 정상 인증 프로세스
        if(patientMail.getCode().equals(dto.getCode())
                && patientMail.getExpirationDateTime().isAfter(time)){
            userMailRepository.delete(patientMail);
            Patient patient = Patient.patientMailToPatient(patientMail);
            userRepository.save(patient);
        // 인증 시간 초과
        }else if(patientMail.getExpirationDateTime().isBefore(time)){
            throw new TimeOutException("인증 시간을 초과하였습니다");
        // 인증 번호 틀림
        }else{
            throw new AuthenticationServiceException("잘못된 인증번호 입니다");
        }
    }

    // 의사 인증
    @Transactional
    public void validateDoctor(CodeRequest dto,LocalDateTime time){
        DoctorMail doctorMail = (DoctorMail) userMailRepository.findByEmail(dto.getEmail())
                .stream().filter(e->e instanceof DoctorMail)
                .findFirst()
                .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        // 정상 인증 프로세스
        if(doctorMail.getCode().equals(dto.getCode())
                && doctorMail.getExpirationDateTime().isAfter(time)){
            userMailRepository.delete(doctorMail);
            userRepository.save(Doctor.doctorMailToDoctor(doctorMail));
            // 인증 시간 초과
        }else if(doctorMail.getExpirationDateTime().isBefore(time)){
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
        findUser.setToken(null);
    }

    // 토큰 설정
    @Transactional
    public void setToken(User user, Token token){
        User findUser = userRepository.findById(user.getId()).orElse(null);
        findUser.setToken(token);
        token.setUser(user);
    }

    // 이메일 중복 확인 로직
    public boolean isEmailDuplicated(String email){
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null;
    }

    // 의사 인증 상태를 ONGOING으로 설정
    @Transactional
    public void setDoctorAuthenticationSuccess(Long id){
        Doctor doctor = userRepository.findDoctorById(id).orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        doctor.changeDoctorState(AuthenticationProcess.AUTHENTICATION_SUCCESS);
    }

    // 의사 인증 상태를 DENINED로 설정
    @Transactional
    public void setDoctorAuthenticationDenied(Long id){
        Doctor doctor = userRepository.findDoctorById(id).orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));
        doctor.changeDoctorState(AuthenticationProcess.AUTHENTICATION_DENIED);
    }

}
