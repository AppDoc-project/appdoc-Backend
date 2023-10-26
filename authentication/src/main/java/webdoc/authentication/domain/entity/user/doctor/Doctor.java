package webdoc.authentication.domain.entity.user.doctor;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.doctor.enums.AuthenticationProcess;
import webdoc.authentication.domain.entity.user.doctor.enums.MedicalSpecialities;

import java.time.LocalDate;

@Entity
@Getter
@DiscriminatorValue("docter")
public class Doctor extends User {

    public static final String role = "ROLE_DOCTOR";
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String certificateAddress;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MedicalSpecialities medicalSpeciality;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthenticationProcess authenticationProcess;


    private String selfDescription;

    private String hospitalName;
    protected Doctor(){

    }

    public void changeDoctorState(AuthenticationProcess authenticationProcess){
        this.authenticationProcess = authenticationProcess;
    }

    // Doctor객체를 만드는 팩토리 함수
    public static Doctor createDoctor(String email,String password,String name,String contact ,String address,
                               String certificateAddress, MedicalSpecialities medicalSpeciality,
                               String selfDescription, LocalDate dateOfBirth,String hospitalName){
        return
                Doctor
                    .builder()
                    .name(name)
                    .email(email)
                    .hospitalName(hospitalName)
                    .password(password)
                    .certificateAddress(certificateAddress)
                    .contact(contact)
                    .medicalSpeciality(medicalSpeciality)
                    .selfDescription(selfDescription)
                    .address(address)
                    .dateOfBirth(dateOfBirth)
                    .authenticationProcess(AuthenticationProcess.AUTHENTICATION_ONGOING)
                    .role(role)
                    .build();
    }

    public static Doctor doctorMailToDoctor(DoctorMail doctorMail){
        return
                Doctor.createDoctor(
                        doctorMail.getEmail(),
                        doctorMail.getPassword(),
                        doctorMail.getName(),
                        doctorMail.getContact(),
                        doctorMail.getAddress(),
                        doctorMail.getCertificateAddress(),
                        doctorMail.getMedicalSpeciality(),
                        doctorMail.getSelfDescription(),
                        doctorMail.getDateOfBirth(),
                        doctorMail.getHospitalName()
                );
    }

    @Builder
    private Doctor(
            String name, String email,String password,
            String contact, String address,String certificateAddress,
            MedicalSpecialities medicalSpeciality, String selfDescription, String role,
            LocalDate dateOfBirth, AuthenticationProcess authenticationProcess,String hospitalName){
        super(name,email,password,contact,role,dateOfBirth);
        this.address = address;
        this.authenticationProcess = authenticationProcess;
        this.certificateAddress = certificateAddress;
        this.medicalSpeciality = medicalSpeciality;
        this.selfDescription = selfDescription;
        this.hospitalName = hospitalName;
    }



}
