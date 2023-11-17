package webdoc.authentication.domain.entity.user.tutor;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import webdoc.authentication.domain.entity.user.UserMail;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.domain.entity.user.tutor.request.TutorCreateRequest;

import java.time.LocalDateTime;

@Entity
@Getter
@DiscriminatorValue("tutor")
public class TutorMail extends UserMail {
    public TutorMail() {

    }
    @Column(nullable = false)
    private String authenticationAddress;
    @Column(nullable = false)
    private String specialities;
    private String selfDescription;
    @Builder
    private TutorMail(
            String name, String email, String password,
            String contact, String authenticationAddress,
            String specialities, String selfDescription, String role,
            String code, LocalDateTime expirationDateTime
            ){
        super(name,email,password,contact,role,code,expirationDateTime);
        this.authenticationAddress = authenticationAddress;
        this.specialities = specialities;
        this.selfDescription = selfDescription;
    }



    // DTO - ENTITY 변환함수
    public static TutorMail dtoToMail(TutorCreateRequest dto, String code, LocalDateTime expirationDateTime){
        return
                TutorMail
                        .builder()
                        .name(dto.getName())
                        .password(dto.getPassword())
                        .selfDescription(dto.getSelfDescription())
                        .specialities(Specialities.enumToString(dto.getSpecialities()))
                        .email(dto.getEmail())
                        .authenticationAddress(dto.getAuthenticationAddress())
                        .contact(dto.getContact())
                        .role(Tutor.role)
                        .expirationDateTime(expirationDateTime)
                        .code(code)
                        .build();
    }


}
