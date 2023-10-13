package webdoc.authentication.domain.entity.user.patient;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import webdoc.authentication.domain.entity.user.UserMail;
import webdoc.authentication.domain.entity.user.patient.request.PatientCreateRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@DiscriminatorValue("patient")
public class PatientMail extends UserMail {
    public PatientMail() {

    }
    @Builder
    private PatientMail(String email, String password, String name,
                        String contact, LocalDate dateOfBirth, String role,
                        String code, LocalDateTime expirationDateTime){
        super(name,email,password,contact,role,dateOfBirth,code,expirationDateTime);
    }

    public static PatientMail dtoToMail(PatientCreateRequest dto,String code,LocalDateTime expirationDateTime){
        return PatientMail
                    .builder()
                    .name(dto.getName())
                    .password(dto.getPassword())
                    .role(Patient.role)
                    .email(dto.getEmail())
                    .expirationDateTime(expirationDateTime)
                    .code(code)
                    .dateOfBirth(dto.getDateOfBirth())
                    .build();
    }




}
