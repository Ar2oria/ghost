package cc.w0rm.ghost.api;

import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.bot.BotInfo;

/**
 * @author : xuyang
 * @date : 2020/10/15 4:10 下午
 */
public interface MsgConsumer {
    /**
     *
     * @param botInfo 账号信息
     * @param group qq群号
     * @param msgGet 消息
     */
    void consume(BotInfo botInfo, String group, MsgGet msgGet);
}
