package webdoc.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.review.Review;
import webdoc.community.domain.entity.user.Specialities;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.repository.PickRepository;
import webdoc.community.repository.ReviewRepository;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * 통계 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {
    private final RedisService redisService;

    private final ReviewRepository reviewRepository;

    private final PickRepository pickRepository;

    private final List<Specialities>  specialities = List.of(
            Specialities.BASS,Specialities.COMPOSITION,Specialities.DRUM,Specialities.GUITAR,
            Specialities.KEYBOARD_INSTRUMENT,Specialities.MUSIC_THEORY,Specialities.PIANO,
            Specialities.STRING_INSTRUMENT, Specialities.VOCAL,Specialities.WIND_INSTRUMENT
    );

    /*
    * 모든 분류 기준을 기준1 + 악기별로 분류 해야한다
     */

    // 특정 userId를 가진 강사의 평점을 계산해서 레디스에 저장하는 함수
    public Double reviewScoreCount(UserResponse userResponse){
        List<Review> reviews = reviewRepository.findReviewByTutorId(userResponse.getId());
        List<Specialities> specialities = userResponse.getSpecialities();
        double avg = reviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);

        avg = Math.round(avg * 100.0) / 100.0;
        double finalAvg = avg;

        specialities.forEach(e->{
            redisService.addToSortedSet("review"+e,userResponse.getId().toString(), finalAvg);
        });



        return finalAvg;
    }

    // 특정 userId를 가진 강사의 찜수를 계산해서 레디스에 저장하는 함수
    public int pickCount(UserResponse userResponse){
        int picks = pickRepository.countByTutorId(userResponse.getId());
        List<Specialities> specialities = userResponse.getSpecialities();

        specialities.forEach(e->{
            redisService.addToSortedSet("pick"+e,userResponse.getId().toString(),(long) picks);
        });

        return picks;

    }

    // 특정 userId를 가진 강사의 레슨 수를 계산해서 레디스에 저장하는 함수
    public int lessonCount(UserResponse userResponse){
        Random random = new Random();
        List<Specialities> specialities = userResponse.getSpecialities();

        int lessons = random.nextInt(0,200);

        specialities.forEach(e->{
            redisService.addToSortedSet("lesson"+e,userResponse.getId().toString(),(long) lessons);
        });

        return lessons;
    }

    // 특정 userId를 가진 강사의 찜수를 리턴하는 함수
    public Integer getPickCount(Long userId){
        Double score = null;
        for(Specialities speciality: specialities){
            Double value = redisService.getScoreFromSortedSet("pick" + speciality ,userId.toString());
            if (value != null){
                score = value;
                break ;
            }
        }
        if (score == null) return null;
        return score.intValue();

    }

    // 특정 userId를 가진 강사의 평점을 리턴하는 함수
    public Double getReviewScore(Long userId){
        Double score = null;
        for(Specialities speciality: specialities){
            Double value = redisService.getScoreFromSortedSet("review" + speciality ,userId.toString());
            if (value != null){
                score = value;
                break ;
            }
        }
        return score;
    }

    // 특정 userId를 가진 강사의 레슨 수를 리턴하는 함수
    public  Integer getLessonCount(Long userId){
        Double score = null;
        for(Specialities speciality: specialities){
            Double value = redisService.getScoreFromSortedSet("lesson" + speciality ,userId.toString());
            if (value != null){
                score = value;
                break ;
            }
        }
        if (score == null) return null;
        return score.intValue();
    }

    // 특정 순위 범위 내의 강사 레슨 수를 기반으로 강사를 리턴
    public List<Long> getTutorByLesson(int start, int limit,String speciality){
        Set<Object> set = redisService.getSortedSetRange("lesson" + speciality ,start,start+limit-1);
        return set.stream()
                .map(s->(Long.parseLong((String) s)))
                .collect(Collectors.toList());

    }

    // 특정 순위 범위 내의 강사 찜순 기반으로 강사를 리턴
    public List<Long> getTutorByPick(int start, int limit,String speciality){
        Set<Object> set = redisService.getSortedSetRange("pick" + speciality ,start,start+limit-1);
        return set.stream()
                .map(s->(Long.parseLong((String) s)))
                .collect(Collectors.toList());

    }

    // 특정 순위 범위 내의 강사 평점순 기반으로 강사를 리턴
    public List<Long> getTutorByScore(int start, int limit,String speciality){
        Set<Object> set = redisService.getSortedSetRange("review"+speciality,start,start+limit-1);
        return set.stream()
                .map(s->(Long.parseLong((String) s)))
                .collect(Collectors.toList());

    }
}
