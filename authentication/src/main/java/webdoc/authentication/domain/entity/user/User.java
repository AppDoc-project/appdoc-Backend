package webdoc.authentication.domain.entity.user;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import webdoc.authentication.domain.BaseEntity;
/*
* 튜터, 튜티 상위 회원 도메인 객체
 */

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(of = "id")
@DiscriminatorColumn(name="dtype")
public abstract class User extends BaseEntity {
    protected User(){}

    protected User(String name, String email,
                   String password,String contact,
                   String role){
        this.name = name;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.role = role;
    }

    // role setter
    public void setRole(String role){
        this.role = role;
    }

    public void setPassword(String password){this.password = password;}

    public void setContact(String contact){this.contact = contact;}

    public void setProfile(String profile){this.profile = profile;}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column
    private String profile;

}
