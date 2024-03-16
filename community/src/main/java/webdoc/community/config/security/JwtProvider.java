package webdoc.community.config.security;

import org.springframework.stereotype.Component;

/*
* JWT토큰을 Thread-Local을 통해 관리
*/
@Component
public class JwtProvider {
    private ThreadLocal<String> jwtRepository = new ThreadLocal<>();

    public String getJwt(){
        return jwtRepository.get();
    }

    public void setJwt(String jwt){
        jwtRepository.set(jwt);
    }

    public void expireJwt(){
        jwtRepository.remove();
    }
}
