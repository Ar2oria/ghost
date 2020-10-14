package cc.w0rm.ghost.intercept;

import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.listener.MsgGetContext;
import com.forte.qqrobot.listener.MsgIntercept;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/13 12:26 上午
 */

@Beans
@Component
public class ProducerMsgInterceptor implements MsgIntercept {

    @Override
    public boolean intercept(MsgGetContext context) {
        return false;
    }
}
