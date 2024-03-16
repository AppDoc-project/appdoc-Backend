package webdoc.authentication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j

/*
 * 예약 서비스 : 예약 서버에 요청을 보내서 레슨정보를 확인
 */

public class ReservationService {
    @Value("${community.server}")
    private String reservationServer;

    // 특정 유저에게 남은 예약이 있는 지 확인하는 로직
    public boolean hasLeftReservation(String jwt,Long userId,int connectTimeOut, int readTimeOut){

        String result;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);

        HttpEntity<?> httpEntity = new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory(connectTimeOut,readTimeOut));

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                reservationServer + "/tutor/reservation/server",
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        log.info("통신 결과 상태 코드 :{}",responseEntity.getStatusCode());

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            result = responseEntity.getBody();
            return result.equals("YES");
        } else {
            throw new RuntimeException("서버내부 에러");
        }
    }

    private ClientHttpRequestFactory clientHttpRequestFactory(int connectTimeOut, int readTimeOut) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeOut);  // 연결 타임아웃 10초
        factory.setReadTimeout(readTimeOut);     // 읽기 타임아웃 10초
        return factory;
    }
}
