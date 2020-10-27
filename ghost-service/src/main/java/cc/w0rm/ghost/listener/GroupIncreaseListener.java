package cc.w0rm.ghost.listener;

import cc.w0rm.ghost.service.SendEmailService;
import com.forte.qqrobot.anno.ListenBody;
import com.forte.qqrobot.anno.template.OnGroup;
import com.forte.qqrobot.anno.template.OnGroupMemberIncrease;
import com.forte.qqrobot.beans.messages.msgget.EventGet;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberIncrease;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.sender.MsgSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Files;

/**
 * @author panyupeng
 * @date 2020-10-15 13:55
 */
@OnGroupMemberIncrease
@ListenBody
@Component
@Slf4j
public class GroupIncreaseListener {
    
    @Resource
    private SendEmailService sendEmailService;
    
    public void listen(MsgSender msgSender, GroupMemberIncrease groupMemberIncrease) {
        try {
            sendEmailService.increase(msgSender, groupMemberIncrease);
        } catch (Exception e) {
            log.info("email邮件发送失败", e);
        }
    }
}
