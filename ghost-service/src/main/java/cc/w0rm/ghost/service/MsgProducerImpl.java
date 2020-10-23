package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.api.MsgProducer;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.sender.MsgSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : xuyang
 * @date : 2020/10/15 3:40 下午
 */
@Slf4j
@Service
public class MsgProducerImpl implements MsgProducer {

    @Autowired
    private Coordinator coordinator;

    @Override
    public void make(MsgSender msgSender, GroupMsg groupMsg) {

        // 1. 解析消息

        // 2. 判断解析结果

        // 3. 存储数据库

        // 4. 使用协调者转发消息 coordinator.forward()

        try {
            coordinator.forward(groupMsg);
        } catch (Exception exp) {
            log.error("消息生产者，转发消息失败 msgId[{}]",
                    groupMsg.getId(), exp);
        }

    }
}
