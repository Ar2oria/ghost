package cc.w0rm.ghost.service;

import cc.w0rm.ghost.listener.GroupIncreaseListener;
import cc.w0rm.ghost.mysql.dao.EmailDALImpl;
import cc.w0rm.ghost.mysql.po.Email;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberIncrease;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.result.GroupList;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.forte.qqrobot.sender.MsgSender;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author panyupeng
 * @date 2020-10-15 17:42
 */
@Service
@Slf4j
public class SendEmailService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Resource
    private JavaMailSender javaMailSender;
    
    @Resource
    private EmailDALImpl emailDAL;
    
    @Value("${spring.mail.username}")
    private String from;
    
    public void execute(MsgSender msgSender, GroupMemberIncrease groupMemberIncrease) {
        try {
            String qq = groupMemberIncrease.getBeOperatedQQ();
            Set<String> targetQQJoinedGroups = emailDAL.getTargetQQJoinedGroups(qq);
            GroupList groupList = msgSender.GETTER.getGroupList();
            List<String> curQQGroups = groupList.stream().map(Group::getGroupCode).collect(Collectors.toList());
            curQQGroups.removeAll(targetQQJoinedGroups);
            if (CollectionUtils.isEmpty(curQQGroups)) {
                return;
            }
            sendTextMail("3372342316@qq.com", "新成员加入" + qq, "发送的群号为:" + curQQGroups.get(0));
            File file = ResourceUtils.getFile("classpath:templates/emailTemplate.html");
            Context context = new Context();
            context.setVariable("qq", qq);
            String emailContent = new TemplateEngine().process(new String(Files.readAllBytes(file.toPath())), context);
            sendHtmlMail(qq + "@qq.com", "主题:您好请点击激活账号", emailContent);
            Email email = new Email();
            email.setQqAccount(Integer.parseInt(qq));
            targetQQJoinedGroups.add(curQQGroups.get(0));
            emailDAL.addEmail(email, targetQQJoinedGroups);
        } catch (Exception e) {
            log.error("新成员加入邮件发送失败 成员qq:{} 群组:{}", groupMemberIncrease.getBeOperatedQQ(), groupMemberIncrease
                .getGroup(), e);
        }
    }
    
    
    /**
     * 发送纯文本邮件
     *
     * @param to      邮件接收方
     * @param subject 邮件主题
     * @param text    邮件内容
     */
    private void sendTextMail(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        try {
            javaMailSender.send(simpleMailMessage);
            logger.info("邮件已发送。");
        } catch (Exception e) {
            logger.error("邮件发送失败。" + e.getMessage());
        }
    }
    
    /**
     * 发送html形式邮件
     *
     * @param to
     * @param subject
     * @param content
     */
    private void sendHtmlMail(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
            logger.info("html邮件发送成功");
        } catch (MessagingException e) {
            logger.error("发送html邮件时发生异常！", e);
        }
    }
}
