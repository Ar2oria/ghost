package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.api.MsgProducer;
import cc.w0rm.ghost.util.MsgUtil;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.sender.MsgSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : xuyang
 * @date : 2020/10/15 3:40 下午
 */
@Slf4j
@Service
public class MsgProducerImpl implements MsgProducer {
    
    @Autowired
    private Coordinator coordinator;
    
    private static final String TAOBAO_REGEX = "([\\p{Sc}])\\w{8,12}([\\p{Sc}])";
    
    private static final Pattern TAOBAO_PATTERN = Pattern.compile(TAOBAO_REGEX);
    
    private static final String CHINESE_REGEX = "[\u4e00-\u9fa5]";
    
    private static final Pattern CHINESE_PATTERN = Pattern.compile(CHINESE_REGEX);
    
    
    @Override
    public void make(MsgSender msgSender, GroupMsg groupMsg) {
        
        // 1. 解析消息
        String msg = groupMsg.getMsg();
        // 2. 判断解析结果
        if (filterByTB(msg) || filterByChineseByteAndUrl(msg)) {
            return;
        }
        // TODO 3. 存储数据库
        
        // 4. 使用协调者转发消息 coordinator.forward()
        
        try {
            coordinator.forward(groupMsg);
        } catch (Exception exp) {
            log.error("消息生产者，转发消息失败 msgId[{}]", groupMsg.getId(), exp);
        }
    }
    
    private boolean filterByTB(String groupMsg) {
        return TAOBAO_PATTERN.matcher(groupMsg).find();
    }
    
    private boolean filterByChineseByteAndUrl(String groupMsg) {
        
        boolean flag = true;
        Matcher matcher = CHINESE_PATTERN.matcher(groupMsg);
        int chineseCount = 0;
        while (matcher.find()) {
            chineseCount++;
        }
        log.info("[filterByChineseByteAndUrl] {}", chineseCount);
        if (chineseCount < 20) {
            flag = groupMsg.contains("http") || MsgUtil.FILE_PATTERN.matcher(groupMsg).find();
        }
        return flag;
    }
}
