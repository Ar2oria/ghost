package cc.w0rm.ghost.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * FileName : ControllerRequestIdAspect
 * <p>
 * ProjectName : xinche-after-insurance
 * <p>
 * PackageName : com.maodou.after.insurance.aspect
 * <p>
 * Description : controller层requestid添加
 *
 * @author : daisenrong
 * @version : 1.0.0
 * @date : 2018/11/16 23:02
 */
@Aspect
@Component
@Slf4j
@Order(-10)
public class ControllerRequestIdAspect {
    private static final String REQUEST_ID_KEY = "request_id";

    @Pointcut("execution(public * cc.w0rm.ghost.controller..*(..))")
    public void controllerPoint() {
    }

    @Around("controllerPoint()")
    public Object doControllerPointAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_KEY, requestId);
        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            MDC.remove(REQUEST_ID_KEY);
        }
        return result;
    }
}
