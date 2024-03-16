package webdoc.authentication.domain.entity.user.tutee;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import webdoc.authentication.domain.entity.user.UserMail;
import webdoc.authentication.domain.entity.user.tutee.request.TuteeCreateRequest;

import java.time.LocalDateTime;
/*
* 튜티 회원가입 도메인 객체
 */
@Entity
@Getter
@DiscriminatorValue("tutee")
public class TuteeMail extends UserMail {
    public TuteeMail() {

    }
    @Builder
    private TuteeMail(String email, String password, String name,
                      String contact, String role,
                      String code, LocalDateTime expirationDateTime){
        super(name,email,password,contact,role,code,expirationDateTime);
    }

    public static TuteeMail dtoToMail(TuteeCreateRequest dto, String code, LocalDateTime expirationDateTime){
        return TuteeMail
                    .builder()
                    .name(dto.getName())
                    .password(dto.getPassword())
                    .role(Tutee.role)
                    .contact(dto.getContact())
                    .email(dto.getEmail())
                    .expirationDateTime(expirationDateTime)
                    .code(code)
                    .build();
    }




}
