package cc.w0rm.ghost.entity.forward;

import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : xuyang
 * @date : 2020/10/21 12:55 上午
 */
public class MsgGetExt implements MsgGet {
    @Getter
    private MsgGet msgGet;

    @Getter
    @Setter
    private String msgGroup;


    public MsgGetExt(MsgGet msgGet){
        this.msgGet = msgGet;
    }

    /**
     * 获取ID, 一般用于消息类型判断
     */
    @Override
    public String getId() {
        return msgGet.getId();
    }

    /**
     * 一般来讲，监听到的消息大部分都会有个“消息内容”。定义此方法获取消息内容。
     * 如果不存在，则为null。（旧版本推荐为空字符串，现在不了。我变卦了）
     */
    @Override
    public String getMsg() {
        return msgGet.getMsg();
    }

    /**
     * 重新设置消息
     *
     * @param newMsg msg
     * @since 1.7.x
     */
    @Override
    public void setMsg(String newMsg) {
        msgGet.setMsg(newMsg);
    }

    /**
     * 获取消息的字体
     */
    @Override
    public String getFont() {
        return msgGet.getFont();
    }

    /**
     * 获取到的时间, 代表某一时间的秒值。一般情况下是秒值。如果类型不对请自行转化
     */
    @Override
    public Long getTime() {
        return msgGet.getTime();
    }

    /**
     * 获取原本的数据 originalData
     */
    @Override
    public String getOriginalData() {
        return msgGet.getOriginalData();
    }

    /**
     * 此消息获取的时候，代表的是哪个账号获取到的消息。
     *
     * @return 接收到此消息的账号。
     */
    @Override
    public String getThisCode() {
        return msgGet.getThisCode();
    }

    /**
     * 允许重新定义Code以实现在存在多个机器人的时候切换处理。
     *
     * @param code code
     */
    @Override
    public void setThisCode(String code) {
        msgGet.setThisCode(code);
    }
}
