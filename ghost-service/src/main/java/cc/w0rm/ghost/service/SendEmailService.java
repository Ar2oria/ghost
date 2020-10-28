package cc.w0rm.ghost.service;

import cc.w0rm.ghost.mysql.dao.EmailDALImpl;
import cc.w0rm.ghost.mysql.po.Email;
import cc.w0rm.ghost.util.HttpUtils;
import cc.w0rm.ghost.util.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.concurrent.CompletableFuture;
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
    private AccountManagerImpl accountManagerImpl;
    
    @Resource
    private EmailDALImpl emailDAL;
    
    @Value("${spring.mail.username}")
    private String from;
    
    @Value("${spring.mail.test}")
    private String testEmailTo;
    
    public void increase(MsgSender msgSender, GroupMemberIncrease groupMemberIncrease) {
        try {
            String qq = groupMemberIncrease.getBeOperatedQQ();
            Set<String> msgGroupByCode = accountManagerImpl
                .getMsgGroupFlag(msgSender.GETTER.getLoginQQInfo().getQQCode());
            Set<String> whiteGroup = new HashSet<>();
            for (String msgGroup : msgGroupByCode) {
                whiteGroup.addAll(accountManagerImpl.getMsgGroupConsumerMemberGroups(msgGroup));
            }
            if (CollectionUtils.isEmpty(whiteGroup)) {
                return;
            }
            List<String> whiteGroupsList = new ArrayList<>(whiteGroup);
            sendTextMail(testEmailTo, "新成员加入" + qq, "发送的群号为:" + whiteGroupsList.get(0));
            process(qq, whiteGroupsList, "classpath:templates/emailTemplate.html");
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
        groupQsig = groupQsig.replace("\\u0026", "&");
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
        groupQsig = "https://qm.qq.com/cgi-bin/qm/qr?k=" + groupQsig + "&jump_from=webapi";
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
        Map<String, String> header = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("bkn", "1380053855");
        params.put("gc", group);
        header.put("Referer", "https://qun.qq.com/proxy.html?callback=1&id=1");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header
            .put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/86.0.4240.111 Safari/537.36");
        header.put("Accept", "*/*");
        header
            .put("Cookie", "pgv_info=ssid=s9625610669; pgv_pvid=6863400768; RK=JmpAk9wjGU; " + "ptcz" +
                "=c8df89f1eaa700612cb39c65aba945b7a2fc1dc8155d1bb5f57b9a9c4373b716; pgv_pvi=3055724544; " + "pgv_si" + "=s9864925184; eas_sid=21w6V012l3d110H2f0z31138T9; lplqqcomrouteLine=a20200918s10_a20200918s10;" + " " + "lolqqcomrouteLine=index-tool_index-page; tokenParams=%3Fe_code%3D507042; p_uin=o1208385859; " + "traceid=7b9e50f8ee; " + "verifysession" + "=h013b4969305b536263f6b18bc9a527d37b9924444e86e80a43e570fda38d676e94eebf5a40ae20bd85; " + "pac_uid" + "=1_1208385859; tvfe_boss_uuid=204266b89114ab29; " + "pt4_token=*0f8j72GbgPC*YR" + "*cMlAs8zAsjocAsAKQNcVKPqMrNk_; " + "p_skey=51nzO0u6KUXGJptwc-wuENW5hAofU5V9wHOgm-4dwNM_; " + "o_cookie=3372342316; uin=o1208385859; " + "ptui_loginuin=1208385859; skey=@uf4uagb5W; " + "_qpsvr_localtk=1603878836418");
        header.put("Origin", "https://qun.qq.com");
        String data = HttpUtils.get("https://admin.qun.qq.com/cgi-bin/qun_admin/get_join_k", params, header);
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        Map<String, String> parsedData = JacksonUtils
            .jsonString2Object(data, new TypeReference<Map<String, String>>() {});
        if (CollectionUtils.isEmpty(parsedData)) {
            return null;
        }
        return parsedData.getOrDefault("k", "");
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
