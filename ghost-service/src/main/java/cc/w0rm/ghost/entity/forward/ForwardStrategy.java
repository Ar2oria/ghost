package cc.w0rm.ghost.entity.forward;

import cc.w0rm.ghost.entity.Strategy;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;

/**
 * @author : xuyang
 * @date : 2020/10/16 11:46 下午
 */

public interface ForwardStrategy extends Strategy {
    /**
     * 转发消息
     *
     * @param msgGet
     */
    void forward(MsgGet msgGet);
}
