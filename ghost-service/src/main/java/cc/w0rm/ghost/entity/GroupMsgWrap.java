package cc.w0rm.ghost.entity;

import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.GroupMsgType;
import com.forte.qqrobot.beans.messages.types.PowerType;

/**
 * @author : xuyang
 * @date : 2020/10/23 3:03 下午
 */
public abstract class GroupMsgWrap extends MsgGetWrap implements GroupMsg {

    public GroupMsgWrap(GroupMsg groupMsg){
        super(groupMsg);
    }

    @Override
    public String getQQ() {
        return ((GroupMsg)getMsgGet()).getQQ();
    }

    @Override
    public String getQQCode() {
        return ((GroupMsg)getMsgGet()).getQQCode();
    }

    @Override
    public String getCode() {
        return ((GroupMsg)getMsgGet()).getCode();
    }

    @Override
    public String getQQHeadUrl() {
        return ((GroupMsg)getMsgGet()).getQQHeadUrl();
    }

    @Override
    public Long getQQCodeNumber() {
        return ((GroupMsg)getMsgGet()).getQQCodeNumber();
    }

    @Override
    public Long getCodeNumber() {
        return ((GroupMsg)getMsgGet()).getCodeNumber();
    }

    @Override
    public String getGroup() {
        return ((GroupMsg)getMsgGet()).getGroup();
    }

    @Override
    public String getGroupCode() {
        return ((GroupMsg)getMsgGet()).getGroupCode();
    }

    @Override
    public String getGroupHeadUrl() {
        return ((GroupMsg)getMsgGet()).getGroupHeadUrl();
    }

    @Override
    public Long getGroupCodeNumber() {
        return ((GroupMsg)getMsgGet()).getGroupCodeNumber();
    }

    @Override
    public GroupMsgType getType() {
        return ((GroupMsg)getMsgGet()).getType();
    }

    @Override
    public String getFlag() {
        return ((GroupMsg)getMsgGet()).getFlag();
    }

    @Override
    public String getNickname() {
        return ((GroupMsg)getMsgGet()).getNickname();
    }

    @Override
    public PowerType getPowerType() {
        return ((GroupMsg)getMsgGet()).getPowerType();
    }

    @Override
    public void setPowerType(PowerType powerType) {
        ((GroupMsg)getMsgGet()).setPowerType(powerType);
    }

    @Override
    public String getRemark() {
        return ((GroupMsg)getMsgGet()).getRemark();
    }

    @Override
    public String getRemarkOrNickname() {
        return ((GroupMsg)getMsgGet()).getRemarkOrNickname();
    }
}
