package cc.w0rm.ghost.api;

import cc.w0rm.ghost.config.role.Consumer;
import cc.w0rm.ghost.config.role.MsgGroup;
import cc.w0rm.ghost.entity.GroupRule;
import com.forte.qqrobot.beans.messages.ThisCodeAble;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;

import java.util.List;
import java.util.Set;

/**
 * @author : xuyang
 * @date : 2020/10/13 2:08 上午
 */
public interface AccountManager {
    /**
     * 通过消息组标识获取组
     * @param flag
     * @return
     */
    MsgGroup getMsgGroup(String flag);
    /**
     * 通过qq信息获取组
     * @param codesAble
     * @return
     */
    List<MsgGroup> listMsgGroup(ThisCodeAble codesAble);

    /**
     * 通过消息组标识获取组成员
     * @param flag
     * @return
     */
    Set<Consumer> getMsgGroupConsumerMember(String flag);
    /**
     * 通过qq信息获取组成员
     * @param codesAble
     * @return
     */
    Set<Consumer> listMsgGroupConsumerMember(ThisCodeAble codesAble);

    /**
     * 通过qq号获取消息组标识
     * @param code
     * @return
     */
    Set<String> getMsgGroupFlag(String code);

    /**
     * 通过qq信息获取消息组标识
     * @param codesAble
     * @return
     */
    Set<String> getMsgGroupFlag(ThisCodeAble codesAble);

    /**
     * 通过qq信息获取群管理/转发规则
     * @param codesAble
     * @return
     */
    GroupRule getGroupRuler(ThisCodeAble codesAble);

    boolean isProducer(String code);

    boolean isProducer(ThisCodeAble codeAble);
}
