package cc.w0rm.ghost.api;

import cc.w0rm.ghost.dto.BotInfoDTO;
import cc.w0rm.ghost.dto.CodesDTO;
import cc.w0rm.ghost.entity.GroupRuler;

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
    Set<BotInfoDTO> listMsgGroupMember(String flag);
    /**
     * 通过qq信息获取组成员
     * @param codesAble
     * @return
     */
    Set<BotInfoDTO> listMsgGroupMember(CodesDTO codesAble);

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
    String getMsgGroupFlag(CodesDTO codesAble);

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
    GroupRuler getGroupRuler(CodesDTO codesAble);
}
