package cc.w0rm.ghost.entity.forward;

import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/18 11:33 下午
 */

@Component
public class MsgExpireStrategy implements ExpireStrategy {

    @Override
    public void accept(MsgGet msgGet) {
        throw new MsgForwardException("消息转发失败： 消息已过期 msgId:" + msgGet.getId());
    }
}
