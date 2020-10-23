package cc.w0rm.ghost.entity;

import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * @author : xuyang
 * @date : 2020/10/23 2:19 下午
 */
public abstract class MsgGetWrap implements MsgGet {
    @Getter
    private MsgGet msgGet;

    public MsgGetWrap(MsgGet msgGet){
        this.msgGet = msgGet;
    }

    public MsgGet getOriginMsg(){
        if (msgGet instanceof MsgGetWrap){
            return ((MsgGetWrap) msgGet).getOriginMsg();
        }
        return msgGet;
    }

    @Override
    public void setMsg(Function<String, String> updateMsg) {
        msgGet.setMsg(updateMsg);
    }

    @Override
    public LocalDateTime getTimeToLocalDateTime() {
        return msgGet.getTimeToLocalDateTime();
    }

    @Override
    public Object getOtherParam(String key) {
        return msgGet.getOtherParam(key);
    }

    @Override
    public <T> T getOtherParam(String key, Class<T> type) {
        return msgGet.getOtherParam(key, type);
    }

    @Override
    public int hashCode() {
        return msgGet.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return msgGet.equals(obj);
    }

    @Override
    public String toString() {
        return msgGet.toString();
    }

    @Override
    public String getId() {
        return msgGet.getId();
    }

    @Override
    public String getMsg() {
        return msgGet.getMsg();
    }

    @Override
    public void setMsg(String newMsg) {
        msgGet.setMsg(newMsg);
    }

    @Override
    public String getFont() {
        return msgGet.getFont();
    }

    @Override
    public Long getTime() {
        return msgGet.getTime();
    }

    @Override
    public String getOriginalData() {
        return msgGet.getOriginalData();
    }

    @Override
    public String getThisCode() {
        return msgGet.getThisCode();
    }

    @Override
    public void setThisCode(String code) {
        msgGet.setThisCode(code);
    }
}
