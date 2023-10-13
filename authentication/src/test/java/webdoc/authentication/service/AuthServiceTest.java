package webdoc.authentication.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.doctor.DoctorMail;
import webdoc.authentication.domain.entity.user.doctor.request.DoctorCreateRequest;
import webdoc.authentication.domain.enums.MedicalSpecialities;
import webdoc.authentication.repository.UserMailRepository;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {
    @Autowired
    AuthService authService;

    @Autowired
    UserMailRepository userMailRepository;

    @MockBean
    EmailService emailService;

    @DisplayName("의사회원가입을 테스트 한다")
    @Test
    void createDoctorUserTest() throws MessagingException {
        //given
        DoctorCreateRequest dto =
               createRequest();

        //when
        authService.createDoctorUser(dto);
        DoctorMail userMail = userMailRepository.findByEmail(dto.getEmail())
                .stream().map(e->(DoctorMail)e).findAny().get();

        //then
        assertThat(userMail).isNotNull();
        assertThat(userMail)
                .extracting(
                        "address", "certificateAddress", "contact", "dateOfBirth", "email",
                        "hospitalName", "medicalSpeciality", "password", "selfDescription", "name"
                )
                .contains(
                        dto.getAddress(), dto.getCertificateAddress(), dto.getContact(),
                        dto.getDateOfBirth(), dto.getEmail(), dto.getHospitalName(),
                        dto.getMedicalSpeciality(), dto.getPassword(), dto.getSelfDescription(),
                        dto.getName()
                );
     }











    private DoctorCreateRequest createRequest(){
        return DoctorCreateRequest
                .builder()
                .address("서울시 마포구 서교동")
                .certificateAddress("http://localhost:8080")
                .contact("01025045779")
                .dateOfBirth(LocalDate.now())
                .email("1dilumn0@gmail.com")
                .hospitalName("서울대학병원")
                .medicalSpeciality(MedicalSpecialities.DENTISTRY)
                .password("dntjrdn78")
                .selfDescription("좋은 의사입니다")
                .name("우석우")
                .build();
    }
}