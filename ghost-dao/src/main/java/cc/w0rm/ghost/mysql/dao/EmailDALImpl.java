package cc.w0rm.ghost.mysql.dao;

import cc.w0rm.ghost.mysql.mapper.EmailMapper;
import cc.w0rm.ghost.mysql.po.Email;
import com.alibaba.druid.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class EmailDALImpl {

    @Resource
    private EmailMapper emailMapper;

    public Email getEmail(String qq) {
        if (!StringUtils.isNumber(qq)) {
            return null;
        }
        try {
            return emailMapper.selectByQQAcount(Long.parseLong(qq));
        } catch (Exception e) {
            log.error("email信息查询失败 查询qq信息:{}", qq, e);
        }
        return null;
    }

    public void addEmail(Email email, Set<String> joinedGroups) {
        email.setJoinedGroups(String.join(",", joinedGroups));
        try {
            if (Objects.isNull(email.getId())) {
                emailMapper.insertSelective(email);
            } else {
                emailMapper.updateByPrimaryKeySelective(email);
            }
        } catch (Exception e) {
            log.error("email信息添加失败 查询qq信息:{}", email.toString(), e);
        }
    }
}
