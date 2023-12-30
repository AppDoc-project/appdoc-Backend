package webdoc.authentication.domain.entity.user;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import webdoc.authentication.domain.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
        if (tokens.size() >= 1){
            tokens.remove(0);
        }
        tokens.add(token);
        token.setUser(this);
    }

    // role setter
    public void setRole(String role){
        this.role = role;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Token> tokens = new ArrayList<>();
    @Column(nullable = false)
    private String contact;
    @Column(nullable = false)
    private String role;
    @Column
    private String profile;

}
