package webdoc.authentication.domain.entity.user.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
/*
* 로그인 성공시 유저 정보 전달 객체
 */
@Getter
@Setter
@RequiredArgsConstructor
public class LoginResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final boolean isTutor;

}
