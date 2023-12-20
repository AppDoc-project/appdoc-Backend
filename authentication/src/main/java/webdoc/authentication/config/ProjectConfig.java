package webdoc.authentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import webdoc.authentication.config.init.PostConstruct;
import webdoc.authentication.repository.UserRepository;

@Configuration
@EnableJpaAuditing
public class ProjectConfig {
    @Autowired
    UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PostConstruct postConstruct(){
        return new PostConstruct(userRepository,passwordEncoder());
    }

}
