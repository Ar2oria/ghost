package cc.w0rm.ghost.config;

import cc.w0rm.ghost.common.util.TypeUtil;
import cc.w0rm.ghost.config.color.InterceptContext;
import cc.w0rm.ghost.config.color.InterceptNode;
import cc.w0rm.ghost.config.role.ConfigRole;
import cc.w0rm.ghost.config.role.Consumer;
import cc.w0rm.ghost.config.role.MsgGroup;
import cc.w0rm.ghost.config.role.Producer;
import cc.w0rm.ghost.entity.*;
import cc.w0rm.ghost.enums.ColorEnum;
import cc.w0rm.ghost.enums.RoleEnum;
import cn.hutool.core.collection.CollUtil;
import com.forte.qqrobot.bot.BotInfo;
import com.forte.qqrobot.bot.BotManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/13 11:07 上午
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "accountManager")
public class AccountManagerConfig extends LinkedHashMap<String, Object> {
    /**
     * 所有在线的qq账号
     */
    private ImmutableSet<String> _onlineCodes;

    /**
     * key：qq账号，value：所属的消息组
     */
    private Map<String, Set<String>> _codeGroup;

    /**
     * key：消息组名，value：组中的生产者与消费者
     */
    private Map<String, PartnerInfo<List<String>>> _group;

    /**
     * key：qq账号，value：对应的生产者消费者的黑白名单
     */
    private Map<String, PartnerInfo<ConfigRole>> _rule;

    /**
     * key：消息组名，value：消息组数据，组中的生产者与消费者均为已登陆账号
     */
    private ImmutableMap<String, MsgGroup> onlineMsgGroup;

    /**
     * 生产者拦截器
     */
    private InterceptNode producerIntercept;

    /**
     * 消费者拦截器
     */
    private InterceptNode consumerIntercept;

    /**
     * 配置文件加载状态
     */
    private volatile boolean initState = false;

    private static final String MAP_REGEX = "^\\{(.*)}$";
    private static final Pattern MAP_PATTERN = Pattern.compile(MAP_REGEX);

    @Resource
    private BotManager botManager;
    @Resource
    private InterceptContext interceptContext;

    @PostConstruct
    public synchronized void init() {
        log.info("[ghost-robot] 加载配置文件...");

        if (isPrepared()) {
            return;
        }
        // 加载qq号信息
        loadCodes();

        // 解析消息组的配置文件
        parseMsgGroup();

        // 解析规则的配置文件
        parseRule();

        // 组装消息组数据
        loadMsgGroup();

        // 生成拦截器
        createIntercept();

        initState = true;

        log.info("[ghost-robot] 配置文件加载完成");
    }

    public void refresh() {
        loadCodes();
        loadCodes();
        loadMsgGroup();
    }

    /**
     * 解析配置文件，读取消息组
     */
    private void parseMsgGroup() {
        Map<String, Object> msgGroup = TypeUtil.convert(get("msgGroup"));
        Set<String> names = msgGroup.keySet();
        if (CollUtil.isEmpty(names)) {
            log.error("没有发现到任何消息组，请检查配置文件");
            return;
        }

        // key - 消息组名称， value - 对应的生产者/消费者的qq号
        this._group = names.stream()
                .collect(Collectors.toMap(Function.identity(), name -> {
                    Map<String, Object> role = TypeUtil.convert(msgGroup.get(name));
                    String producer = TypeUtil.convert(role.get("producer"));
                    String consumer = TypeUtil.convert(role.get("consumer"));
                    List<String> producerGroup = split(producer);
                    if (CollUtil.isEmpty(producerGroup)) {
                        throw new IllegalStateException("消息组[" + name + "]创建失败，没有配置生产者");
                    }
                    List<String> consumerGroup = split(consumer);
                    loadCodeGroup(name, producerGroup, consumerGroup);
                    log.info("加载消息组内生产者、消费者完成");

                    return new PartnerInfo<>(producerGroup, consumerGroup);
                }));

        log.info("加载消息组完成，消息组:{}", _group);
    }

    private void loadCodeGroup(String name, List<String> producerGroup, List<String> consumerGroup) {
        if (CollUtil.isNotEmpty(producerGroup)){
            addCodeGroup(name, producerGroup);
        }

        if (CollUtil.isNotEmpty(consumerGroup)){
            addCodeGroup(name, consumerGroup);
        }
    }

    private void addCodeGroup(String name, List<String> codes) {
        if (CollUtil.isEmpty(_codeGroup)){
            this._codeGroup = new HashMap<>();
        }

        codes.forEach(code->{
            Set<String> groups = this._codeGroup.get(code);
            if (CollUtil.isEmpty(groups)){
                groups = new HashSet<>();
                this._codeGroup.put(code, groups);
            }
            groups.add(name);
        });
    }


    /**
     * 解析配置文件，读取规则
     */
    private void parseRule() {
        if (CollUtil.isEmpty(_rule)) {
            this._rule = new HashMap<>();
        }

        Map<String, Object> rule = TypeUtil.convert(get("rule"));
        if (!CollUtil.isEmpty(rule)) {
            Map<String, Object> producer = TypeUtil.convert(rule.get("producer"));
            String black = TypeUtil.convert(producer.get("black"));
            parseRule(black, RoleEnum.PRODUCER, ColorEnum.BLACK);
            String white = TypeUtil.convert(producer.get("white"));
            parseRule(white, RoleEnum.PRODUCER, ColorEnum.WHITE);

            Map<String, Object> consumer = TypeUtil.convert(rule.get("consumer"));
            black = TypeUtil.convert(consumer.get("black"));
            parseRule(black, RoleEnum.CONSUMER, ColorEnum.BLACK);
            white = TypeUtil.convert(consumer.get("white"));
            parseRule(white, RoleEnum.CONSUMER, ColorEnum.WHITE);
        }

        List<String> codes = listOnlineCodes();
        if (CollUtil.isNotEmpty(codes)) {
            codes.forEach(code -> {
                PartnerInfo<ConfigRole> configRole = _rule.get(code);
                if (Objects.isNull(configRole)) {
                    configRole = new PartnerInfo<>(new ConfigRole(code), new ConfigRole(code));
                    this._rule.put(code, configRole);
                }
            });
        }

        log.info("加载消息组规则完成");
    }

    private void parseRule(String config, RoleEnum roleEnum, ColorEnum colorEnum) {
        Map<String, List<String>> book = parseRuleConfig(config);
        book.keySet().forEach(code -> {
            PartnerInfo<ConfigRole> rule = _rule.get(code);
            if (Objects.isNull(rule)) {
                ConfigRole producerRule = new ConfigRole(code);
                ConfigRole consumerRule = new ConfigRole(code);
                rule = new PartnerInfo<>(producerRule, consumerRule);
                _rule.put(code, rule);
            }

            ConfigRole role = null;
            switch (roleEnum) {
                case PRODUCER:
                    role = rule.getProducer();
                    break;
                case CONSUMER:
                    role = rule.getConsumer();
                    break;
                default:
                    break;
            }

            Set<String> colorSet = null;
            switch (colorEnum) {
                case BLACK:
                    colorSet = role.getBlackSet();
                    break;
                case WHITE:
                    colorSet = role.getWhiteSet();
                    break;
                default:
                    break;
            }

            colorSet.addAll(book.get(code));
        });
    }


    private Map<String, List<String>> parseRuleConfig(String config) {
        if (Strings.isBlank(config)) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> result = new HashMap<>();
        Matcher matcher = MAP_PATTERN.matcher(config);
        if (!matcher.find()) {
            List<String> codes = split(config);
            result.put("#", codes);
            return result;
        }

        String mapConfig = matcher.group(1);
        String[] split = mapConfig.split(";");
        for (String kvStr : split) {
            if (Strings.isBlank(kvStr)) {
                continue;
            }

            String[] kvSplit = kvStr.split(":");
            if (kvSplit.length != 2) {
                throw new IllegalStateException("解析规则失败，请检查配置文件");
            }
            result.put(kvSplit[0], split(kvSplit[1]));
        }

        return result;
    }

    private void createIntercept() {
        if (CollUtil.isEmpty(_rule)) {
            log.warn("跳过加载拦截器，无任何账号登陆");
            return;
        }

        InterceptNode producerRoot = new InterceptNode();
        InterceptNode consumerRoot = new InterceptNode();

        InterceptNode producerPre = producerRoot;
        InterceptNode consumerPre = consumerRoot;

        Set<String> codes = _rule.keySet();
        for (String code : codes) {
            PartnerInfo<ConfigRole> configRole = _rule.get(code);
            ConfigRole producer = configRole.getProducer();
            ConfigRole consumer = configRole.getConsumer();

            InterceptNode prTmp = createIntercept(producer);
            InterceptNode coTmp = createIntercept(consumer);

            producerPre.setNext(prTmp);
            consumerPre.setNext(coTmp);
            producerPre = prTmp;
            consumerPre = coTmp;
        }
        producerPre.setNext(getRepeatMessageIntercept());

        this.producerIntercept = producerRoot.getNext();
        this.consumerIntercept = consumerRoot.getNext();

        log.info("加载消息拦截器完成");
    }


    private InterceptNode createIntercept(ConfigRole configRole) {
        if (Objects.isNull(configRole)) {
            throw new NullPointerException("无法加载自定义拦截器，ConfigRole为空");
        }

        InterceptNode interceptNode = new InterceptNode();
        interceptNode.setIntercept(interceptContext.getInterceptStrategy(configRole));

        return interceptNode;
    }

    private InterceptNode getRepeatMessageIntercept() {
        InterceptNode interceptNode = new InterceptNode();
        interceptNode.setIntercept(interceptContext.getRepeatMessageHandleStrategy());
        return interceptNode;
    }


    private void loadCodes() {
        this._onlineCodes = ImmutableSet.copyOf(Lists.newArrayList(botManager.bots()).stream()
                .map(BotInfo::getBotCode).collect(Collectors.toSet()));
    }

    private void loadMsgGroup() {
        if (CollUtil.isEmpty(this._group)){
            log.warn("初始化消息组失败，原因：消息组为空");
            return;
        }

        Map<String, MsgGroup> collect = _group.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), name -> {
                    PartnerInfo<List<String>> listPartnerInfo = _group.get(name);
                    List<String> producerCodes = listPartnerInfo.getProducer();
                    List<String> consumerCodes = listPartnerInfo.getConsumer();

                    Set<Producer> producerSet = producerCodes.stream()
                            .filter(this::isOnline)
                            .map(code -> {
                                PartnerInfo<ConfigRole> partnerInfo = _rule.get(code);
                                ConfigRole producerRule = partnerInfo.getProducer();
                                return new Producer(botManager.getBot(code), producerRule.getBlackSet(), producerRule.getWhiteSet());
                            }).collect(Collectors.toSet());

                    Set<Consumer> consumerSet = consumerCodes.stream()
                            .filter(this::isOnline)
                            .map(code -> {
                                PartnerInfo<ConfigRole> partnerInfo = _rule.get(code);
                                ConfigRole consumerRule = partnerInfo.getConsumer();
                                return new Consumer(botManager.getBot(code), consumerRule.getBlackSet(), consumerRule.getWhiteSet());
                            }).collect(Collectors.toSet());

                    return new MsgGroup(name, producerSet, consumerSet);
                }));

        this.onlineMsgGroup = ImmutableMap.copyOf(collect);
    }

    private List<String> split(String str) {
        return Lists.newArrayList(str.split(","));
    }

    /**
     * 列出所有登陆的qq号
     *
     * @return
     */
    public List<String> listOnlineCodes() {
        return Lists.newArrayList(_onlineCodes);
    }

    /**
     * 判断一个账户是否在线
     *
     * @param code
     * @return
     */
    public Boolean isOnline(String code) {
        return _onlineCodes.contains(code);
    }

    /**
     * 获取所有消息组的名称
     *
     * @return
     */
    public List<String> listMsgGroupNames() {
        return Lists.newArrayList(onlineMsgGroup.keySet());
    }

    /**
     * 获取所有登陆的消息组
     *
     * @return
     */
    public List<MsgGroup> listMsgGroup() {
        return Lists.newArrayList(onlineMsgGroup.values());
    }

    /**
     * 通过消息组的名称获取组信息
     *
     * @param name
     * @return
     */
    public MsgGroup getMsgGroup(String name) {
        return onlineMsgGroup.get(name);
    }

    /**
     * 获取一个账号所属的所有消息组
     * @param code
     * @return
     */
    public List<String> getMsgGroupByCode(String code){
        return Lists.newArrayList(_codeGroup.get(code));
    }

    /**
     * 获取一个账号的生产者/消费者下的黑名单
     *
     * @param code
     * @param roleEnum
     * @return
     */
    public Set<String> getBlackSet(String code, RoleEnum roleEnum) {
        PartnerInfo<ConfigRole> partnerInfo = this._rule.get(code);
        switch (roleEnum) {
            case PRODUCER:
                return partnerInfo.getProducer().getBlackSet();
            case CONSUMER:
                return partnerInfo.getConsumer().getBlackSet();
            default:
                return Collections.emptySet();
        }
    }

    /**
     * 获取一个账号下的生产者/消费者下的白名单
     *
     * @param code
     * @param roleEnum
     * @return
     */
    public Set<String> getWhiteSet(String code, RoleEnum roleEnum) {
        PartnerInfo<ConfigRole> partnerInfo = this._rule.get(code);
        switch (roleEnum) {
            case PRODUCER:
                return partnerInfo.getProducer().getWhiteSet();
            case CONSUMER:
                return partnerInfo.getConsumer().getWhiteSet();
            default:
                return Collections.emptySet();
        }
    }

    public boolean isPrepared() {
        return initState;
    }

    public InterceptNode getProducerIntercept() {
        return producerIntercept;
    }

    public InterceptNode getConsumerIntercept() {
        return consumerIntercept;
    }
}
