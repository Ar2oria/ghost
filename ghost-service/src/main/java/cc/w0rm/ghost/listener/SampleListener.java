package cc.w0rm.ghost.listener;

import com.forte.qqrobot.anno.ListenBody;
import com.forte.qqrobot.anno.template.OnGroup;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.result.GroupInfo;
import com.forte.qqrobot.sender.MsgSender;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/12 5:42 下午
 */

@OnGroup
@ListenBody
@Component
public class SampleListener {
    private static final String SAMPLE_QQ_GROUP = "830628164";

    public void listen(MsgSender msgSender, GroupMsg groupMsg) {
        GroupInfo groupInfo = msgSender.getGroupInfo(groupMsg);
        msgSender.SENDER.sendGroupMsg(msgSender.bot().getBotCode() + ":" + SAMPLE_QQ_GROUP,
                "[" + groupInfo.getName() + "](" + groupMsg.getRemarkOrNickname() + "):" + groupMsg.getMsg());
    }
}
