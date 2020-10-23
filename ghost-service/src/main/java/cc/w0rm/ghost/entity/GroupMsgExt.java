package cc.w0rm.ghost.entity;

import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import lombok.Getter;

/**
 * @author : xuyang
 * @date : 2020/10/21 12:55 上午
 */
public class GroupMsgExt extends GroupMsgWrap {
    @Getter
    private String msgGroupName;

    public GroupMsgExt(GroupMsg groupMsg, String msgGroupName) {
        super(groupMsg);
        this.msgGroupName = msgGroupName;
    }


}

