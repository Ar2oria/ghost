package cc.w0rm.ghost.entity.forward;

import cc.w0rm.ghost.entity.Strategy;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;

import java.util.function.Consumer;

/**
 * @author : xuyang
 * @date : 2020/10/23 2:26 上午
 */
public interface ExpireStrategy extends Consumer<MsgGet>, Strategy {

}
