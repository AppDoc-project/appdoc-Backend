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


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String signingKey;
    private final ObjectMapper mapper;
    private final String authServer;

    private final UserService userService;

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
            filterChain.doFilter(request,response);
            return ;
        }



        String email = claims.get("email",String.class);
        String expiredAt = claims.get("expireAt",String.class);
        LocalDateTime tokenExpiredAt = LocalDateTime.parse(expiredAt);


        UserResponse user;
        try{
            user = userService.fetchUserResponseFromAuthServer(authServer+"/server/user/my",jwt,10_000,10_000).get();
        }catch(Exception e){

            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write(mapper.writeValueAsString(new CodeMessageResponse(CommonMessageProvider.INTERNAL_SERVER_ERROR,500,ResponseCodeProvider.INTERNAL_SERVER_ERROR)));
            return;
        }


        if(user == null || tokenExpiredAt.isBefore(LocalDateTime.now())){
            filterChain.doFilter(request,response);
            return;
        }

        Authentication authentication = new JwtAuthenticationToken(user);
        context.setAuthentication(authentication);

        filterChain.doFilter(request,response);


    }


}
