package webdoc.authentication.securityConfig;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import webdoc.authentication.config.security.token.JwtAuthenticationToken;
import webdoc.authentication.domain.entity.user.response.UserResponse;
import webdoc.authentication.domain.entity.user.tutor.Tutor;


public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new JwtAuthenticationToken(
                Tutor.createTutor(
                        "1dilumn0@gmail.com",
                        "dntrjdn78",
                        "우석우",
                        "01025045779",
                        "",
                        "gkdkgkdk"
                )
        );
        context.setAuthentication(auth);
        return context;
    }
}
