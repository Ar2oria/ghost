package cc.w0rm.ghost.api;

import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.sender.MsgSender;

/**
 * @author : xuyang
 * @date : 2020/10/15 3:40 下午
 */
public interface MsgProducer {
    /**
     * 接收监听器通知
     * @param msgSender
     * @param groupMsg
     */
    void createGroupMsg(MsgSender msgSender, GroupMsg groupMsg);
}
