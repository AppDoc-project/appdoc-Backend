package webdoc.authentication.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import webdoc.authentication.config.security.token.JwtAuthenticationToken;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.response.CodeMessageResponse;
import webdoc.authentication.repository.UserRepository;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String signingKey;
    private final ObjectMapper mapper;

    private final UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        SecurityContext context = SecurityContextHolder.getContext();
        String jwt = request.getHeader("Authorization");
        Claims claims;
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8)
        );


        if (!StringUtils.hasText(jwt)){
            filterChain.doFilter(request,response);
            return;
        }

        try{
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

        }catch (RuntimeException e){
            e.printStackTrace();
            response.setCharacterEncoding("UTF-8");
            response.setStatus(400);
            response.getWriter().write(mapper.writeValueAsString(new CodeMessageResponse("유효하지 않은 jwt 토큰입니다",400,400)));
            return ;
        }

        String email = claims.get("email",String.class);
        User user =  repository.findByEmail(email).orElse(null);
        String tokenExpiredAt = claims.get("expireAt",String.class);
        if(user == null || user.getToken() == null || user.getToken().getExpiredAt().isBefore(LocalDateTime.now()) || !jwt.equals(user.getToken().getValue())){
            response.setCharacterEncoding("UTF-8");
            response.setStatus(400);
            response.getWriter().write(mapper.writeValueAsString(new CodeMessageResponse("유효하지 않은 jwt 토큰입니다",400,400)));
            return;
        }

        Authentication authentication = new JwtAuthenticationToken(user);
        context.setAuthentication(authentication);

        filterChain.doFilter(request,response);





    }
}
