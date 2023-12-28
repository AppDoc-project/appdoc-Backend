package webdoc.authentication.domain.entity.user.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final boolean isTutor;

}
