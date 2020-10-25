package cc.w0rm.ghost.intercept;

import cc.w0rm.ghost.config.AccountManagerConfig;
import cc.w0rm.ghost.config.color.InterceptNode;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.result.LoginQQInfo;
import com.forte.qqrobot.sender.intercept.SendContext;
import com.forte.qqrobot.sender.intercept.SenderSendIntercept;
import com.forte.qqrobot.sender.senderlist.RootSenderList;
import com.forte.qqrobot.sender.senderlist.SenderSendList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/14 3:53 下午
 */
@Slf4j
@Beans
@Component
public class ConsumerMsgInterceptor implements SenderSendIntercept {

    @Autowired
    private AccountManagerConfig accountManagerConfig;

    @Override
    public boolean intercept(SendContext context) {
        if (!accountManagerConfig.isPrepared()) {
            return true;
        }

        InterceptNode root = accountManagerConfig.getConsumerIntercept();
        if (root == null) {
            return true;
        }

        boolean result = true;
        try {
            if ("sendGroupMsg".equals(context.getMethod().getName())) {
                SenderSendList sender = context.getSender();
                if (sender instanceof RootSenderList) {
                    LoginQQInfo loginQQInfo = ((RootSenderList) context.SENDER).getLoginQQInfo();
                    context.put("qq", loginQQInfo.getCode());
                    context.put("group", context.getParams()[0]);
                }

            }
            result = root.intercept(context);

        } catch (Exception exp) {
            log.error("消费者自定义拦截器执行异常", exp);
        } finally {
            context.clear();
        }

        return result;
    }
}
