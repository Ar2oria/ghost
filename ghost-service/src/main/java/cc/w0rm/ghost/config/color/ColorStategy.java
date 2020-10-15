package cc.w0rm.ghost.config.color;

import cc.w0rm.ghost.config.role.ConfigRole;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.intercept.Context;
import com.forte.qqrobot.listener.MsgGetContext;
import com.forte.qqrobot.sender.intercept.SendContext;

/**
 * @author : xuyang
 * @date : 2020/10/14 6:27 下午
 */
public abstract class ColorStategy implements InterceptStrategy {

    public abstract boolean strategy(String qq, String group, ConfigRole configRole);

    @Override
    public boolean intercept(Context context, ConfigRole configRole) {
        if (context instanceof MsgGetContext) {
            MsgGet msgGet = ((MsgGetContext) context).getMsgGet();
            if (msgGet instanceof GroupMsg) {
                GroupMsg groupMsg = (GroupMsg) msgGet;
                String qq = groupMsg.getThisCode();
                return strategy(qq, groupMsg.getGroup(), configRole);
            } else {
                return false;
            }
        } else if (context instanceof SendContext) {
            SendContext sendContext = (SendContext) context;
            if ("sendGroupMsg".equals(sendContext.getMethod().getName())) {
                String qq = sendContext.get("qq").toString();
                String group = sendContext.get("group").toString();
                return strategy(qq, group, configRole);
            } else {
                return false;
            }
        }

        return false;
    }
}
