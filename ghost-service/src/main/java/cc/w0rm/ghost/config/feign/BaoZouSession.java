package cc.w0rm.ghost.config.feign;

import lombok.Data;

/**
 * @author : xuyang
 * @date : 2020/10/30 3:16 上午
 */

@Data
public class BaoZouSession {
    private String csrf;
    private String session;
}
