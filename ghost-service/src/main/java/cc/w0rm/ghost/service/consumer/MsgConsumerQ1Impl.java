package cc.w0rm.ghost.service.consumer;

import cc.w0rm.ghost.api.MsgConsumer;
import cc.w0rm.ghost.config.AccountManagerConfig;
import cc.w0rm.ghost.config.role.MsgGroup;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.forte.qqrobot.bot.BotInfo;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Service("q1")
public class MsgConsumerQ1Impl extends BaseConsumer {
    
    /**
     * @param botInfo 账号信息
     * @param group   qq群号
     * @param msgGet  消息
     */
    @Override
    public void consume(BotInfo botInfo, String group, MsgGet msgGet) {
        log.debug("[q1] 消费者：[{}] , 接收到消息：[{}] ==> 群qq：[{}]", botInfo.getBotCode(), msgGet.getId(), group);
        super.consume(botInfo, group, msgGet);
    }
    
    // TODO 可以改成配置文件 但是我懒得改
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
