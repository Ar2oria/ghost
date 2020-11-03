package cc.w0rm.ghost.service.consumer;

import cc.w0rm.ghost.api.MsgConsumer;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.bot.BotInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author panyupeng
 * @date 2020-10-27 03:11
 */
@Slf4j
@Service
public abstract class BaseConsumer implements MsgConsumer {


    @Override
    public void consume(BotInfo botInfo, String group, MsgGet msgGet) {
        botInfo.getSender().SENDER.sendGroupMsg(group, msgGet.getMsg());
    }

}
