package cc.w0rm.ghost.entity.forward;

import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.GroupMsgType;
import com.forte.qqrobot.beans.messages.types.PowerType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : xuyang
 * @date : 2020/10/21 12:55 上午
 */
public class MsgGetExt implements  GroupMsg {
    @Getter
    private GroupMsg groupMsg;

    @Getter
    @Setter
    private String msgGroup;

    public MsgGetExt (GroupMsg groupMsg){
        this.groupMsg = groupMsg;
    }

    @Override
    public String getQQ() {
        return groupMsg.getQQ();
    }

    @Override
    public String getGroup() {
        return groupMsg.getGroup();
    }

    @Override
    public GroupMsgType getType() {
        return groupMsg.getType();
    }

    @Override
    public String getNickname() {
        return groupMsg.getNickname();
    }

    @Override
    public PowerType getPowerType() {
        return groupMsg.getPowerType();
    }

    @Override
    public void setPowerType(PowerType powerType) {
        groupMsg.setPowerType(powerType);
    }

    @Override
    public String getRemark() {
        return groupMsg.getRemark();
    }

    @Override
    public String getId() {
        return groupMsg.getId();
    }

    @Override
    public String getMsg() {
        return groupMsg.getMsg();
    }

    @Override
    public void setMsg(String newMsg) {
        groupMsg.setMsg(newMsg);
    }

    @Override
    public String getFont() {
        return groupMsg.getFont();
    }

    @Override
    public Long getTime() {
        return groupMsg.getTime();
    }

    @Override
    public String getOriginalData() {
        return groupMsg.getOriginalData();
    }

    @Override
    public String getThisCode() {
        return groupMsg.getThisCode();
    }

    @Override
    public void setThisCode(String code) {
        groupMsg.setThisCode(code);
    }
}

