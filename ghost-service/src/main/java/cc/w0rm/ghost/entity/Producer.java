package cc.w0rm.ghost.entity;

import com.forte.qqrobot.bot.BotInfo;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author : xuyang
 * @date : 2020/10/13 2:45 下午
 */
@EqualsAndHashCode(callSuper = true)
public class Producer extends DefaultRole {
    public Producer(BotInfo botInfo, Set<String> blackSet, Set<String> whiteSet) {
        super(botInfo, blackSet, whiteSet);
    }
}
