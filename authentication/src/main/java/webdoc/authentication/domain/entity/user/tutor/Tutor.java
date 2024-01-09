package webdoc.authentication.domain.entity.user.tutor;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import webdoc.authentication.domain.entity.SpecialityChange;
import webdoc.authentication.domain.entity.TutorSpeciality;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.tutor.enums.AuthenticationProcess;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@DiscriminatorValue("tutor")
public class Tutor extends User {
    public static final String role = "ROLE_TUTOR";
    @Column(nullable = false)
    private String authenticationAddress;
    @OneToMany(fetch =  FetchType.LAZY, mappedBy = "tutor",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<TutorSpeciality> specialities = new ArrayList<>();

    @OneToMany(fetch =  FetchType.LAZY, mappedBy = "tutor",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<SpecialityChange> changeRequests = new ArrayList<>();

    @Column(nullable = false,length = 3000)
    @Enumerated(EnumType.STRING)
    private AuthenticationProcess authenticationProcess;
    private String selfDescription;
    protected Tutor(){

    }

    public void setChangeRequests(Set<Specialities> specialities){
        List<SpecialityChange> specialityChanges = SpecialityChange.convertTutorSpeciality(
                new ArrayList<>(specialities), this
        );
        this.changeRequests.addAll(specialityChanges);
    }

    public void setSelfDescription(String selfDescription){
        this.selfDescription = selfDescription;
    }
    public void changeTutorState(AuthenticationProcess authenticationProcess){
        this.authenticationProcess = authenticationProcess;
    }

    public void setAuthenticationAddress(String authenticationAddress){this.authenticationAddress = authenticationAddress;}

    public void setTutorSpecialities(List<TutorSpeciality> specialities){
        this.specialities.addAll(specialities);
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

    public static Tutor tutorMailToTutor(TutorMail tutorMail){
       Tutor tutor =
                Tutor.createTutor(
                        tutorMail.getEmail(),
                        tutorMail.getPassword(),
                        tutorMail.getName(),
                        tutorMail.getContact(),
                        tutorMail.getAuthenticationAddress(),
                        tutorMail.getSelfDescription()
                );

       tutor.setTutorSpecialities(
               TutorSpeciality.convertTutorSpeciality(Specialities.stringToEnum(tutorMail.getSpecialities()),tutor));

       return tutor;
    }


    // builder를 포함한 tutor 생성자
    @Builder
    private Tutor(
            String name, String email, String password,
            String contact, String authenticationAddress
            , String selfDescription, String role
            ,AuthenticationProcess authenticationProcess){
        super(name,email,password,contact,role);
        this.authenticationProcess = authenticationProcess;
        this.authenticationAddress = authenticationAddress;
        this.specialities = specialities;
        this.selfDescription = selfDescription;
    }



}
