package cc.w0rm.ghost.service.consumer;

import cc.w0rm.ghost.api.MsgConsumer;
import cc.w0rm.ghost.util.HttpUtils;
import cc.w0rm.ghost.util.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.forte.qqrobot.bot.BotInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    
    
    @Value("${parse.transfer.path}")
    private String path;
    
    @Override
    public void consume(BotInfo botInfo, String group, MsgGet msgGet) {
        //if (botGroups.contains(group)) {
        //    String sendMsg = buildMsg();
        //    botInfo.getSender().SENDER.sendGroupMsg(group, sendMsg);
        //}
        if ("830628164".equals(group)) {
            String sendMsg = buildMsg(msgGet.getMsg());
            if (StringUtils.isEmpty(sendMsg) || "-1".equals(sendMsg)) {
                sendMsg = msgGet.getMsg();
            }
            botInfo.getSender().SENDER.sendGroupMsg("830628164", sendMsg);
        }
    }
    
    public abstract Map<String, String> buildParameter();
    
    private String buildMsg(String msg) {
        Map<String, String> requestParameters = buildParameter();
        requestParameters.put("text", msg);
        String data = HttpUtils.get(path, requestParameters, new HashMap<>());
        Map<String, String> goodsMap = JacksonUtils
            .jsonString2Object(data, new TypeReference<Map<String, String>>() {});
        return goodsMap.getOrDefault("text", "");
    }
}
