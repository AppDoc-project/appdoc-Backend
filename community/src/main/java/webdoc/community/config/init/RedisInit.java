package webdoc.community.config.init;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.user.Specialities;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.service.RedisService;
import webdoc.community.service.StatisticsService;
import webdoc.community.service.UserService;
import java.util.List;
/*
* 레디스 시작 시 초기화 구성
*/
@RequiredArgsConstructor
public class RedisInit {
    private final StatisticsService statisticsService;
    private final UserService userService;

    private final RedisService redisService;

    @Value("${authentication.server}")
    private String address;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void devCommunityInit(){
        List<Specialities> specialities = List.of(
                Specialities.BASS,Specialities.COMPOSITION,Specialities.DRUM,Specialities.GUITAR,
                Specialities.KEYBOARD_INSTRUMENT,Specialities.MUSIC_THEORY,Specialities.PIANO,
                Specialities.STRING_INSTRUMENT,Specialities.VOCAL,Specialities.WIND_INSTRUMENT
        );

        specialities.forEach(e->{
            redisService.deleteValues("pick"+e);
            redisService.deleteValues("review"+e);
            redisService.deleteValues("score"+e);
        });

        List<UserResponse> tutors = userService.fetchTutors(address+"/server/tutors",10_000,10_000);
        tutors.forEach(statisticsService::reviewScoreCount);
        tutors.forEach(statisticsService::pickCount);
        tutors.forEach(statisticsService::lessonCount);
    }
}
