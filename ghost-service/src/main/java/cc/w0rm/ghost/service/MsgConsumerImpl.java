package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.MsgConsumer;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.bot.BotInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : xuyang
 * @date : 2020/10/15 4:10 下午
 */

/**
 * service 必须指定名称， 名称为对应的消息组名称
 */

@Slf4j
@Service("q1")
public class MsgConsumerImpl implements MsgConsumer {


    /**
     * @param botInfo 账号信息
     * @param group   qq群号
     * @param msgGet  消息
     */
    @Override
    public void consume(BotInfo botInfo, String group, MsgGet msgGet) {
        log.debug("[q1] 消费者：[{}] , 接收到消息：[{}] ==> 群qq：[{}]",
                botInfo.getBotCode(),
                msgGet.getId(),
                group);
    }

}
