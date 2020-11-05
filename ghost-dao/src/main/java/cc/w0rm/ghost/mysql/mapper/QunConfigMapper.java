package cc.w0rm.ghost.mysql.mapper;

import cc.w0rm.ghost.mysql.po.QunConfig;
import cc.w0rm.ghost.mysql.po.QunConfigExample;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
@Mapper
public interface QunConfigMapper {
    @SelectProvider(type=QunConfigSqlProvider.class, method="countByExample")
    long countByExample(QunConfigExample example);

    @DeleteProvider(type=QunConfigSqlProvider.class, method="deleteByExample")
    int deleteByExample(QunConfigExample example);

    @Delete({
        "delete from qun_config",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into qun_config (id, qq_code, ",
        "group_code, group_key, ",
        "deleted, created_at, ",
        "updated_at)",
        "values (#{id,jdbcType=INTEGER}, #{qqCode,jdbcType=VARCHAR}, ",
        "#{groupCode,jdbcType=VARCHAR}, #{groupKey,jdbcType=VARCHAR}, ",
        "#{deleted,jdbcType=TINYINT}, #{createdAt,jdbcType=TIMESTAMP}, ",
        "#{updatedAt,jdbcType=TIMESTAMP})"
    })
    int insert(QunConfig record);

    @InsertProvider(type=QunConfigSqlProvider.class, method="insertSelective")
    int insertSelective(QunConfig record);

    @SelectProvider(type=QunConfigSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="qq_code", property="qqCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="group_code", property="groupCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="group_key", property="groupKey", jdbcType=JdbcType.VARCHAR),
        @Result(column="deleted", property="deleted", jdbcType=JdbcType.TINYINT),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<QunConfig> selectByExample(QunConfigExample example);

    @Select({
        "select",
        "id, qq_code, group_code, group_key, deleted, created_at, updated_at",
        "from qun_config",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="qq_code", property="qqCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="group_code", property="groupCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="group_key", property="groupKey", jdbcType=JdbcType.VARCHAR),
        @Result(column="deleted", property="deleted", jdbcType=JdbcType.TINYINT),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    QunConfig selectByPrimaryKey(Integer id);

    @UpdateProvider(type=QunConfigSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") QunConfig record, @Param("example") QunConfigExample example);

    @UpdateProvider(type=QunConfigSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") QunConfig record, @Param("example") QunConfigExample example);

    @UpdateProvider(type=QunConfigSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(QunConfig record);

    @Update({
        "update qun_config",
        "set qq_code = #{qqCode,jdbcType=VARCHAR},",
          "group_code = #{groupCode,jdbcType=VARCHAR},",
          "group_key = #{groupKey,jdbcType=VARCHAR},",
          "deleted = #{deleted,jdbcType=TINYINT},",
          "created_at = #{createdAt,jdbcType=TIMESTAMP},",
          "updated_at = #{updatedAt,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(QunConfig record);
}