package webdoc.community.config.security.token;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import webdoc.community.domain.entity.user.response.UserResponse;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationToken implements Authentication {
    private final UserResponse userResponse;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(userResponse::getRole);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userResponse;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return userResponse.getNickName();
    }
}
