package cc.w0rm.ghost.listener;

import cc.w0rm.ghost.api.MsgProducer;
import com.forte.qqrobot.anno.ListenBody;
import com.forte.qqrobot.anno.template.OnGroup;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.sender.MsgSender;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : xuyang
 * @date : 2020/10/12 5:42 下午
 */

@Slf4j
@OnGroup
@ListenBody
@Component
public class GroupMsgListener {

    @Autowired
    private MsgProducer msgProducer;

    private ExecutorService executorService = new ThreadPoolExecutor(8,
            20,
            30,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(300),
            new ThreadFactoryBuilder().setNameFormat("Listener-ThreadPool").build(),
            new ThreadPoolExecutor.AbortPolicy());

    public void listen(MsgSender msgSender, GroupMsg groupMsg) {
        executorService.submit(()->{
            try {
                msgProducer.createGroupMsg(msgSender, groupMsg);
            }catch (Exception exp){
                log.error("[listener]消息处理异常", exp);
            }
        });
    }
}