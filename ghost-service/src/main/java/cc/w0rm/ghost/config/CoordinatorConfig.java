package cc.w0rm.ghost.config;

import cc.w0rm.ghost.entity.forward.ForwardStrategy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/15 6:36 下午
 */
@Data
@Component
@ConfigurationProperties(prefix = "coordinatorConfig")
public class CoordinatorConfig {
    private Long intervalTime;

    private Integer waitCount;

    private String forwardStrategy;

    private String expireStrategy;

    private Integer roomSize;

    public ForwardStrategy getMsgExpireStrategy() {
        return null;
    }
}
