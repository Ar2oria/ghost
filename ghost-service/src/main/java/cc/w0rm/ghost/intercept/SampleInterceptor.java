package cc.w0rm.ghost.intercept;

import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.listener.MsgGetContext;
import com.forte.qqrobot.listener.MsgIntercept;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/13 12:26 上午
 */

@Beans
@Component
public class SampleInterceptor implements MsgIntercept {
    private static final String SAMPLE_QQ_GROUP = "792924131";

    @Override
    public boolean intercept(MsgGetContext context) {
        MsgGet msgGet = context.getMsgGet();
        if (!(msgGet instanceof GroupMsg)){
            return true;
        }

        GroupMsg groupMsg = (GroupMsg) msgGet;
        if (!groupMsg.getGroup().equals(SAMPLE_QQ_GROUP)){
            return true;
        }

        return false;
    }
}
