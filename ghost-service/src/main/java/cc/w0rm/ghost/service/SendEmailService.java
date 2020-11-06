package cc.w0rm.ghost.service;

import cc.w0rm.ghost.common.util.Strings;
import cc.w0rm.ghost.mysql.dao.EmailDALImpl;
import cc.w0rm.ghost.mysql.dao.QunConfigDAL;
import cc.w0rm.ghost.mysql.po.Email;
import cc.w0rm.ghost.mysql.po.QunConfig;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberIncrease;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberReduce;
import com.forte.qqrobot.sender.MsgSender;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author panyupeng
 * @date 2020-10-15 17:42
 */
@Service
@Slf4j
public class SendEmailService {

    private static final Cache<String, QunConfig> QUN_CONFIG_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.MAX_VALUE)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .softValues()
            .build();

    private static final String GROUP_URL_TEMPLATE = "https://qm.qq.com/cgi-bin/qm/qr?k=${groupKey}&jump_from=webapi";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private AccountManagerImpl accountManagerImpl;

    @Resource
    private EmailDALImpl emailDAL;

    @Autowired
    private QunConfigDAL qunConfigDAL;

    @Value("${spring.mail.username}")
    private String from;

    public void increase(MsgSender msgSender, GroupMemberIncrease groupMemberIncrease) {
        try {
            String qq = groupMemberIncrease.getBeOperatedQQ();
            Set<String> msgGroupByCode = accountManagerImpl
                    .getMsgGroupFlag(msgSender.GETTER.getLoginQQInfo().getQQCode());
            Set<String> whiteGroup = new HashSet<>();
            for (String msgGroup : msgGroupByCode) {
                whiteGroup.addAll(accountManagerImpl.getAllAvailableGroupNumbers(msgGroup));
            }
            if (CollectionUtils.isEmpty(whiteGroup)) {
                return;
            }
            List<String> whiteGroupsList = new ArrayList<>(whiteGroup);
            process(qq, whiteGroupsList, "templates/emailTemplate.html");
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
            simpleProcess(qq, group, "templates/emailReduceTemplate.html");
        } catch (Exception e) {
            log.error("成员退出邮件发送失败 成员qq:{}", groupMemberReduce.getBeOperatedQQ(), e);
        }
    }

    private void simpleProcess(String qq, String group, String filePath) {
        // 解析设置的h5文件
        String data = readFile(filePath);
        // 获取该群的加入链接
        String groupQsig = getGroupQsig(group);
        if (StringUtils.isEmpty(groupQsig)) {
            return;
        }
        // 设置上下文 和h5文件交互
        Context context = new Context();
        context.setVariable("qqGroupUrl", groupQsig);
        String emailContent = new TemplateEngine().process(data, context);
        // 发送h5邮件
        sendHtmlMail(qq + "@qq.com", "你真的舍得就这么走了吗", emailContent);
        sendTextMail(qq + "@qq.com", "促销期间，更多福利都在裙中|Apple|My Office Account", "进裙和家人们团聚：" + group);
    }

    private void process(String qq, List<String> curQQGroups, String filePath) {
        Email email = emailDAL.getEmail(qq);
        if (email == null) {
            email = new Email();
        }
        Set<String> targetQQJoinedGroups = StringUtils
                .isEmpty(email.getJoinedGroups()) ? new HashSet<>() : new HashSet<>(Arrays
                .asList(email.getJoinedGroups().split(",")));
        curQQGroups.removeAll(targetQQJoinedGroups);

        if (CollectionUtils.isEmpty(curQQGroups)){
            return;
        }

        // 解析设置的h5文件
        String data = readFile(filePath);
        // 获取该群的加入链接
        String groupQsig = getGroupQsig(curQQGroups.get(0));
        log.info("[腾讯加群解析测试日志] 二级解析 ret:{}", groupQsig);
        if (StringUtils.isEmpty(groupQsig)) {
            return;
        }

        // 设置上下文 和h5文件交互
        Context context = new Context();
        context.setVariable("qqGroupUrl", groupQsig);
        String emailContent = new TemplateEngine().process(data, context);
        // 发送h5邮件
        sendHtmlMail(qq + "@qq.com", "欢迎，您的审核已经通过|Apple|My Office Account", emailContent);
        sendTextMail(qq + "@qq.com", "【系统自动邮件】恭喜，您已经通过系统审核|Apple|My Office Account", "这是系统的自动邮件，如果您已经收到类似的邮件请忽略，" +
                "双十一期间，淘宝，京东限时优惠，十亿补贴等你来拿！快点一起加入吧，您的专属裙:" + curQQGroups.get(0));
        // 添加数据库
        email.setQqAccount(Long.parseLong(qq));
        targetQQJoinedGroups.add(curQQGroups.get(0));
        emailDAL.addEmail(email, targetQQJoinedGroups);
    }

    @NotNull
    private String readFile(String filePath) {
        String data = Strings.EMPTY;
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        try {
            byte[] bdata = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
            data = new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("IOException", e);
        }
        return data;
    }

    private String getGroupQsig(String group) {
        if (Strings.isBlank(group)) {
            return Strings.EMPTY;
        }

        QunConfig qunConfig = QUN_CONFIG_CACHE.getIfPresent(group);
        if (Objects.nonNull(qunConfig)) {
            return GROUP_URL_TEMPLATE.replace("${groupKey}", qunConfig.getGroupKey());
        }

        qunConfig = qunConfigDAL.selectByGroupCode(group);
        if (Objects.isNull(qunConfig)) {
            return Strings.EMPTY;
        }

        QUN_CONFIG_CACHE.put(group, qunConfig);
        return GROUP_URL_TEMPLATE.replace("${groupKey}", qunConfig.getGroupKey());
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
            logger.error("邮件发送失败。", e);
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
