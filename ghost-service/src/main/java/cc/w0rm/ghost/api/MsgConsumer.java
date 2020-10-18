package cc.w0rm.ghost.api;

import com.forte.qqrobot.beans.messages.msgget.MsgGet;

/**
 * @author : xuyang
 * @date : 2020/10/15 4:10 下午
 */
public interface MsgConsumer {
    void consume(MsgGet msgGet);
}
