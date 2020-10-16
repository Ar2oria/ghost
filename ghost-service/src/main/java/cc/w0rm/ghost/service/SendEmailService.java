package cc.w0rm.ghost.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


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
    
    @Value("${spring.mail.username}")
    private String from;
    
    /**
     * 发送纯文本邮件
     *
     * @param to      邮件接收方
     * @param subject 邮件主题
     * @param text    邮件内容
     */
    public void sendTextMail(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        
        try {
            System.out.println(javaMailSender);
            System.out.println(simpleMailMessage);
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
    public void sendHtmlMail(String to, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        
        try {
            //true表示需要创建一个multipart message
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
