package cc.w0rm.ghost.aspect;

import cc.w0rm.ghost.util.HttpServletRequests;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@Order(0)
public class ControllerLogAspect {
    @Pointcut("execution(public * cc.w0rm.ghost.controller..*(..))")
    public void controllerPoint() {
    }

    @Around("controllerPoint()")
    public Object doControllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = null;
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Object[] params = joinPoint.getArgs();
            log.info("Controller层 className={}, methodName={}, params={}, rul={}, ip={}",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                    Arrays.toString(params), request.getRequestURL(), HttpServletRequests.getIPAddress(request));
            result = joinPoint.proceed();
        } finally {
            log.info("Controller层 耗时={}(ms), className={}, methodName={}, result={}",
                    System.currentTimeMillis() - startTime, joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result);
        }
        return result;
    }
}
