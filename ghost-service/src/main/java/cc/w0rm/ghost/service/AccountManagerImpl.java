package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.AccountManager;
import cc.w0rm.ghost.config.AccountManagerConfig;
import cc.w0rm.ghost.config.role.Consumer;
import cc.w0rm.ghost.config.role.DefaultRole;
import cc.w0rm.ghost.config.role.MsgGroup;
import cc.w0rm.ghost.config.role.Producer;
import cc.w0rm.ghost.entity.GroupRule;
import cc.w0rm.ghost.entity.Rule;
import cc.w0rm.ghost.entity.platform.GetAble;
import cc.w0rm.ghost.entity.platform.Parser;
import cc.w0rm.ghost.enums.RoleEnum;
import cn.hutool.core.collection.CollUtil;
import com.forte.qqrobot.beans.messages.ThisCodeAble;
import com.google.common.collect.ImmutableSet;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/13 2:18 上午
 */
@Component
public class AccountManagerImpl implements AccountManager {

    @Autowired
    private AccountManagerConfig accountManagerConfig;

    @Autowired
    private Map<String, Parser> parserMap;

    @Override
    public List<String> listAllGroups() {
        return accountManagerConfig.listMsgGroupNames();
    }

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

    @Override
    public List<MsgGroup> listLeadGroup(ThisCodeAble codesAble) {
        if (codesAble == null) {
            return null;
        }
        Set<String> msgGroupFlag = getMsgGroupFlag(codesAble);
        return msgGroupFlag.stream()
                .filter(flag -> isProducer(codesAble.getThisCode(), flag))
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
    public Set<Consumer> getMsgGroupConsumerMember(ThisCodeAble codesAble) {
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
    public boolean isProducer(String code, String group) {
        if (code == null || group == null){
            return false;
        }

        MsgGroup msgGroup = accountManagerConfig.getMsgGroup(code);
        Set<String> collect = msgGroup.getProducer().stream()
                .map(Producer::getQQCode)
                .collect(Collectors.toSet());

        return collect.contains(code);
    }


    @Override
    public boolean isProducer(ThisCodeAble codesAble) {
        if (codesAble == null){
            return false;
        }

        return isProducer(codesAble.getThisCode());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, GetAble> getPlatformConfig(String platform) {
        if (Strings.isBlank(platform)){
            return Collections.emptyMap();
        }
        Parser parser = parserMap.get(platform);
        if (parser == null){
            throw new IllegalArgumentException("不支持的平台，请制定解析器");
        }

        Object o = accountManagerConfig.get("platform");
        parser.parse((LinkedHashMap<String, Object>)o);

        return parser.getMsgGroupConfig();
    }
}
