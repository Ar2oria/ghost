package cc.w0rm.ghost.api;

import cc.w0rm.ghost.dto.MsgGetDTO;
import cc.w0rm.ghost.entity.ForwardResult;

/**
 * @author : xuyang
 * @date : 2020/10/13 1:14 上午
 */
public interface Coordinator {
    /**
     * 设置消息转发策略
     * @param strategy
     */
    void setForwardStrategy(Object strategy);

    /**
     * 将消息转发到同消息组，该消息会同步到消息组中所有消费者
     * @param msgGet
     * @return
     */
    ForwardResult forward(MsgGetDTO msgGet);

    /**
     * 将消息转发到指定的消息组，该消息会同步到消息组中所有消费者
     * @param flag
     * @param msgGet
     * @return
     */
    ForwardResult forward(String flag, MsgGetDTO msgGet);

    /**
     * 将消息转发到指定的qq群
     * @param group
     * @param msgGet
     * @return
     */
    ForwardResult forwardGroup(String group, MsgGetDTO msgGet);


}

