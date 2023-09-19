package webdoc.authentication.domain.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@DiscriminatorValue("patient")
public class Patient extends User {
    protected Patient(){}
    public static Patient createPatient(String email,String password,String name,String contact){
        Patient patient = new Patient();

        patient.setEmail(email);
        patient.setPassword(password);
        patient.setContact(contact);
        patient.setName(name);

        return patient;
    }
    @Column(nullable = true)
    private String authenticationCode;

    @Column(nullable = true)
    private LocalDateTime authenticationDue;

}
