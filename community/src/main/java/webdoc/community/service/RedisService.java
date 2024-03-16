package webdoc.community.service;



import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/*
 * Redis 서비스
 */

@Component
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    // key로 데이터를 삽입
    public void setValues(String key, String data) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    // 유효기간이 있는 데이터를 삽입
    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }


    // key로 데이터 가져오기
    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return "false";
        }
        return (String) values.get(key);
    }

    // key로 value를 삭제
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    // 특정 key에 대해 timeOut 설정
    @Transactional
    public void expireValues(String key, int timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }


    // 특정 value가 존재하는 지 확인
    public boolean checkExistsValue(String value) {
        return !value.equals("false");
    }


    // sorted set을 추가
    @Transactional
    public void addToSortedSet(String key, String value, double score) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        // key의 Sorted Set에 value를 추가하고, 해당 value의 score를 지정
        zSetOperations.add(key, value, score);
    }

    // 특정 등수 범위에 대해 value 획득
    public Set<Object> getSortedSetRange(String key, long start, long end) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        // 지정된 key의 Sorted Set에서 내림차순으로 특정 범위의 값들을 가져옴
        return zSetOperations.reverseRange(key, start, end);
    }

    // 특정 값 범위에 대해 value 획득
    public Set<Object> getSortedSetByScoreRange(String key, double minScore, double maxScore) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        // 지정된 key의 Sorted Set에서 스코어 범위에 해당하는 값들을 가져옴
        return zSetOperations.rangeByScore(key, minScore, maxScore);
    }

    // 특정 key에서 value를 삭제
    public void removeFromSortedSet(String key, String value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        // 지정된 key의 Sorted Set에서 특정 값을 제거
        zSetOperations.remove(key, value);
    }

    // 특정 value를가진 score 반환
    public Double getScoreFromSortedSet(String key, String value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Double score = zSetOperations.score(key, value);
        return score;
    }

}

