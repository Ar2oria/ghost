package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.api.MsgProducer;
import cc.w0rm.ghost.config.AccountManagerConfig;
import cc.w0rm.ghost.config.role.Consumer;
import cc.w0rm.ghost.dto.MsgInfoDTO;
import cc.w0rm.ghost.entity.GroupMsgExt;
import cc.w0rm.ghost.entity.GroupMsgWrap;
import cc.w0rm.ghost.entity.platform.GetAble;
import cc.w0rm.ghost.enums.ResolveType;
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
    
    @Resource
    private MsgResolverImpl msgResolverImpl;
    
    @Value("${parse.info.path}")
    private String infoPath;
    
    @Value("${parse.transfer.path}")
    private String transferPath;
    
    
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(4, Integer.MAX_VALUE, 60,
        TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactoryBuilder()
        .setDaemon(true).setNameFormat("MsgProducer-ThreadPool").build());
    
    @Override
    public void make(MsgSender msgSender, GroupMsg groupMsg) {
        
        String msg = groupMsg.getMsg();
        // 一般过滤
        if (!isNeedFilter(msg)) {
            return;
        }
        // 获取当前消息qq号对应的消息组code
        List<String> msgGroupByCode = accountManagerConfig.getMsgGroupByCode(groupMsg.getThisCode());
        try {
            // 循环将消息处理发送给每个组
            for (String msgGroup : msgGroupByCode) {
                MsgInfoDTO msgInfoDTO = msgResolverImpl.resolve(msg, msgGroup);
                if (null == msgInfoDTO || CollectionUtils.isEmpty(msgInfoDTO.getResolveList()) || StringUtils
                    .isEmpty(msgInfoDTO.getModifiedMsg())) {
                    continue;
                }
                groupMsg.setMsg(msgInfoDTO.getModifiedMsg());
                GroupMsgExt newGroupMsg = new GroupMsgExt(groupMsg,msgGroup);
                newGroupMsg.setCommodityId(msgInfoDTO.getResolveList().get(0).getCommodityId());
                // 6. 异步转发 不关心结果
                CompletableFuture.runAsync(() -> coordinator.forward(msgGroup, newGroupMsg), EXECUTOR_SERVICE);
            }
        } catch (Exception exp) {
            log.error("消息生产者，转发消息失败 msgId[{}]", groupMsg.getId(), exp);
        }
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
    
}


    
