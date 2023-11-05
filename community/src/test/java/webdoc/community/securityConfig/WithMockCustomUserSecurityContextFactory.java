package webdoc.community.securityConfig;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import webdoc.community.config.security.token.JwtAuthenticationToken;
import webdoc.community.domain.entity.user.patient.Patient;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new JwtAuthenticationToken(
                Patient.createPatient(null,null,null,null,null)
        );
        context.setAuthentication(auth);
        return context;
    }
}
