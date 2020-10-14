package cc.w0rm.ghost.intercept;

import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.sender.intercept.SendContext;
import com.forte.qqrobot.sender.intercept.SenderSendIntercept;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/14 3:53 下午
 */

@Beans
@Component
public class ConsumerMsgInterceptor implements SenderSendIntercept {
    @Override
    public boolean intercept(SendContext context) {
        return false;
    }
}
