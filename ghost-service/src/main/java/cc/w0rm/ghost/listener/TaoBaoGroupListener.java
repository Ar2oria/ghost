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
public class TaoBaoGroupListener {
    
    private static final String SAMPLE_QQ_GROUP = "1095382558";
    
    @Resource
    private SendEmailService sendEmailService;
    
    public void listen(GroupMemberIncrease groupMemberIncrease) {
        try {
            //String increaseBeOperatedQQ = groupMemberIncrease.getBeOperatedQQ();
            //sendEmailService.sendTextMail("3372342316@qq.com", "新成员加入" + increaseBeOperatedQQ, "测试");
            //Context context = new Context();
            //context.setVariable("id", "006");
            //File file = ResourceUtils.getFile("classpath:templates/emailTemplate.html");
            //String emailContent = new TemplateEngine().process(new String(Files.readAllBytes(file.toPath())),
            // context);
            //sendEmailService.sendHtmlMail("3372342316@qq.com", "主题:您好请点击激活账号", emailContent);
        } catch (Exception e) {
            log.info("", e);
        }
    }
}
