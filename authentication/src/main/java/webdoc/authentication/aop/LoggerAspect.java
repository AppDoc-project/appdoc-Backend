package webdoc.authentication.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import webdoc.authentication.utility.log.LogTrace;
import webdoc.authentication.utility.log.TraceStatus;

/*
 * AOP 로깅 설정을 위한 point cut과 advice
 */

@Aspect
@RequiredArgsConstructor
public class LoggerAspect {
    private final LogTrace logTrace;

    @Pointcut("execution(* webdoc.authentication.repository..*(..))")
    public void repo(){}
    @Pointcut("execution(* webdoc.authentication.service..*(..)) && !within(webdoc.authentication.service.RedisService)")
    public void service(){}

    @Pointcut("execution(* webdoc.authentication.controller..*(..))")
    public void controller(){}

    @Around("repo() || service() || controller()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{
        TraceStatus status = null;
        try{
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);
            Object result = joinPoint.proceed();
            logTrace.end(status);
            return result;
        }catch(Exception e){
            logTrace.exception(status,e);
            throw e;
        }
    }
}
