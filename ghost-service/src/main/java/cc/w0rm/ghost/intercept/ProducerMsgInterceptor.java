package cc.w0rm.ghost.intercept;

import cc.w0rm.ghost.config.AccountManagerConfig;
import cc.w0rm.ghost.config.color.InterceptNode;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.listener.MsgGetContext;
import com.forte.qqrobot.listener.MsgIntercept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/13 12:26 上午
 */
@Slf4j
@Beans
@Component
public class ProducerMsgInterceptor implements MsgIntercept {
    @Autowired
    private AccountManagerConfig accountManagerConfig;

    @Override
    public boolean intercept(MsgGetContext context) {
        if (!accountManagerConfig.isPrepared()) {
            return true;
        }

        InterceptNode root = accountManagerConfig.getProducerIntercept();
        if (root == null) {
            return true;
        }

        boolean result = true;
        try {
            result = root.intercept(context);
        } catch (Exception exp) {
            log.error("生产者自定义拦截器执行异常", exp);
        }

        return result;
    }
}
