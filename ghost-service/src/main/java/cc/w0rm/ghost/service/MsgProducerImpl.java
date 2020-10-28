package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.api.MsgProducer;
import cc.w0rm.ghost.config.AccountManagerConfig;
import cc.w0rm.ghost.config.role.Consumer;
import cc.w0rm.ghost.entity.platform.GetAble;
import cc.w0rm.ghost.mysql.dao.CommodityDALImpl;
import cc.w0rm.ghost.mysql.po.Commodity;
import cc.w0rm.ghost.util.FilterUtils;
import cc.w0rm.ghost.util.HttpUtils;
import cc.w0rm.ghost.util.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.result.GroupList;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.forte.qqrobot.sender.MsgSender;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/15 3:40 下午
 */
@Slf4j
@Service("msgProducer")
public class MsgProducerImpl implements MsgProducer {
    
    @Autowired
    private Coordinator coordinator;
    
    @Resource
    private CommodityDALImpl commodityDAL;
    
    @Autowired
    private AccountManagerConfig accountManagerConfig;
    
    @Resource
    private AccountManagerImpl accountManagerImpl;
    
    @Value("${parse.info.path}")
    private String infoPath;
    
    @Value("${parse.transfer.path}")
    private String transferPath;
    
    
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(4, Integer.MAX_VALUE, 60,
        TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactoryBuilder()
        .setDaemon(true).setNameFormat("MsgProducer-ThreadPool").build());
    
    @Override
    public void make(MsgSender msgSender, GroupMsg groupMsg) {
        
        
        // 1. 解析消息
        String msg = groupMsg.getMsg();
        if (!isNeedFilter(msg)) {
            return;
        }
        List<String> msgGroupByCode = accountManagerConfig.getMsgGroupByCode(groupMsg.getThisCode());
        // 2. 判断解析结果
        Commodity commodity = parseMsg(msg);
        if (null == commodity || StringUtils.isEmpty(commodity.getCommodityId())) {
            log.debug("消息生产者，商品解析空返，请查看解析逻辑 msgId[{}]", groupMsg.getId());
            return;
        }
        try {
            for (String msgGroup : msgGroupByCode) {
                Set<String> groupCode = accountManagerImpl.getMsgGroupConsumerMemberGroups(msgGroup);
                groupCode.parallelStream().forEach(group -> CompletableFuture
                    .runAsync(() -> addCommodityData(commodity, group), EXECUTOR_SERVICE));
                // 5. 构建转换参数 可能转换失败 为原消息
                String transfer = buildMsg(msg, msgGroup);
                groupMsg.setMsg(transfer);
                // 6. 异步转发 不关心结果
                CompletableFuture.runAsync(() -> coordinator.forward(msgGroup, groupMsg), EXECUTOR_SERVICE);
            }
        } catch (Exception exp) {
            log.error("消息生产者，转发消息失败 msgId[{}]", groupMsg.getId(), exp);
        }
    }
    
    private Commodity parseMsg(String msg) {
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("text", msg);
        String data = HttpUtils.get(infoPath, requestParameters, new HashMap<>());
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        Map<String, Object> commodityMap = JacksonUtils
            .jsonString2Object(data, new TypeReference<Map<String, Object>>() {});
        log.info("[解析测试日志] commodityMap:{}", commodityMap);
        if (CollectionUtils.isEmpty(commodityMap) || 0 != (Integer) commodityMap.get("flag")) {
            return null;
        }
        Map<String, Object> firstGoodDetail = (Map<String, Object>) commodityMap.get("good_id");
        log.info("[解析测试日志] firstGoodDetail:{}", firstGoodDetail);
        if (CollectionUtils.isEmpty(firstGoodDetail)) {
            return null;
        }
        Map<String, Object> goodsMap = (Map<String, Object>) firstGoodDetail.get("data");
        log.info("[解析测试日志] secondGoodDetail:{}", goodsMap);
        if (CollectionUtils.isEmpty(goodsMap)) {
            return null;
        }
        String goodId = (String) goodsMap.get("goods_id");
        log.info("[解析测试日志] goodId:{}", goodId);
        if (StringUtils.isEmpty(goodId)) {
            return null;
        }
        Commodity commodity = new Commodity();
        commodity.setCommodityId(goodId);
        commodity.setSku((String) goodsMap.get("title"));
        return commodity;
    }
    
    private boolean isNeedFilter(String msg) {
        if (FilterUtils.isFilterByVideo(msg)) {
            return false;
        }
        if (!(FilterUtils.isFilterByTB(msg) || FilterUtils.isFilterByChineseCount(msg))) {
            return false;
        }
        return true;
    }
    
    
    private String buildMsg(String msg, String msgGroup) {
        try {
            Map<String, GetAble> platformConfig = accountManagerImpl.getPlatformConfig("taobao");
            if (!CollectionUtils.isEmpty(platformConfig) && null != platformConfig.get(msgGroup)) {
                GetAble configInfo = platformConfig.get(msgGroup);
                Map<String, String> requestParameters = buildParameter(configInfo, msg);
                String data = HttpUtils.get(transferPath, requestParameters, new HashMap<>());
                log.info("[构建转换口令测试日志] data:{}", data);
                if (StringUtils.isEmpty(data)) {
                    return msg;
                }
                Map<String, Object> goodsMap = JacksonUtils
                    .jsonString2Object(data, new TypeReference<Map<String, Object>>() {});
                log.info("[构建转换口令测试日志] goodsMap:{}", goodsMap);
                if (CollectionUtils.isEmpty(goodsMap) || 0 != (Integer) goodsMap.get("flag")) {
                    return msg;
                }
                String transferMsg = (String) goodsMap.get("text");
                log.info("[构建转换口令测试日志] transferMsg:{}", transferMsg);
                if (!StringUtils.isEmpty(transferMsg) && "0".equals(transferMsg)) {
                    return transferMsg;
                }
            }
        } catch (Exception e) {
            log.error("[构建转换口令异常] 原消息:{} 消息组:{}", msg, msgGroup, e);
        }
        return msg;
    }
    
    private Map<String, String> buildParameter(GetAble info, String originMsg) {
        Map<String, String> ret = new HashMap<>();
        ret.put("apikey", info.get("apikey"));
        ret.put("pid_2", info.get("pid_2"));
        ret.put("pid_3", info.get("pid_3"));
        ret.put("appkey", info.get("appkey"));
        ret.put("secret", info.get("secret"));
        ret.put("uid", info.get("uid"));
        ret.put("text", originMsg);
        return ret;
    }
    
    private void addCommodityData(Commodity commodity, String group) {
        // 3. 存储数据库
        try {
            Set<String> targetCommodityPushedGroups = commodityDAL
                .getTargetCommodityPushedGroups(commodity.getCommodityId());
            if (targetCommodityPushedGroups.contains(group)) {
                return;
            }
            commodityDAL.addCommodity(commodity, group);
        } catch (Exception exp) {
            log.error("消息生产者，商品记录失败", exp);
        }
    }
}


    
