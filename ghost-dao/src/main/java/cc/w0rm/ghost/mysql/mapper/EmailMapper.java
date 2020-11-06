package cc.w0rm.ghost.mysql.mapper;

import cc.w0rm.ghost.mysql.po.Email;
import cc.w0rm.ghost.mysql.po.EmailExample;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
@Mapper
public interface EmailMapper {
    @SelectProvider(type=EmailSqlProvider.class, method="countByExample")
    long countByExample(EmailExample example);

    @DeleteProvider(type=EmailSqlProvider.class, method="deleteByExample")
    int deleteByExample(EmailExample example);

    @Delete({
        "delete from email",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into email (id, qq_code, ",
        "group_code, mail_type, ",
        "deleted, created_at, ",
        "updated_at)",
        "values (#{id,jdbcType=INTEGER}, #{qqCode,jdbcType=VARCHAR}, ",
        "#{groupCode,jdbcType=VARCHAR}, #{mailType,jdbcType=INTEGER}, ",
        "#{deleted,jdbcType=TINYINT}, #{createdAt,jdbcType=TIMESTAMP}, ",
        "#{updatedAt,jdbcType=TIMESTAMP})"
    })
    int insert(Email record);

    @InsertProvider(type=EmailSqlProvider.class, method="insertSelective")
    int insertSelective(Email record);

    @SelectProvider(type=EmailSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="qq_code", property="qqCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="group_code", property="groupCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="mail_type", property="mailType", jdbcType=JdbcType.INTEGER),
        @Result(column="deleted", property="deleted", jdbcType=JdbcType.TINYINT),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<Email> selectByExample(EmailExample example);

    @Select({
        "select",
        "id, qq_code, group_code, mail_type, deleted, created_at, updated_at",
        "from email",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="qq_code", property="qqCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="group_code", property="groupCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="mail_type", property="mailType", jdbcType=JdbcType.INTEGER),
        @Result(column="deleted", property="deleted", jdbcType=JdbcType.TINYINT),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    Email selectByPrimaryKey(Integer id);

    @UpdateProvider(type=EmailSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") Email record, @Param("example") EmailExample example);

    @UpdateProvider(type=EmailSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") Email record, @Param("example") EmailExample example);

    @UpdateProvider(type=EmailSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Email record);

    @Update({
        "update email",
        "set qq_code = #{qqCode,jdbcType=VARCHAR},",
          "group_code = #{groupCode,jdbcType=VARCHAR},",
          "mail_type = #{mailType,jdbcType=INTEGER},",
          "deleted = #{deleted,jdbcType=TINYINT},",
          "created_at = #{createdAt,jdbcType=TIMESTAMP},",
          "updated_at = #{updatedAt,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Email record);
}