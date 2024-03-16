package webdoc.authentication.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

import webdoc.authentication.domain.entity.user.response.LoginResponse;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.enums.AuthenticationProcess;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.response.CodeMessageResponse;
import webdoc.authentication.domain.response.ObjectResponse;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.service.RedisService;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import webdoc.authentication.utility.messageprovider.ResponseCodeProvider;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

/*
 *  로그인 필터
 */
@RequiredArgsConstructor
public class InitialAuthenticationFilter extends OncePerRequestFilter {
    private final AuthService service;
    private final String signingKey;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    private final RedisService redisService;



    // 로그인 로직
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
        response.setCharacterEncoding("UTF-8");
        String email = request.getHeader("email");
        String password = request.getHeader("password");
        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null){
            // 유저가 없는 경우
            response.setStatus(400);
            response.getWriter().write(objectMapper.writeValueAsString(new CodeMessageResponse(AuthMessageProvider.LOGIN_FAIL,400, ResponseCodeProvider.LOGIN_FAIL)));
            return ;
        } else if (!passwordEncoder.matches(password,user.getPassword())) {
            //  비밀번호가 틀린 경우
            response.setStatus(400);
            response.getWriter().write(objectMapper.writeValueAsString(new CodeMessageResponse(AuthMessageProvider.LOGIN_FAIL,400, ResponseCodeProvider.LOGIN_FAIL)));
            return ;

        }else if(user instanceof Tutor &&
                ((Tutor) user).getAuthenticationProcess().equals(AuthenticationProcess.AUTHENTICATION_DENIED)){
            // [튜터] 심사가 거부된 경우
            response.setStatus(400);
            response.getWriter().write(objectMapper.writeValueAsString(new CodeMessageResponse(AuthMessageProvider.AUTHENTICATION_DENIED,400,ResponseCodeProvider.AUTHENTICATION_DENIED)));
            return ;
        } else if(user instanceof Tutor &&
                ((Tutor) user).getAuthenticationProcess().equals(AuthenticationProcess.AUTHENTICATION_ONGOING)){
            // [튜터] 인증절차가 진행 중인 경우
            response.setStatus(400);
            response.getWriter().write(objectMapper.writeValueAsString(new CodeMessageResponse(AuthMessageProvider.AUTHENTICATION_ONGOING,400,ResponseCodeProvider.AUTHENTICATION_ONGOING)));
            return ;
        }

        String type;
        boolean isTutor;

        if (user instanceof Tutor){
            type = "tutor";
            isTutor = true;

        }else{
            type = "tutee";
            isTutor = false;

        }

        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8)
        );
        LocalDateTime now = LocalDateTime.now();

        String jwt = Jwts.builder()
                .setClaims(Map.of("email",email,"expireAt",now.plusHours(24).toString(),"type",type))
                .signWith(key)
                .compact();

        // 레디스에 jwt토큰 저장
        redisService.setValues(email,jwt);



        response.setHeader("Authorization",jwt);
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(new ObjectResponse<>(new LoginResponse(user.getId(),user.getEmail(),user.getName(),isTutor),200)));



    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String requestPath = request.getRequestURI();
        return !requestPath.equals("/auth/login");
    }


}
