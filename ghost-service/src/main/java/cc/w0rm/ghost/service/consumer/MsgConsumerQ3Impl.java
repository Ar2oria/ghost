package cc.w0rm.ghost.service.consumer;

import cc.w0rm.ghost.api.MsgConsumer;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.forte.qqrobot.bot.BotInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/15 4:10 下午
 */

/**
 * service 必须指定名称， 名称为对应的消息组名称
 */

@Slf4j
@Service("q3")
public class MsgConsumerQ3Impl extends BaseConsumer {
    
    /**
     * @param botInfo 账号信息
     * @param group   qq群号
     * @param msgGet  消息
     */
    @Override
    public void consume(BotInfo botInfo, String group, MsgGet msgGet) {
        log.debug("[q3] 消费者：[{}] , 接收到消息：[{}] ==> 群qq：[{}]", botInfo.getBotCode(), msgGet.getId(), group);
        super.consume(botInfo, group, msgGet);
    }
    
    @Override
    public Map<String, String> buildParameter() {
        Map<String, String> ret = new HashMap<>();
        ret.put("apikey", "");
        ret.put("pid_2", "");
        ret.put("pid_3", "");
        ret.put("appkey", "");
        ret.put("sercet", "");
        return ret;
    }
}
