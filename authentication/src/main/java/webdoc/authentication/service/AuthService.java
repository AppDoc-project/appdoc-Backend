package webdoc.authentication.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.dto.user.DoctorDto;
import webdoc.authentication.domain.dto.user.PatientCodeDto;
import webdoc.authentication.domain.dto.user.PatientDto;
import webdoc.authentication.domain.entity.user.Doctor;
import webdoc.authentication.domain.entity.user.Patient;
import webdoc.authentication.domain.entity.user.Token;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.exceptions.TimeOutException;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.utility.generator.FourDigitsNumberGenerator;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    //의사 계정 생성
    @Transactional
    public Doctor createDoctorUser(DoctorDto dto){
        User findUser = repository.findByEmail(dto.getEmail()).orElse(null);
        Doctor doctor;

        // 만약 null 이 아니고 isDenied가 false면 예외 발생
        if (findUser != null && !findUser.isDenied()){
            throw new IllegalStateException("해당 이메일을 가진 유저가 존재합니다");
        }

        if (findUser instanceof Doctor){
            doctor = (Doctor) findUser;
            doctor.setDoctor(dto);
        }else{
            doctor =  Doctor.createDoctor(
                    dto.getEmail(),passwordEncoder.encode(dto.getPassword()),dto.getName(),dto.getContact(),
                    dto.getAddress(),dto.getCertificateAddress(),dto.getMedicalSpeciality(),
                    dto.getSelfDescription()
            );
            doctor.setRole("ROLE_DOCTOR");
            repository.save(doctor);
        }

        return doctor;
    }


    // 환자 계정 생성
    @Transactional
    public Patient createPatientUser(PatientDto dto) throws MessagingException {
        User findUser = repository.findByEmail(dto.getEmail()).orElse(null);

        if(findUser != null && findUser.isActive()){
            throw new IllegalStateException("해당 이메일을 가진 유저가 존재합니다");
        }else if(findUser != null && !findUser.isActive() && findUser instanceof Patient){
            Patient findPatient = (Patient) findUser;
            String generatedValue = FourDigitsNumberGenerator.generateFourDigitsNumber();
            findPatient.setAuthenticationCode(generatedValue);
            findPatient.setAuthenticationDue(LocalDateTime.now().plusMinutes(3L));
            emailService.sendEmail(findPatient.getEmail(),generatedValue);
            return findPatient;
        }

        Patient patient =
                Patient.createPatient(dto.getEmail(), passwordEncoder.encode(dto.getPassword()),dto.getName(),dto.getContact());
        patient.setRole("ROLE_PATIENT");
        String generatedValue = FourDigitsNumberGenerator.generateFourDigitsNumber();
        patient.setAuthenticationCode(generatedValue);
        patient.setAuthenticationDue(LocalDateTime.now().plusMinutes(3L));
        emailService.sendEmail(patient.getEmail(), generatedValue);
        repository.save(patient);
        return patient;
    }

    //환자 인증
    @Transactional
    public void validatePatient(PatientCodeDto dto){

        Patient findUser = (Patient) repository.findByEmail(dto.getEmail())
                .stream().filter(e->e instanceof Patient)
                .findFirst()
                .orElseThrow(()->new NoSuchElementException("id에 해당하는 회원이 없습니다"));

        if(findUser.isActive()){
            throw new NoSuchElementException("이미 인증된 회원입니다");
        }

        // 정상 인증 프로세스
        if(findUser.getAuthenticationCode().equals(dto.getCode())
                && findUser.getAuthenticationDue().isAfter(LocalDateTime.now())){
            findUser.setActive(true);
        }else if(findUser.getAuthenticationDue().isBefore(LocalDateTime.now())){
            throw new TimeOutException("인증 시간을 초과하였습니다");
        }else{
            throw new AuthenticationServiceException("잘못된 인증번호 입니다");
        }



    }

    @Transactional
    public void logOut(User user){
        User findUser = repository.findByEmail(user.getEmail()).orElseThrow(()->new RuntimeException("서버에러가 발생하였습니다"));
        findUser.setToken(null);
    }

    @Transactional
    public void setToken(User user, Token token){
        User findUser = repository.findById(user.getId()).orElse(null);
        findUser.setToken(token);
        token.setUser(user);
        return;
    }

}
