package cc.w0rm.ghost.api;

import cc.w0rm.ghost.entity.GroupRuler;
import com.forte.qqrobot.beans.messages.ThisCodeAble;
import com.forte.qqrobot.bot.BotInfo;

import java.util.Set;

/**
 * @author : xuyang
 * @date : 2020/10/13 2:08 上午
 */
public interface AccountManager {
    /**
     * 通过消息组标识获取组成员
     * @param flag
     * @return
     */
    Set<BotInfo> listMsgGroupMember(String flag);
    /**
     * 通过qq信息获取组成员
     * @param codesAble
     * @return
     */
    Set<BotInfo> listMsgGroupMember(ThisCodeAble codesAble);

    /**
     * 通过qq号获取消息组标识
     * @param code
     * @return
     */
    String getMsgGroupFlag(String code);

    /**
     * 通过qq信息获取消息组标识
     * @param codesAble
     * @return
     */
    String getMsgGroupFlag(ThisCodeAble codesAble);

    /**
     * 通过qq号获取群管理/转发规则
     * @param code
     * @return
     */
    GroupRuler getGroupRuler(String code);

    /**
     * 通过qq信息获取群管理/转发规则
     * @param codesAble
     * @return
     */
    GroupRuler getGroupRuler(ThisCodeAble codesAble);
}
