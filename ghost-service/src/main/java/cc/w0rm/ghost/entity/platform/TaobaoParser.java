package cc.w0rm.ghost.entity.platform;

import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/27 6:00 下午
 */

@Component("taobao")
public class TaobaoParser extends DefaultParser {
    @Override
    public String getPlatform() {
        return "taobao";
    }
}
