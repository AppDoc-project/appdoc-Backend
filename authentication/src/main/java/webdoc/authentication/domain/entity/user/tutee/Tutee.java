package webdoc.authentication.domain.entity.user.tutee;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import webdoc.authentication.domain.entity.user.User;

import java.time.LocalDate;
import java.util.Objects;
/*
* 튜티 도메인 객체
 */
@Entity
@Getter
@DiscriminatorValue("tutee")
public class Tutee extends User {
    @Column(unique = true)
    private String nickName;
    public static final String role = "ROLE_TUTEE";

    public void setNickName(String nickName){
        this.nickName = nickName;
    }
    protected Tutee(){}
    public static Tutee createTutee(String email, String password, String name,
                                      String contact){
        return
                Tutee.builder()
                        .email(email)
                        .password(password)
                        .name(name)
                        .contact(contact)
                        .role(role)
                        .build();
    }

    public static Tutee tuteeMailToTutee(TuteeMail tuteeMail){
            return
                    Tutee.createTutee(
                            tuteeMail.getEmail(),
                            tuteeMail.getPassword(),
                            tuteeMail.getName(),
                            tuteeMail.getContact()
                    );
    }

    public String getNickName(){
        return Objects.requireNonNullElseGet(nickName, () -> "익명" + getId());
    }
    @Builder
    private Tutee(String email, String password, String name,
                  String contact, String role){
        super(name,email,password,contact,role);
    }


}
