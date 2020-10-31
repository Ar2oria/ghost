package cc.w0rm.ghost.entity.platform;

import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/31 3:35 上午
 */
@Component("jd")
public class JdParser extends DefaultParser {
    @Override
    public String getPlatform() {
        return "jd";
    }
}
