package webdoc.community.config;

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
        // 파일 시스템 경로로 설정
        registry.addResourceHandler("/community/image/**")
                .addResourceLocations("file:"+path +"/");
    }


}
