package cc.w0rm.ghost.entity.forward;

import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/22 12:22 上午
 */
@Component
public class StraightForwardStrategy extends DefaultForwardStrategy implements ExpireStrategy {
    @Override
    public void accept(MsgGet msgGet) {
        super.forward(msgGet);
    }
}
