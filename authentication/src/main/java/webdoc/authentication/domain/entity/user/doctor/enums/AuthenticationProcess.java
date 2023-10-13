package webdoc.authentication.domain.entity.user.doctor.enums;

public enum AuthenticationProcess {
    AUTHENTICATION_DENIED("인증 거부"),
    AUTHENTICATION_SUCCESS("인증 성공"),
    AUTHENTICATION_ONGOING("인증 진행중");

    private final String name;

    AuthenticationProcess(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
