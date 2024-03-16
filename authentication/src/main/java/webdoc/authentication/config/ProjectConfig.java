package webdoc.authentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import webdoc.authentication.config.init.PostConstruct;
import webdoc.authentication.repository.UserRepository;

/*
 * 프로젝트 기본 설정
 */
@Configuration
@EnableJpaAuditing
public class ProjectConfig {
    @Autowired
    UserRepository userRepository;

    @Value("${init:true}")
    private boolean init;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Profile({"dev","local-dev"})
    public PostConstruct postConstruct(){
        return new PostConstruct(userRepository,passwordEncoder(),init);
    }

}
