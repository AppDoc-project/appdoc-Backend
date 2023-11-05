package webdoc.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("dev")
public class StaticResourceConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 파일 시스템 경로로 설정
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:/Users/woo/Desktop/code/appdoc-Backend/community/src/main/resources/static/media/");
    }
}
