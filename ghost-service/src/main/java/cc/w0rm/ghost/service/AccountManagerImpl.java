package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.AccountManager;
import cc.w0rm.ghost.config.AccountManagerConfig;
import cc.w0rm.ghost.config.role.Consumer;
import cc.w0rm.ghost.config.role.DefaultRole;
import cc.w0rm.ghost.config.role.MsgGroup;
import cc.w0rm.ghost.config.role.Producer;
import cc.w0rm.ghost.entity.GroupRule;
import cc.w0rm.ghost.entity.Rule;
import cc.w0rm.ghost.enums.RoleEnum;
import cn.hutool.core.collection.CollUtil;
import com.forte.qqrobot.beans.messages.ThisCodeAble;
import com.google.common.collect.ImmutableSet;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/13 2:18 上午
 */
@Component
public class AccountManagerImpl implements AccountManager {

    @Autowired
    private AccountManagerConfig accountManagerConfig;

    /**
     * 通过消息组标识获取组
     *
     * @param flag
     * @return
     */
    @Override
    public MsgGroup getMsgGroup(String flag) {
        if (Strings.isBlank(flag)) {
            return null;
        }

        return accountManagerConfig.getMsgGroup(flag);
    }

    /**
     * 通过qq信息获取组
     *
     * @param codesAble
     * @return
     */
    @Override
    public List<MsgGroup> listMsgGroup(ThisCodeAble codesAble) {
        if (codesAble == null) {
            return null;
        }

        Set<String> msgGroupFlag = getMsgGroupFlag(codesAble);
        return msgGroupFlag.stream()
                .map(this::getMsgGroup)
                .collect(Collectors.toList());
    }

    /**
     * 通过消息组标识获取组成员
     *
     * @param flag
     * @return
     */
    @Override
    public Set<Consumer> getMsgGroupConsumerMember(String flag) {
        if (Strings.isBlank(flag)) {
            return Collections.emptySet();
        }

        MsgGroup msgGroup = accountManagerConfig.getMsgGroup(flag);
        return msgGroup == null?
                Collections.emptySet(): msgGroup.getConsumer();
    }

    /**
     * 通过qq信息获取组成员
     *
     * @param codesAble
     * @return
     */
    @Override
    public Set<Consumer> listMsgGroupConsumerMember(ThisCodeAble codesAble) {
        if (codesAble == null) {
            return Collections.emptySet();
        }

        List<MsgGroup> msgGroups = listMsgGroup(codesAble);
        if (CollUtil.isEmpty(msgGroups)){
            return Collections.emptySet();
        }

        return msgGroups.stream()
                .flatMap(msgGroup -> msgGroup.getConsumer().stream())
                .collect(Collectors.toSet());
    }

    /**
     * 通过qq号获取消息组标识
     *
     * @param code
     * @return
     */
    @Override
    public Set<String> getMsgGroupFlag(String code) {
        if (Strings.isBlank(code)) {
            return Collections.emptySet();
        }

        return new HashSet<>(accountManagerConfig.getMsgGroupByCode(code));
    }

    /**
     * 通过qq信息获取消息组标识
     *
     * @param codesAble
     * @return
     */
    @Override
    public Set<String> getMsgGroupFlag(ThisCodeAble codesAble) {
        if (codesAble == null) {
            return Collections.emptySet();
        }

        return getMsgGroupFlag(codesAble.getThisCode());
    }

    /**
     * 通过qq信息获取群管理/转发规则
     *
     * @param codesAble
     * @return
     */
    @Override
    public GroupRule getGroupRuler(ThisCodeAble codesAble) {
        if (codesAble == null){
            return null;
        }

        Set<String> producerBlack = accountManagerConfig.getBlackSet(codesAble.getThisCode(), RoleEnum.PRODUCER);
        Set<String> producerWhite = accountManagerConfig.getWhiteSet(codesAble.getThisCode(), RoleEnum.CONSUMER);

        Set<String> consumerBlack = accountManagerConfig.getBlackSet(codesAble.getThisCode(), RoleEnum.CONSUMER);
        Set<String> consumerWhite = accountManagerConfig.getWhiteSet(codesAble.getThisCode(), RoleEnum.CONSUMER);

        Rule producerRule = new Rule(producerBlack, producerWhite);
        Rule consumerRule = new Rule(consumerBlack, consumerWhite);

        GroupRule groupRule = new GroupRule();
        groupRule.setCode(codesAble.getThisCode());
        groupRule.setProducer(producerRule);
        groupRule.setConsumer(consumerRule);

        return groupRule;
    }

    @Override
    public boolean isProducer(String code) {
        if (Strings.isBlank(code)){
            return false;
        }

        List<String> msgGroup = accountManagerConfig.getMsgGroupByCode(code);
        if (CollUtil.isEmpty(msgGroup)){
            return false;
        }

        for (String groupName : msgGroup){
            MsgGroup group = accountManagerConfig.getMsgGroup(groupName);
            if (group == null){
                continue;
            }

            ImmutableSet<Producer> producer = group.getProducer();
            if (CollUtil.isNotEmpty(producer)){
                Set<String> qqSet = producer.stream()
                        .map(DefaultRole::getBotCode)
                        .collect(Collectors.toSet());
                if (qqSet.contains(code)){
                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public boolean isProducer(ThisCodeAble codesAble) {
        if (codesAble == null){
            return false;
        }

        return isProducer(codesAble.getThisCode());
    }
}
