package webdoc.authentication.domain.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import webdoc.authentication.domain.dto.user.DoctorDto;
import webdoc.authentication.domain.enums.MedicalSpecialities;

@Entity
@Getter
@Setter
@DiscriminatorValue("docter")
public class Doctor extends User{
    protected Doctor(){

    }
    public static Doctor createDoctor(String email,String password,String name,String contact ,String address,
                               String certificateAddress, MedicalSpecialities medicalSpeciality,String selfDescription){
        Doctor doctor = new Doctor();

        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setPassword(password);
        doctor.setContact(contact);
        doctor.setAddress(address);
        doctor.setCertificateAddress(certificateAddress);
        doctor.setMedicalSpeciality(medicalSpeciality);
        doctor.setSelfDescription(selfDescription);

        return doctor;

    }

    public void setDoctor(DoctorDto dto){
        this.setName(dto.getName());
        this.setPassword(dto.getPassword());
        this.setContact(dto.getContact());
        this.setCertificateAddress(dto.getCertificateAddress());
        this.setAddress(dto.getAddress());
        this.setDenied(false);
        this.setMedicalSpeciality(dto.getMedicalSpeciality());
        this.setSelfDescription(dto.getSelfDescription());
    }

    @Column(nullable = true)
    private String address;

    @Column(nullable = false)
    private String certificateAddress;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MedicalSpecialities medicalSpeciality;

    @Column(nullable = true)
    private String selfDescription;

}
