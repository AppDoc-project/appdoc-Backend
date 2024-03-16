package webdoc.authentication.domain.entity.user.tutee.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
/*
* 튜티 계정 생성 객체
 */
@Getter
@Setter
public class TuteeCreateRequest {

    @Email
    @Size(max=50)
    private String email;

    @NotEmpty
    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[가-힣]*$")
    private String name;

    @NotEmpty
    @Size(min = 8, max = 18)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@~_])[a-zA-Z\\d!@~_]+$")
    private String password;

    @NotEmpty
    @Pattern(regexp = "^[0-9]{11}$")
    private String contact;

    @Builder
    private TuteeCreateRequest(String email, String name,
                               String password, String contact){
        this.email = email;
        this.name = name;
        this.password = password;
        this.contact = contact;
    }


}
