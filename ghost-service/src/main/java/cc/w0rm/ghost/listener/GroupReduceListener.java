package cc.w0rm.ghost.listener;

import cc.w0rm.ghost.service.SendEmailService;
import com.forte.qqrobot.anno.ListenBody;
import com.forte.qqrobot.anno.template.OnGroupMemberReduce;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberReduce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author panyupeng
 * @date 2020-10-15 13:55
 */
@OnGroupMemberReduce
@ListenBody
@Component
@Slf4j
public class GroupReduceListener {
    
    @Resource
    private SendEmailService sendEmailService;
    
    public void listen(GroupMemberReduce groupMemberReduce) {
        try {
//            sendEmailService.reduce(groupMemberReduce);
        } catch (Exception e) {
            log.info("email邮件发送失败", e);
        }
    }
}
