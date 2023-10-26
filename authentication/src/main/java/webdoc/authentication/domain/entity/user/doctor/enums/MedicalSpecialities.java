package webdoc.authentication.domain.entity.user.doctor.enums;

public enum MedicalSpecialities {
    INTERNAL_MEDICINE("내과"), SURGERY("외과"), ORTHOPEDICS("정형외과"),
    OPHTHALMOLOGY("정형외과"),OTORHINOLARYNGOLOGY("이비인후과"),
    DERMATOLOGY("피부과"), DENTISTRY("치과"), UROLOGY("비뇨기과"),
    PLASTICSURGERY("성형외과");


    private final String name;

    MedicalSpecialities(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}