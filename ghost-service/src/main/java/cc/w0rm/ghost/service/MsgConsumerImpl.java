package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.MsgConsumer;
import org.springframework.stereotype.Service;

/**
 * @author : xuyang
 * @date : 2020/10/15 4:10 下午
 */

@Service
public class MsgConsumerImpl implements MsgConsumer {
    @Override
    public void consume() {

        // 1. 获取消息下所有群

        // 2. 通过sku判断群是否接收过消息

        // 3. 消息转发

    }
}
