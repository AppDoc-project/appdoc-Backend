package webdoc.authentication.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("operation")
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /image/** URL 패턴에 해당하는 요청은 /image 폴더의 정적 리소스를 제공
        registry.addResourceHandler("/authentication_image/**")
                .addResourceLocations("file:authentication_image/"); // 정적 리소스 경로 설정
    }
}