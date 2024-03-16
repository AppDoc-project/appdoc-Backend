package webdoc.community.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import webdoc.community.config.init.PostConstruct;
import webdoc.community.config.init.RedisInit;
import webdoc.community.repository.CommunityRepository;
import webdoc.community.service.CommunityService;
import webdoc.community.service.RedisService;
import webdoc.community.service.StatisticsService;
import webdoc.community.service.UserService;

/*
* 프로젝트 기본 설정
*/
@Configuration
@EnableJpaAuditing
public class ProjectConfig {
    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    CommunityService communityService;

    @Autowired
    StatisticsService statisticsService;

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Profile({"dev","local-dev"})
    public PostConstruct postConstruct(){
        return new PostConstruct(communityRepository,communityService);
    }

    @Bean
    @Profile({"dev","operation","local-dev"})
    public RedisInit redisInit(){
        return new RedisInit(statisticsService,userService,redisService);
    }
}
