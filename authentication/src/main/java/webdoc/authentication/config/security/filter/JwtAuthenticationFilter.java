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
import webdoc.authentication.service.RedisService;
import webdoc.authentication.utility.messageprovider.ResponseCodeProvider;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 *  JWT 인증 필터
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String signingKey;
    private final ObjectMapper mapper;

    private final UserRepository repository;

    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        SecurityContext context = SecurityContextHolder.getContext();
        String jwt = request.getHeader("Authorization");
        Claims claims;
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8)
        );

        // JWT가 없으면 계속 진행
        if (!StringUtils.hasText(jwt)){
            filterChain.doFilter(request,response);
            return;
        }

        // JWT 파싱
        try{
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

        }catch (RuntimeException e){
            response.setCharacterEncoding("UTF-8");
            response.setStatus(401);
            response.getWriter().write(mapper.writeValueAsString(new CodeMessageResponse("로그인이 필요합니다",401, ResponseCodeProvider.AUTHENTICATION_NOT_PROVIDED)));
            return ;
        }

        String email = claims.get("email",String.class);
        User user =  repository.findByEmail(email).orElse(null);

        String token = redisService.getValues(email);
        LocalDateTime expireTime = LocalDateTime.parse((CharSequence) claims.get("expireAt"));


        if(user == null || token==null || expireTime.isBefore(LocalDateTime.now()) || !jwt.equals(token)){
            response.setCharacterEncoding("UTF-8");
            response.setStatus(401);
            response.getWriter().write(mapper.writeValueAsString(new CodeMessageResponse("로그인이 필요합니다",401,ResponseCodeProvider.AUTHENTICATION_NOT_PROVIDED)));
            return;
        }

        Authentication authentication = new JwtAuthenticationToken(user);
        context.setAuthentication(authentication);

        filterChain.doFilter(request,response);





    }
}
