package cc.w0rm.ghost.service;

import cc.w0rm.ghost.mysql.dao.EmailDALImpl;
import cc.w0rm.ghost.mysql.po.Email;
import cc.w0rm.ghost.util.HttpUtils;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberIncrease;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberReduce;
import com.forte.qqrobot.beans.messages.result.GroupList;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.forte.qqrobot.sender.MsgSender;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
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
    
    @Value("${spring.mail.test}")
    private String testEmailTo;
    
    public void increase(MsgSender msgSender, GroupMemberIncrease groupMemberIncrease) {
        try {
            String qq = groupMemberIncrease.getBeOperatedQQ();
            GroupList groupList = msgSender.GETTER.getGroupList();
            List<String> curQQGroups = groupList.stream().map(Group::getGroupCode).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(curQQGroups)) {
                return;
            }
            sendTextMail(testEmailTo, "新成员加入" + qq, "发送的群号为:" + curQQGroups.get(0));
            process(qq, curQQGroups, "classpath:templates/emailTemplate.html");
            // 发送测试邮件
        } catch (Exception e) {
            log.error("新成员加入邮件发送失败 成员qq:{}", groupMemberIncrease.getBeOperatedQQ(), e);
        }
    }
    
    public void reduce(GroupMemberReduce groupMemberReduce) {
        try {
            String qq = groupMemberReduce.getBeOperatedQQ();
            String group = groupMemberReduce.getGroupCode();
            // 发送测试邮件
            sendTextMail(testEmailTo, "成员退出" + qq, "发送的群号为:" + group);
            simpleProcess(qq, group, "classpath:templates/emailReduceTemplate.html");
        } catch (Exception e) {
            log.error("成员退出邮件发送失败 成员qq:{}", groupMemberReduce.getBeOperatedQQ(), e);
        }
    }
    
    private void simpleProcess(String qq, String group, String filePath) throws IOException {
        // 解析设置的h5文件
        File file = ResourceUtils.getFile(filePath);
        // 获取该群的加入链接
        String groupQsig = getGroupQsig(group);
        if (StringUtils.isEmpty(groupQsig)) {
            return;
        }
        groupQsig = groupQsig.replace("\\u0026", group);
        // 设置上下文 和h5文件交互
        Context context = new Context();
        context.setVariable("qqGroupUrl", groupQsig);
        String emailContent = new TemplateEngine().process(new String(Files.readAllBytes(file.toPath())), context);
        // 发送h5邮件
        sendHtmlMail(testEmailTo, "你真的舍得就这么走了吗", emailContent);
        //sendHtmlMail(qq + "@qq.com", "主题:您好请点击激活账号", emailContent);
    }
    
    private void process(String qq, List<String> curQQGroups, String filePath) throws IOException {
        Email email = emailDAL.getEmail(qq);
        if (email == null) {
            email = new Email();
        }
        Set<String> targetQQJoinedGroups = StringUtils
            .isEmpty(email.getJoinedGroups()) ? new HashSet<>() : new HashSet<>(Arrays
            .asList(email.getJoinedGroups().split(",")));
        curQQGroups.removeAll(targetQQJoinedGroups);
        // 解析设置的h5文件
        File file = ResourceUtils.getFile(filePath);
        // 获取该群的加入链接
        String groupQsig = getGroupQsig(curQQGroups.get(0));
        log.info("[腾讯加群解析测试日志] 二级解析 ret:{}", groupQsig);
        if (StringUtils.isEmpty(groupQsig)) {
            return;
        }
        groupQsig = groupQsig.replace("\\u0026", "&");
        // 设置上下文 和h5文件交互
        Context context = new Context();
        context.setVariable("qqGroupUrl", groupQsig);
        String emailContent = new TemplateEngine().process(new String(Files.readAllBytes(file.toPath())), context);
        // 发送h5邮件
        sendHtmlMail(testEmailTo, "隐藏福利开启", emailContent);
        //sendHtmlMail(qq + "@qq.com", "主题:您好请点击激活账号", emailContent);
        // 添加数据库
        email.setQqAccount(Long.parseLong(qq));
        targetQQJoinedGroups.add(curQQGroups.get(0));
        emailDAL.addEmail(email, targetQQJoinedGroups);
    }
    
    private String getGroupQsig(String group) {
        String groupKey = getGroupKey(group);
        log.info("[腾讯加群解析测试日志] 一级解析 key:{}", groupKey);
        if (StringUtils.isEmpty(groupKey)) {
            return null;
        }
        Map<String, String> header = new HashMap<>();
        header.put("Referer", "https://shang.qq.com/wpa/g_wpa_get?guin=123");
        header.put("Content-Type", "utf-8");
        header
            .put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/86.0.4240.111 Safari/537.36");
        header.put("Accept", "*/*");
        String data = HttpUtils.get("http://shang.qq.com/wpa/qunwpa?idkey=" + groupKey, new HashMap<>(), header);
        if (StringUtils.isEmpty(data) || !data.contains("tencent")) {
            return null;
        }
        String[] firstSplit = data.split("tencent");
        if (firstSplit.length < 2) {
            return null;
        }
        String[] secondSplit = firstSplit[1].split("\";");
        return "tencent" + secondSplit[0];
    }
    
    
    private String getGroupKey(String group) {
        Map<String, String> header = new HashMap<>();
        header.put("Referer", "https://shang.qq.com/wpa/g_wpa_get?guin=123");
        header.put("Content-Type", "utf-8");
        header
            .put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/86.0.4240.111 Safari/537.36");
        header.put("Accept", "*/*");
        String data = HttpUtils.get("https://shang.qq.com/wpa/g_wpa_get?guin=" + group, new HashMap<>(), header);
        if (StringUtils.isEmpty(data) || !data.contains("key")) {
            return null;
        }
        String[] afterSplit = data.split("key\":\"");
        if (afterSplit.length < 2) {
            return null;
        }
        String groupKey = afterSplit[1];
        return groupKey.split("\"}")[0];
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
