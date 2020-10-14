package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.dto.MsgGetDTO;
import cc.w0rm.ghost.entity.ForwardResult;
import org.springframework.stereotype.Service;

/**
 * @author : xuyang
 * @date : 2020/10/13 1:41 上午
 */
@Service
public class CoordinatorImpl implements Coordinator {
    /**
     * 设置消息转发策略
     *
     * @param strategy
     */
    @Override
    public void setForwardStrategy(Object strategy) {

    }

    /**
     * 将消息转发到同消息组，该消息会同步到消息组中所有消费者
     *
     * @param msgGet
     * @return
     */
    @Override
    public ForwardResult forward(MsgGetDTO msgGet) {
        return null;
    }

    /**
     * 将消息转发到指定的消息组，该消息会同步到消息组中所有消费者
     *
     * @param flag
     * @param msgGet
     * @return
     */
    @Override
    public ForwardResult forward(String flag, MsgGetDTO msgGet) {
        return null;
    }

    /**
     * 将消息转发到指定的qq群
     *
     * @param group
     * @param msgGet
     * @return
     */
    @Override
    public ForwardResult forwardGroup(String group, MsgGetDTO msgGet) {
        return null;
    }
}
