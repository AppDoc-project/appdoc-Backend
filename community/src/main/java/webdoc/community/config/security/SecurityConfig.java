package webdoc.community.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import webdoc.community.config.security.filter.JwtAuthenticationFilter;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.repository.UserRepository;


import java.io.IOException;

@Configuration
public class SecurityConfig {
    @Autowired
    UserRepository userRepository;



    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${jwt.signing.key}")
    private String key;

    @Bean AuthenticationManager authenticationManager(HttpSecurity http){
        return http.getSharedObject(AuthenticationManager.class);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(key,objectMapper,userRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterAt(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth->{
                    auth
                            .anyRequest()
                            .authenticated();
                })
                .csrf(csrf->{
                    csrf.disable();
                })
                .exceptionHandling(ex->{
                    ex.accessDeniedHandler(new AccessDeniedHandler() {
                        @Override
                        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                            response.setCharacterEncoding("UTF-8");
                            response.setStatus(403);
                            response.getWriter().write(objectMapper.writeValueAsString(new CodeMessageResponse("권한이 없습니다",403,null)));
                        }
                    });
                   ex.authenticationEntryPoint(new AuthenticationEntryPoint() {
                       @Override
                       public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                           response.setCharacterEncoding("UTF-8");
                           response.setStatus(401);
                           response.getWriter().write(objectMapper.writeValueAsString(new CodeMessageResponse("인증이 필요합니다",400,null)));

                       }
                   });
                })
                .sessionManagement(session->{
                    //세션이 만들어 지지 않는다 JWT로 동작
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });

        return http.build();
    }
}
