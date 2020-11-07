package cc.w0rm.ghost.service;

import cc.w0rm.ghost.common.util.DateTimeUtil;
import cc.w0rm.ghost.common.util.Strings;
import cc.w0rm.ghost.enums.EmailType;
import cc.w0rm.ghost.mysql.dao.EmailDAL;
import cc.w0rm.ghost.mysql.dao.QunConfigDAL;
import cc.w0rm.ghost.mysql.po.Email;
import cc.w0rm.ghost.mysql.po.QunConfig;
import cc.w0rm.ghost.util.FileUtil;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberIncrease;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberReduce;
import com.forte.qqrobot.sender.MsgSender;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


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

    @Autowired
    private EmailDAL emailDAL;

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
            if (CollectionUtils.isEmpty(whiteGroup) || whiteGroup.contains(groupMemberIncrease.getGroup())) {
                return;
            }
            List<String> whiteGroupsList = new ArrayList<>(whiteGroup);
            welcomeCustomer(qq, whiteGroupsList);
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
            begCustomers(qq, group);
        } catch (Exception e) {
            log.error("成员退出邮件发送失败 成员qq:{}", groupMemberReduce.getBeOperatedQQ(), e);
        }
    }

    private void begCustomers(String qq, String group) {
        // 解析设置的h5文件
        String data = FileUtil.readFile("templates/keep.html");
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
    }

    private void welcomeCustomer(String qq, List<String> curQQGroups) {
        if (CollectionUtils.isEmpty(curQQGroups)){
            return;
        }

        List<Email> emails = emailDAL.selectInGroups(qq, curQQGroups, EmailType.INCREASE.getCode());
        Set<String> groupCodes = emails.stream()
                .map(Email::getGroupCode)
                .collect(Collectors.toSet());

        String targetCode = null;
        for (String curCode: curQQGroups){
            if (!groupCodes.contains(curCode)){
                targetCode = curCode;
                break;
            }
        }

        if (Strings.isBlank(targetCode)){
            return;
        }

        Email entity = new Email();
        entity.setQqCode(qq);
        entity.setGroupCode(targetCode);
        entity.setMailType(EmailType.INCREASE.getCode());
        emailDAL.insertSelective(entity);

        // 解析设置的h5文件
        String data = FileUtil.readFile("templates/welcome.html");
        // 获取该群的加入链接
        String groupQsig = getGroupQsig(targetCode);
        log.info("[腾讯加群解析测试日志] 二级解析 ret:{}", groupQsig);
        if (StringUtils.isEmpty(groupQsig)) {
            return;
        }

        // 设置上下文 和h5文件交互
        Context context = new Context();
        context.setVariable("qqGroupUrl", groupQsig);
        context.setVariable("code", UUID.randomUUID().toString().replace("-","").substring(0,4));
        context.setVariable("datetime", DateTimeUtil.now());
        context.setVariable("shakespeare", FileUtil.getShakespeare());
        String emailContent = new TemplateEngine().process(data, context);
        // 发送h5邮件
        sendHtmlMail(qq + "@qq.com", "欢迎，您的审核已经通过|Apple|My Office Account", emailContent);
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
            helper.setFrom(new InternetAddress(from, "系统管理员", "UTF-8"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
            logger.info("html邮件发送成功");
        } catch (Exception e) {
            logger.error("发送html邮件时发生异常！", e);
        }
    }
}
