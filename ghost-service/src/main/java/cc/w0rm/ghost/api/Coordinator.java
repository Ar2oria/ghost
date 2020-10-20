package cc.w0rm.ghost.api;

import cc.w0rm.ghost.config.CoordinatorConfig;
import cc.w0rm.ghost.entity.forward.ForwardStrategy;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;

/**
 * @author : xuyang
 * @date : 2020/10/13 1:14 上午
 */
public interface Coordinator {
    /**
     * 设置消息转发策略
     * @param strategy
     */
    void setForwardStrategy(ForwardStrategy strategy);

    /**
     * 将消息转发到同消息组，该消息会同步到消息组中所有消费者
     * @param msgGet
     * @return
     */
    boolean forward(MsgGet msgGet);

    /**
     * 将消息转发到指定的消息组，该消息会同步到消息组中所有消费者
     * @param name
     * @param msgGet
     * @return
     */
    boolean forward(String name, MsgGet msgGet);

    CoordinatorConfig getConfig();
}

