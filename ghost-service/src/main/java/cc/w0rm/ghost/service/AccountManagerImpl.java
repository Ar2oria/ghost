package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.AccountManager;
import cc.w0rm.ghost.entity.GroupRuler;
import com.forte.qqrobot.beans.messages.CodesAble;
import com.forte.qqrobot.bot.BotInfo;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author : xuyang
 * @date : 2020/10/13 2:18 上午
 */
@Component
public class AccountManagerImpl implements AccountManager {

    /**
     * 通过消息组标识获取组成员
     *
     * @param flag
     * @return
     */
    @Override
    public Set<BotInfo> listMsgGroupMember(String flag) {
        return null;
    }

    /**
     * 通过qq信息获取组成员
     *
     * @param codesAble
     * @return
     */
    @Override
    public Set<BotInfo> listMsgGroupMember(CodesAble codesAble) {
        return null;
    }

    /**
     * 通过qq号获取消息组标识
     *
     * @param code
     * @return
     */
    @Override
    public String getMsgGroupFlag(String code) {
        return null;
    }

    /**
     * 通过qq信息获取消息组标识
     *
     * @param codesAble
     * @return
     */
    @Override
    public String getMsgGroupFlag(CodesAble codesAble) {
        return null;
    }

    /**
     * 通过qq号获取群管理/转发规则
     *
     * @param code
     * @return
     */
    @Override
    public GroupRuler getGroupRuler(String code) {
        return null;
    }

    /**
     * 通过qq信息获取群管理/转发规则
     *
     * @param codesAble
     * @return
     */
    @Override
    public GroupRuler getGroupRuler(CodesAble codesAble) {
        return null;
    }
}
