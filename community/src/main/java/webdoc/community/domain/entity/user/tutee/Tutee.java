package webdoc.community.domain.entity.user.tutee;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import webdoc.community.domain.entity.user.User;


import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@DiscriminatorValue("tutee")
public class Tutee extends User {
    public static final String role = "ROLE_TUTEE";

    @Column(unique = true)
    private String nickName;
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

    public String getNickName(){
        return Objects.requireNonNullElseGet(nickName, () -> "익명" + getId());
    }

    @Builder
    private Tutee(String email, String password, String name,
                  String contact, String role){
        super(name,email,password,contact,role);
    }


}
