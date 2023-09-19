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
import webdoc.authentication.domain.dto.response.CodeMessageResponse;
import webdoc.authentication.domain.dto.response.SubCodeMessageResponse;
import webdoc.authentication.domain.entity.user.Doctor;
import webdoc.authentication.domain.entity.user.Token;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.repository.UserRepository;
import webdoc.authentication.service.AuthService;
import webdoc.authentication.utility.messageprovider.AuthMessageProvider;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
public class InitialAuthenticationFilter extends OncePerRequestFilter {
    private final AuthService service;
    private final String signingKey;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
        response.setCharacterEncoding("UTF-8");
        String email = request.getHeader("email");
        String password = request.getHeader("password");

        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null){
            response.setStatus(400);
            response.getWriter().write(objectMapper.writeValueAsString(new SubCodeMessageResponse("인증에 실패하였습니다",400,400)));
            return ;
        } else if (!passwordEncoder.matches(password,user.getPassword())) {
            response.setStatus(400);
            response.getWriter().write(objectMapper.writeValueAsString(new SubCodeMessageResponse("인증에 실패하였습니다",400,400)));
            return ;
        } else if(user instanceof Doctor && user.isDenied()){
            response.setStatus(400);
            response.getWriter().write(objectMapper.writeValueAsString(new SubCodeMessageResponse("인증이 거부 되었습니다 적절한 인증수단을 갖고 다시 회원가입 해주세요",401,400)));
            return ;
        } else if(user instanceof Doctor && !user.isActive()){
            response.setStatus(400);
            response.getWriter().write(objectMapper.writeValueAsString(new SubCodeMessageResponse("인증절차가 진행 중입니다",402,400)));
            return ;
        }

        String type;
        if (user instanceof Doctor){
            type = "doctor";
        }else{
            type = "patient";
        }

        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8)
        );
        LocalDateTime now = LocalDateTime.now();

        String jwt = Jwts.builder()
                .setClaims(Map.of("email",email,"expireAt",now.plusHours(24).toString(),"type",type))
                .signWith(key)
                .compact();

        Token token = Token.createToken(now.plusHours(24),jwt,null);
        service.setToken(user,token);



        response.setHeader("Authorization",jwt);
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(new CodeMessageResponse(AuthMessageProvider.LOGIN_SUCCESS,200)));

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        return !request.getServletPath().equals("/auth/login");
    }


}
