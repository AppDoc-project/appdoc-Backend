package webdoc.community.domain.entity.user.tutor;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import webdoc.community.domain.entity.TutorSpeciality;
import webdoc.community.domain.entity.user.User;
import webdoc.community.domain.entity.user.tutor.enums.AuthenticationProcess;

import java.util.List;

@Entity
@Getter
@DiscriminatorValue("tutor")
public class Tutor extends User {
    public static final String role = "ROLE_TUTOR";
    @Column(nullable = false)
    private String authenticationAddress;
    @OneToMany(mappedBy = "tutor",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<TutorSpeciality> specialities;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthenticationProcess authenticationProcess;
    private String selfDescription;
    protected Tutor(){

    }
    public void changeTutorState(AuthenticationProcess authenticationProcess){
        this.authenticationProcess = authenticationProcess;
    }

    public void setTutorSpecialities(List<TutorSpeciality> specialities){
        this.specialities = specialities;
    }

    // Tutor객체를 만드는 팩토리 함수
    public static Tutor createTutor(String email,String password,String name,String contact,
                                    String authenticationAddress,
                                    String selfDescription){
        return
                Tutor
                        .builder()
                        .name(name)
                        .email(email)
                        .password(password)
                        .authenticationAddress(authenticationAddress)
                        .contact(contact)
                        .selfDescription(selfDescription)
                        .authenticationProcess(AuthenticationProcess.AUTHENTICATION_ONGOING)
                        .role(role)
                        .build();
    }



    // builder를 포함한 tutor 생성자
    @Builder
    private Tutor(
            String name, String email, String password,
            String contact, String authenticationAddress,
            List<TutorSpeciality> specialities, String selfDescription, String role
            ,AuthenticationProcess authenticationProcess){
        super(name,email,password,contact,role);
        this.authenticationProcess = authenticationProcess;
        this.authenticationAddress = authenticationAddress;
        this.specialities = specialities;
        this.selfDescription = selfDescription;
    }



}
