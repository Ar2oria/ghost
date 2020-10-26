package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.api.MsgConsumer;
import cc.w0rm.ghost.api.MsgProducer;
import cc.w0rm.ghost.common.util.CompletableFutureWithMDC;
import cc.w0rm.ghost.config.AccountManagerConfig;
import cc.w0rm.ghost.config.role.Consumer;
import cc.w0rm.ghost.config.role.MsgGroup;
import cc.w0rm.ghost.mysql.dao.CommodityDALImpl;
import cc.w0rm.ghost.mysql.mapper.CommodityMapper;
import cc.w0rm.ghost.mysql.po.Commodity;
import cc.w0rm.ghost.util.HttpUtils;
import cc.w0rm.ghost.util.JacksonUtils;
import cc.w0rm.ghost.util.MsgUtil;
import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.result.GroupList;
import com.forte.qqrobot.sender.MsgSender;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;

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
    
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(4, Integer.MAX_VALUE, 60,
        TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactoryBuilder()
        .setDaemon(true).setNameFormat("MsgProducer-ThreadPool").build());
    
    @Override
    public void make(MsgSender msgSender, GroupMsg groupMsg) {
        
        
        // 1. 解析消息
        String msg = groupMsg.getMsg();
        if (!(isFilterByTB(msg) || isFilterByChineseCount(msg))) {
            return;
        }
        List<String> msgGroupByCode = accountManagerConfig.getMsgGroupByCode(groupMsg.getThisCode());
        // 2. 判断解析结果
        Commodity commodity = parseMsg(msg);
        if (null == commodity || StringUtils.isEmpty(commodity.getCommodityId())) {
            log.debug("消息生产者，商品解析空返，请查看解析逻辑 msgId[{}]", groupMsg.getId());
            return;
        }
        // 3. 存储数据库
        try {
            Set<String> targetCommodityPushedGroups = commodityDAL
                .getTargetCommodityPushedGroups(commodity.getCommodityId());
            if (targetCommodityPushedGroups.contains(groupMsg.getGroup())) {
                return;
            }
            commodityDAL.addCommodity(commodity, groupMsg.getGroup());
        } catch (Exception exp) {
            log.error("消息生产者，商品记录失败 msgId[{}]", groupMsg.getId(), exp);
        }
        //4. 使用协调者转发消息 coordinator.forward()
        try {
            for (String msgGroup : msgGroupByCode) {
                CompletableFuture.runAsync(() -> coordinator.forward(msgGroup, groupMsg), EXECUTOR_SERVICE);
            }
        } catch (Exception exp) {
            log.error("消息生产者，转发消息失败 msgId[{}]", groupMsg.getId(), exp);
        }
    }
    
    private Commodity parseMsg(String msg) {
        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put("text", msg);
        String data = HttpUtils.get("http://47.98.45.40:5001/get_self_tkl", requestParameters, new HashMap<>());
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        Map<String, String> commodityMap = JacksonUtils
            .jsonString2Object(data, new TypeReference<Map<String, String>>() {});
        if (!"0".equals(commodityMap.getOrDefault("flag", "-1"))) {
            return null;
        }
        String goodDetail = commodityMap.getOrDefault("good_id", "");
        if (StringUtils.isEmpty(goodDetail)) {
            return null;
        }
        Map<String, String> goodsMap = JacksonUtils
            .jsonString2Object(goodDetail, new TypeReference<Map<String, String>>() {});
        Commodity commodity = new Commodity();
        commodity.setCommodityId(goodsMap.getOrDefault("activity_id", ""));
        commodity.setSku(commodityMap.getOrDefault("desc", ""));
        return commodity;
    }
    
    
    private boolean isFilterByTB(String msg) {
        return MsgUtil.TAO_KOU_LING_PATTERN.matcher(msg).find();
    }
    
    private boolean isFilterByChineseCount(String msg) {
        int chineseCount = 0;
        Matcher matcher = MsgUtil.CHINESESE_PATTERN.matcher(msg);
        while (matcher.find()) {
            chineseCount++;
        }
        if (chineseCount < 20) {
            return MsgUtil.FILE_PATTERN.matcher(msg).find() || MsgUtil.URL_PATTERN.matcher(msg).find();
        }
        return true;
    }
}


    
