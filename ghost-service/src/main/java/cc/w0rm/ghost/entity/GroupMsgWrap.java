package cc.w0rm.ghost.entity;

import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.GroupMsgType;
import com.forte.qqrobot.beans.messages.types.PowerType;
import lombok.Getter;

/**
 * @author : xuyang
 * @date : 2020/10/23 2:19 下午
 */
public abstract class GroupMsgWrap implements GroupMsg {
    @Getter
    private GroupMsg groupMsg;

    public GroupMsgWrap(GroupMsg groupMsg){
        this.groupMsg = groupMsg;
    }

    public GroupMsg getOriginMsg(){
        if (groupMsg instanceof GroupMsgWrap){
            return ((GroupMsgWrap) groupMsg).getOriginMsg();
        }
        return groupMsg;
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
