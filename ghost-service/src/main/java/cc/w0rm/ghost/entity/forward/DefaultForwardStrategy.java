package cc.w0rm.ghost.entity.forward;

import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/16 11:51 下午
 */
@Component("defaultForwardStrategy")
public class DefaultForwardStrategy implements ForwardStrategy {
    /**
     * 转发消息
     * @param msgGet
     */
    @Override
    public void forward(MsgGet msgGet) {

    }
}
