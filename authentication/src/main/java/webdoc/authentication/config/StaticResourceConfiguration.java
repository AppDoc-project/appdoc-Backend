package webdoc.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * 정적파일 제공 설정
 */
@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {
    @Value("${file.dir}")
    private String path;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/auth/image/**")
                .addResourceLocations("file:"+path+"/");
    }
}