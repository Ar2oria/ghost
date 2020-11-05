package cc.w0rm.ghost.mysql.dao;

import cc.w0rm.ghost.mysql.mapper.QunConfigMapper;
import cc.w0rm.ghost.mysql.po.QunConfig;
import cc.w0rm.ghost.mysql.po.QunConfigExample;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QunConfigDAL extends QunConfigMapper {
    byte UNDELETED = 0;

    default QunConfig selectByGroupCode(String groupCode){
        QunConfigExample example = new QunConfigExample();
        example.createCriteria()
                .andGroupCodeEqualTo(groupCode)
                .andDeletedEqualTo(UNDELETED);

        return selectByExample(example).stream()
                .findFirst().orElse(null);
    }

    default List<QunConfig> selectAllQunConfig(){
        QunConfigExample example = new QunConfigExample();
        example.createCriteria()
                .andDeletedEqualTo(UNDELETED);

        return selectByExample(example);
    }

}
