package webdoc.authentication.domain.entity.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
/*
* 이메일 정보 전달 객체
 */
@Getter
@Setter
public class EmailRequest {
    @Email
    @Size(max=50)
    private  String email;
}
