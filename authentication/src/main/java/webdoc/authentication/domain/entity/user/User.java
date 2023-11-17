package webdoc.authentication.domain.entity.user;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import webdoc.authentication.domain.BaseEntity;


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
    @Column
    private String profile;





}
