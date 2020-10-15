package cc.w0rm.ghost.config.role;

import com.forte.qqrobot.bot.BotInfo;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author : xuyang
 * @date : 2020/10/13 2:30 下午
 */

@EqualsAndHashCode(callSuper = true)
public class Consumer extends DefaultRole {
    public Consumer(BotInfo botInfo, Set<String> blackSet, Set<String> whiteSet) {
        super(botInfo, blackSet, whiteSet);
    }
}
