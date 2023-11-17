package webdoc.community.domain.entity.user;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import webdoc.community.domain.BaseEntity;
import webdoc.community.domain.entity.user.tutee.Tutee;
import webdoc.community.domain.entity.user.tutor.Tutor;


import java.time.LocalDate;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(of = "id")
@DiscriminatorColumn(name="dtype")
@BatchSize(size = 1000)
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

    public String getNickName(){
        if (this instanceof Tutor) return getName();
        return ((Tutee) this).getNickName();
    }

    public boolean isTutor(){
        return this instanceof Tutor;
    }



    // token setter
    public void setToken(Token token){
        this.token = token;
    }

    // role setter
    public void setRole(String role){
        this.role = role;
    }


    @Id
    @GeneratedValue
    private Long id;


    // 회원 인증 과정 중 인증이 거부되었는 지 여부 --> true(인증과정 중 거부) false(거부되지 않음)

    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "user",orphanRemoval = true,cascade = CascadeType.ALL)
    private Token token;
    @Column(nullable = false)
    private String contact;
    @Column(nullable = false)
    private String role;
    private String profile;



}
