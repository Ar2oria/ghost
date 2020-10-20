package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.api.MsgConsumer;
import cc.w0rm.ghost.config.CoordinatorConfig;
import cc.w0rm.ghost.entity.forward.ForwardStrategy;
import cc.w0rm.ghost.entity.forward.MsgGetExt;
import cc.w0rm.ghost.entity.forward.ReorderMsgForwardStrategy;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author : xuyang
 * @date : 2020/10/13 1:41 上午
 */

@Slf4j
@Service
public class CoordinatorImpl implements Coordinator {
    @Autowired
    private CoordinatorConfig coordinatorConfig;

    @Autowired
    private MsgConsumer msgConsumer;

    @Resource(type = ReorderMsgForwardStrategy.class)
    private ForwardStrategy forwardStrategy;

    /**
     * 设置消息转发策略
     *
     * @param strategy
     */
    @Override
    public void setForwardStrategy(ForwardStrategy strategy) {
        Preconditions.checkArgument(Objects.nonNull(strategy));
        this.forwardStrategy = strategy;
    }

    /**
     * 将消息转发到同消息组，该消息会同步到消息组中所有消费者
     *
     * @param msgGet
     * @return
     */
    @Override
    public boolean forward(MsgGet msgGet) {
        if (msgGet == null){
            return false;
        }

        try {
            forwardStrategy.forward(msgGet);
        }catch (Exception exp){
            log.error("转发消息异常， msg={}", msgGet, exp);
            return false;
        }

        return true;
    }

    /**
     * 将消息转发到指定的消息组，该消息会同步到消息组中所有消费者
     *
     * @param name
     * @param msgGet
     * @return
     */
    @Override
    public boolean forward(String name, MsgGet msgGet) {
        if (Strings.isBlank(name) || msgGet == null){
            return false;
        }
        if (msgGet instanceof GroupMsg){
            GroupMsg groupMsg = (GroupMsg)msgGet;
            MsgGetExt msgGetExt = new MsgGetExt(groupMsg);
            msgGetExt.setMsgGroup(name);

            return forward(msgGetExt);
        }else {
            return forward(msgGet);
        }
    }

    @Override
    public CoordinatorConfig getConfig() {
        return null;
    }

}
