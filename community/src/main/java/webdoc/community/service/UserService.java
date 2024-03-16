package webdoc.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import webdoc.community.config.security.JwtProvider;
import webdoc.community.domain.entity.tutor.response.TutorResponse;
import webdoc.community.domain.entity.user.response.UserResponse;

import java.util.List;
import java.util.Optional;
/*
 * 유저 서비스 : (인증 서버와 통신하여 유저 정보를 가져온다)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final JwtProvider jwtProvider;

    // 특정인을 정보를 AuthServer로 부터 받아 오는 함수
    public Optional<UserResponse> fetchUserResponseFromAuthServer(String url , Integer connectTimeOut, Integer readTimeOut) {

        String jwt = jwtProvider.getJwt();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);

        HttpEntity<?> httpEntity = new HttpEntity<>(null, headers);

        // 타임아웃 설정
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory(connectTimeOut,readTimeOut));

        ResponseEntity<UserResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                UserResponse.class
        );



        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return Optional.of(responseEntity.getBody());
        } else {
            // 에러 처리 로직
            return Optional.of(null);
        }


    }

    // 서버에 저장된 모든 튜터의 정보를 받아오는 함수
    public List<UserResponse> fetchTutors(String url,Integer connectTimeOut, Integer readTimeOut ){
        HttpEntity<?> httpEntity = new HttpEntity<>(null);

        // 타임아웃 설정
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory(connectTimeOut,readTimeOut));

        ParameterizedTypeReference<List<UserResponse>> responseType = new ParameterizedTypeReference<List<UserResponse>>() {};
        ResponseEntity<List<UserResponse>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                responseType
        );



        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            // 에러 처리 로직
            return List.of();
        }
    }

    // 특정 이름을 가진 유저를 받아오는 함수
    public List<UserResponse> fetchUsersByName(String url, String name,Integer connectTimeOut, Integer readTimeOut){
        HttpEntity<?> httpEntity = new HttpEntity<>(null);

        // 타임아웃 설정
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory(connectTimeOut,readTimeOut));

        url = url + name;
        ParameterizedTypeReference<List<UserResponse>> responseType = new ParameterizedTypeReference<List<UserResponse>>() {};
        ResponseEntity<List<UserResponse>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                responseType
        );



        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            // 에러 처리 로직
            return List.of();
        }
    }

    private ClientHttpRequestFactory clientHttpRequestFactory(int connectTimeOut, int readTimeOut) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeOut);  // 연결 타임아웃 10초
        factory.setReadTimeout(readTimeOut);     // 읽기 타임아웃 10초
        return factory;
    }
}
