package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.api.MsgConsumer;
import cc.w0rm.ghost.config.CoordinatorConfig;
import cc.w0rm.ghost.entity.ForwardResult;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : xuyang
 * @date : 2020/10/13 1:41 上午
 */
@Service
public class CoordinatorImpl implements Coordinator {
    @Autowired
    private CoordinatorConfig coordinatorConfig;

    @Autowired
    private MsgConsumer msgConsumer;



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
    public ForwardResult forward(MsgGet msgGet) {

        // 1. 对消息进行去重

        // 2. 使用转发策略对消息顺序进行重排

        // 3. 获取消息组中所有消费者

        // 4. 对所有消费者转发消息 msgConsumer.consume()

        return null;
    }

    /**
     * 将消息转发到指定的消息组，该消息会同步到消息组中所有消费者
     *
     * @param name
     * @param msgGet
     * @return
     */
    @Override
    public ForwardResult forward(String name, MsgGet msgGet) {
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
    public ForwardResult forwardGroup(String group, MsgGet msgGet) {
        return null;
    }

    @Override
    public CoordinatorConfig getConfig() {
        return null;
    }

}
