package cc.w0rm.ghost.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/15 6:36 下午
 */

@Component
@ConfigurationProperties(prefix = "coordinatorConfig")
public class CoordinatorConfig {
    @Getter
    private Long intervalTime = 5000L;
    @Getter
    private Integer waitCount = 2;
    @Getter
    private String forwardStrategy = "reorderMsgForwardStrategy";
    @Getter
    private String expireStrategy = "msgExpireStrategy";
    @Getter
    private Integer roomSize = 5;

    public CoordinatorConfig() {
    }


    public void setIntervalTime(Long intervalTime) {
        if (intervalTime == null) {
            return;
        }

        this.intervalTime = intervalTime;
    }

    public void setWaitCount(Integer waitCount) {
        if (waitCount == null){
            return;
        }

        this.waitCount = waitCount;
    }

    public void setForwardStrategy(String forwardStrategy) {
        if (Strings.isBlank(forwardStrategy)){
            return;
        }

        this.forwardStrategy = forwardStrategy;
    }

    public void setExpireStrategy(String expireStrategy) {
        if (Strings.isBlank(expireStrategy)){
            return;
        }

        this.expireStrategy = expireStrategy;
    }

    public void setRoomSize(Integer roomSize) {
        if (roomSize == null){
            return;
        }

        this.roomSize = roomSize;
    }

}
