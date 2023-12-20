package webdoc.community.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import webdoc.community.config.init.PostConstruct;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.repository.CommunityRepository;

@Configuration
@EnableJpaAuditing
public class ProjectConfig {
    @Autowired
    CommunityRepository communityRepository;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Profile("dev")
    public PostConstruct postConstruct(){
        return new PostConstruct(communityRepository);
    }
}
