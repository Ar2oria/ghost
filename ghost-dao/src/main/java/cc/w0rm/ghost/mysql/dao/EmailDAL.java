package cc.w0rm.ghost.mysql.dao;

import cc.w0rm.ghost.mysql.mapper.EmailMapper;
import cc.w0rm.ghost.mysql.po.Email;
import cc.w0rm.ghost.mysql.po.EmailExample;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmailDAL extends EmailMapper {
    byte UNDELETED = 0;

    default List<Email> selectInGroups(String qq, List<String> curQQGroups, Integer code){
        EmailExample example = new EmailExample();
        example.createCriteria()
                .andQqCodeEqualTo(qq)
                .andGroupCodeIn(curQQGroups)
                .andMailTypeEqualTo(code)
                .andDeletedEqualTo(UNDELETED);

        return selectByExample(example);
    }
}
