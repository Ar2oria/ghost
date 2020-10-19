package cc.w0rm.ghost.entity.forward;

import com.forte.qqrobot.beans.messages.msgget.MsgGet;

/**
 * @author : xuyang
 * @date : 2020/10/16 11:46 下午
 */

public interface ForwardStrategy {
    /**
     * 转发消息
     * @param msgGet
     */
    void forward(MsgGet msgGet);
}
