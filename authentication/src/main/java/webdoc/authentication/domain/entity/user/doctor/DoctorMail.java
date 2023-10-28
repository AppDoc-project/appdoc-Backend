package webdoc.authentication.domain.entity.user.doctor;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import webdoc.authentication.domain.entity.user.UserMail;
import webdoc.authentication.domain.entity.user.doctor.request.DoctorCreateRequest;
import webdoc.authentication.domain.entity.user.doctor.enums.MedicalSpecialities;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@DiscriminatorValue("docter")
public class DoctorMail extends UserMail {
    public DoctorMail() {

    }
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String certificateAddress;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MedicalSpecialities medicalSpeciality;


    @Column(nullable = false)
    private String hospitalName;
    private String selfDescription;
    @Builder
    private DoctorMail(
            String name, String email, String password,
            String contact, String address, String certificateAddress,
            MedicalSpecialities medicalSpeciality, String selfDescription, String role,
            LocalDate dateOfBirth, String code, LocalDateTime expirationDateTime,
            String hospitalName){
        super(name,email,password,contact,role,dateOfBirth,code,expirationDateTime);
        this.address = address;
        this.hospitalName = hospitalName;
        this.certificateAddress = certificateAddress;
        this.medicalSpeciality = medicalSpeciality;
        this.selfDescription = selfDescription;
    }



    // DTO - ENTITY 변환함수
    public static DoctorMail dtoToMail(DoctorCreateRequest dto, String code, LocalDateTime expirationDateTime){
        return
                DoctorMail
                        .builder()
                        .name(dto.getName())
                        .password(dto.getPassword())
                        .selfDescription(dto.getSelfDescription())
                        .medicalSpeciality(dto.getMedicalSpeciality())
                        .email(dto.getEmail())
                        .hospitalName(dto.getHospitalName())
                        .certificateAddress(dto.getCertificateAddress())
                        .dateOfBirth(dto.getDateOfBirth())
                        .address(dto.getAddress())
                        .contact(dto.getContact())
                        .role(Doctor.role)
                        .expirationDateTime(expirationDateTime)
                        .code(code)
                        .build();
    }


}
