package cc.w0rm.ghost.aspect;

import cc.w0rm.ghost.config.AccountManagerConfig;
import cc.w0rm.ghost.config.color.InterceptNode;
import com.forte.qqrobot.listener.MsgGetContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/14 12:40 上午
 */

@Aspect
@Slf4j
@Component
public class ProducerMsgAspect {
    @Autowired
    private AccountManagerConfig accountManagerConfig;

    @Pointcut("execution(public * cc.w0rm.ghost.intercept.ProducerMsgInterceptor.intercept(..))")
    public void interceptPoint() {
    }

    @Around("interceptPoint()")
    public Object doControllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] params = joinPoint.getArgs();
        MsgGetContext context = (MsgGetContext) params[0];
        Object result = joinPoint.proceed();
        try {
            if(accountManagerConfig.isPrepared()) {
                InterceptNode root = accountManagerConfig.getProducerIntercept();
                if (root == null){
                    return result;
                }

                result = root.intercept(context);
            }
        } catch (Exception exp){
            log.error("生产者自定义拦截器执行异常", exp);
        }

        return result;
    }
}
