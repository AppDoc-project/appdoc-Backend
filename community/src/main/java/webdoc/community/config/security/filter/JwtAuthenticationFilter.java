package webdoc.community.config.security.filter;

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
import webdoc.community.config.security.JwtProvider;
import webdoc.community.config.security.token.JwtAuthenticationToken;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.utility.messageprovider.CommonMessageProvider;
import webdoc.community.utility.messageprovider.ResponseCodeProvider;
import webdoc.community.service.UserService;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/*
*  JWT 인증 필터
*/
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String signingKey;
    private final ObjectMapper mapper;
    private final String authServer;

    private final UserService userService;

    private final JwtProvider jwtProvider;

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
            // 파싱에 실패해도 계속 진행
            filterChain.doFilter(request,response);
            return ;
        }




        String email = claims.get("email",String.class);
        String expiredAt = claims.get("expireAt",String.class);
        LocalDateTime tokenExpiredAt = LocalDateTime.parse(expiredAt);


        UserResponse user;
        try{
            // 인증 서버에 jwt 검증 요청
            // jwtProdiver는 외부 API 요청을 위해 jwt를 담는 컴포넌트
            jwtProvider.setJwt(jwt);
            user = userService.fetchUserResponseFromAuthServer(authServer+"/server/user/my",10_000,10_000).get();

        }catch(Exception e){
            // 유효하지 않은 jwt면 401 응답
            response.setStatus(401);
            response.getWriter().write(mapper.writeValueAsString(new CodeMessageResponse("로그인이 필요 합니다",401,408)));
            return;

        }finally {

            jwtProvider.expireJwt();

        }


        // 해당 유저가 없거나 유효기간이 지난 토큰이면 계속 진행
        if(user == null || tokenExpiredAt.isBefore(LocalDateTime.now())){
            filterChain.doFilter(request,response);
            return;
        }


        try{

            // Authentication 토큰을 securityContext에 기록
            jwtProvider.setJwt(jwt);
            Authentication authentication = new JwtAuthenticationToken(user);
            context.setAuthentication(authentication);
            filterChain.doFilter(request,response);

        }catch(Exception e){

            e.printStackTrace();
            throw e;

        }finally {

            jwtProvider.expireJwt();

        }




    }


}
