package cc.w0rm.ghost.mysql.dao;

import cc.w0rm.ghost.mysql.mapper.EmailMapper;
import cc.w0rm.ghost.mysql.po.Email;
import com.alibaba.druid.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class EmailDALImpl {
    
    @Resource
    private EmailMapper emailMapper;
    
    public Set<String> getTargetQQJoinedGroups(String qq) {
        if (!StringUtils.isNumber(qq)) {
            return new HashSet<>();
        }
        try {
            Email email = emailMapper.selectByQQAcount(Integer.parseInt(qq));
            if (!StringUtils.isEmpty(email.getJoinedGroups())) {
                return new HashSet<>(Arrays.asList(email.getJoinedGroups().split(",")));
            }
        } catch (Exception e) {
            log.error("email信息查询失败 查询qq信息:{}", qq);
        }
        return new HashSet<>();
    }
    
    public void addEmail(Email email, Set<String> joinedGroups) {
        email.setJoinedGroups(String.join(",", joinedGroups));
        try {
            emailMapper.insertOrUpdate(email);
        } catch (Exception e) {
            log.error("email信息添加失败 查询qq信息:{}", email.toString());
        }
    }
}
