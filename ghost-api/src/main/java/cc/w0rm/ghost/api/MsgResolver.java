package cc.w0rm.ghost.api;

import cc.w0rm.ghost.dto.MsgInfoDTO;
import lombok.NonNull;

/**
 * @author : xuyang
 * @date : 2020/10/30 4:41 下午
 */


public interface MsgResolver {

    /**
     * 解析消息，返回商品信息 + 转换后的消息
     * @param msg 消息内容
     * @param group 消息组名称
     * @return
     */
    @NonNull
    MsgInfoDTO resolve(String msg, String group);

}
