package webdoc.authentication.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import webdoc.authentication.utility.log.LogTrace;
/*
 * AOP 로깅 설정
 */
@Configuration
@Slf4j
public class AopConfig {

    @Bean
    public LogTrace logTrace(){
        return new LogTrace(log);
    }
    @Bean
    public LoggerAspect loggerAspect() {
        return new LoggerAspect(logTrace());
    }
}
