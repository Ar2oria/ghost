package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.config.CoordinatorConfig;
import cc.w0rm.ghost.entity.forward.ExpireStrategy;
import cc.w0rm.ghost.entity.forward.ForwardStrategy;
import cc.w0rm.ghost.entity.forward.MsgGetExt;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
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
    private Map<String, ForwardStrategy> strategyMap;

    @Autowired
    private Map<String, ExpireStrategy> expireStrategyMap;

    private ForwardStrategy forwardStrategy;

    @PostConstruct
    public void init() {
        forwardStrategy = getForwardStrategy(coordinatorConfig.getForwardStrategy());
    }


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
    public void forward(MsgGet msgGet) {
        if (msgGet == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        if (forwardStrategy == null) {
            log.debug("forward strategy is not init... msg[{}] will be abandoned", msgGet.getId());
            return;
        }

        forwardStrategy.forward(msgGet);
    }



    /**
     * 将消息转发到指定的消息组，该消息会同步到消息组中所有消费者
     *
     * @param name
     * @param msgGet
     * @return
     */
    @Override
    public void forward(String name, MsgGet msgGet) {
        if (Strings.isBlank(name) || msgGet == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (msgGet instanceof GroupMsg) {
            GroupMsg groupMsg = (GroupMsg) msgGet;
            MsgGetExt msgGetExt = new MsgGetExt(groupMsg);
            msgGetExt.setMsgGroup(name);

            forward(msgGetExt);
        } else {
            forward(msgGet);
        }

    }

    @Override
    public CoordinatorConfig getConfig() {
        return coordinatorConfig;
    }

    @Override
    public ForwardStrategy getForwardStrategy(String expireStrategy) {
        return strategyMap.get(expireStrategy);
    }

    @Override
    public ExpireStrategy getExpireStrategy(String expire) {
        return expireStrategyMap.get(expire);
    }

}
