package webdoc.authentication.domain.entity.user;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import webdoc.authentication.domain.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(of = "id")
@DiscriminatorColumn(name="dtype")
public abstract class UserMail extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String contact;
    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    protected String code;
    @Column(nullable = false)
    protected LocalDateTime expirationDateTime;

    protected UserMail(){}
    protected UserMail(String name, String email,
                       String password, String contact,
                       String role,
                       String code, LocalDateTime expirationDateTime){
        this.name = name;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.role = role;
        this.code = code;
        this.expirationDateTime = expirationDateTime;
    }


}
