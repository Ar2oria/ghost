package cc.w0rm.ghost.service.consumer;

import cc.w0rm.ghost.api.MsgConsumer;
import cc.w0rm.ghost.util.HttpUtils;
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
 * @author panyupeng
 * @date 2020-10-27 03:11
 */
@Slf4j
@Service
public abstract class BaseConsumer implements MsgConsumer {
    
    @Override
    public void consume(BotInfo botInfo, String group, MsgGet msgGet) {
        Set<String> botGroups = botInfo.getSender().GETTER.getGroupList().stream().map(Group::getCode)
            .collect(Collectors.toSet());
        // TODO 调用g哥接口组装
        
        if (botGroups.contains(group)) {
            String sendMsg = buildMsg();
            //botInfo.getSender().SENDER.sendGroupMsg(group, sendMsg);
            log.info("[MsgConsumerImpl] botGroups:{}", botGroups);
        }
    }
    
    public abstract Map<String, Object> buildParameter();
    
    private String buildMsg() {
        Map<String, Object> requestParameters = buildParameter();
        return HttpUtils.get("http://47.98.45.40:5001/XXX", requestParameters, new HashMap<>());
    }
}
