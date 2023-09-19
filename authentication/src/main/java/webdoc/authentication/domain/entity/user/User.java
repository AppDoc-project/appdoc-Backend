package webdoc.authentication.domain.entity.user;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(of = "id")
@DiscriminatorColumn(name="dtype")
public abstract class User {

    protected User(){}

    protected User(String email,String password,String contact){
        this.email = email;
        this.password = password;
        this.contact = contact;
    }
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private boolean active = false;

    @Column(nullable = false)
    private boolean denied = false;
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



}
