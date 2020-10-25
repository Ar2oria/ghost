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
import cn.hutool.core.collection.CollUtil;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

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
        List<String> msgGroupByCode = accountManagerConfig.getMsgGroupByCode(groupMsg.getThisCode());
        log.info("[MsgProducerImpl] accountManagerConfig:{} id:{}", accountManagerConfig.toString(), msgGroupByCode);
        // 2. 判断解析结果 TODO
        String commoditysku = "";
        String commodityId = "";
        
        // 3. 存储数据库
        Commodity commodity = new Commodity();
        commodity.setCommodityId(commodityId);
        commodity.setSku(commoditysku);
        try {
            Set<String> targetCommodityPushedGroups = commodityDAL.getTargetCommodityPushedGroups(commodityId);
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
}


    
