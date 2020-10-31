package cc.w0rm.ghost.config;

import cc.w0rm.ghost.common.http.MyRequestProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : xuyang
 * @date : 2020/10/30 3:20 上午
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "request")
public class RequestProperties extends LinkedHashMap<String, Object> {

    @SuppressWarnings("unchecked")
    public MyRequestProperties getProps(String key){
        Map<String, Object> keyNode = (Map<String, Object>)get(key);

        MyRequestProperties myRequestProperties = new MyRequestProperties();
        myRequestProperties.setUrl(keyNode.get("url").toString());
        myRequestProperties.setAppSecret(keyNode.get("appsecret").toString());
        myRequestProperties.setArgs((Map<String, Object>)keyNode.get("args"));

        return myRequestProperties;
    }

}
