package webdoc.community.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import webdoc.community.domain.entity.user.UserResponse;

import java.util.Optional;

@Component
public class UserService {

    public Optional<UserResponse> fetchUserResponseFromAuthServer(String url, String jwt, Integer connectTimeOut, Integer readTimeOut) {

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

    private ClientHttpRequestFactory clientHttpRequestFactory(int connectTimeOut, int readTimeOut) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeOut);  // 연결 타임아웃 10초
        factory.setReadTimeout(readTimeOut);     // 읽기 타임아웃 10초
        return factory;
    }
}
