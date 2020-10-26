package cc.w0rm.ghost.config;

import cc.w0rm.ghost.config.role.ConfigRole;
import cc.w0rm.ghost.util.MsgUtil;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.intercept.Context;
import com.forte.qqrobot.listener.MsgGetContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/25 3:37 下午
 */
@Slf4j
@Component
public class FixAtAllInterceptor implements ProducerInterceptor {

    @Override
    public boolean intercept(Context context, ConfigRole configRole) {
        if (context instanceof MsgGetContext) {
            MsgGetContext msgGetContext = (MsgGetContext) context;
            MsgGet msgGet = msgGetContext.getMsgGet();
            String msg = msgGet.getMsg();
            msg = MsgUtil.replaceAtAll(msg);
            msgGet.setMsg(msg);
        }

        return true;
    }
}
