package webdoc.authentication.domain.entity.user.patient;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import webdoc.authentication.domain.entity.user.User;

import java.time.LocalDate;

@Entity
@Getter
@DiscriminatorValue("patient")
public class Patient extends User {
    public static final String role = "ROLE_PATIENT";
    protected Patient(){}
    public static Patient createPatient(String email,String password,String name,
                                        String contact, LocalDate dateOfBirth){
        return
                Patient.builder()
                        .contact(email)
                        .password(password)
                        .name(name)
                        .contact(contact)
                        .dateOfBirth(dateOfBirth)
                        .role(role)
                        .build();
    }

    public static Patient patientMailToPatient(PatientMail patientMail){
            return
                    Patient.createPatient(
                            patientMail.getEmail(),
                            patientMail.getPassword(),
                            patientMail.getName(),
                            patientMail.getContact(),
                            patientMail.getDateOfBirth()
                    );
    }
    @Builder
    private Patient(String email, String password, String name,
                    String contact, LocalDate dateOfBirth,String role){
        super(name,email,password,contact,role,dateOfBirth);
    }


}
