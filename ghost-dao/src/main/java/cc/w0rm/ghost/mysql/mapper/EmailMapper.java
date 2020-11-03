package cc.w0rm.ghost.mysql.mapper;

import cc.w0rm.ghost.mysql.po.Email;
import cc.w0rm.ghost.mysql.po.EmailExample;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

@Mapper
public interface EmailMapper {
    
    @SelectProvider(type = EmailSqlProvider.class, method = "countByExample")
    long countByExample(EmailExample example);
    
    @DeleteProvider(type = EmailSqlProvider.class, method = "deleteByExample")
    int deleteByExample(EmailExample example);
    
    @Delete({"delete from email", "where _id = #{id,jdbcType=INTEGER}"})
    int deleteByPrimaryKey(Integer id);
    
    @Insert({"insert into email (_id, qq_account, ", "joined_groups)",
        "values (#{id,jdbcType=INTEGER}, #{qqAccount," + "jdbcType=BIGINT}, ", "#{joinedGroups,jdbcType=VARCHAR})"})
    int insert(Email record);
    
    @InsertProvider(type = EmailSqlProvider.class, method = "insertSelective")
    int insertSelective(Email record);
    
    @SelectProvider(type = EmailSqlProvider.class, method = "selectByExample")
    @Results({@Result(column = "_id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column =
        "qq_account", property = "qqAccount", jdbcType = JdbcType.BIGINT), @Result(column = "joined_groups",
        property = "joinedGroups", jdbcType = JdbcType.VARCHAR)})
    List<Email> selectByExample(EmailExample example);
    
    @Select({"select", "_id, qq_account, joined_groups", "from email", "where _id = #{id,jdbcType=INTEGER}"})
    @Results({@Result(column = "_id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column =
        "qq_account", property = "qqAccount", jdbcType = JdbcType.BIGINT), @Result(column = "joined_groups",
        property = "joinedGroups", jdbcType = JdbcType.VARCHAR)})
    Email selectByPrimaryKey(Integer id);
    
    @UpdateProvider(type = EmailSqlProvider.class, method = "updateByExampleSelective")
    int updateByExampleSelective(@Param("record") Email record, @Param("example") EmailExample example);
    
    @UpdateProvider(type = EmailSqlProvider.class, method = "updateByExample")
    int updateByExample(@Param("record") Email record, @Param("example") EmailExample example);
    
    @UpdateProvider(type = EmailSqlProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Email record);
    
    @Update({"update email", "set qq_account = #{qqAccount,jdbcType=BIGINT},", "joined_groups = #{joinedGroups," +
        "jdbcType=VARCHAR}", "where _id = #{id,jdbcType=INTEGER}"})
    int updateByPrimaryKey(Email record);
    
    @Select({"select", "_id, qq_account, joined_groups", "from email", "where qq_account = #{qq,jdbcType=BIGINT}"})
    @Results({@Result(column = "_id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column =
        "qq_account", property = "qqAccount", jdbcType = JdbcType.BIGINT), @Result(column = "joined_groups",
        property = "joinedGroups", jdbcType = JdbcType.VARCHAR)})
    Email selectByQQAcount(Long qq);
    
    @Insert({"insert into email (_id, qq_account, ", "joined_groups)",
        "values (#{id,jdbcType=INTEGER}, #{qqAccount," + "jdbcType=BIGINT}, ", "#{joinedGroups,jdbcType=VARCHAR}) on " +
        "duplicate key update _id=#{id,jdbcType=INTEGER}, " + "qq_account=#{qqAccount,jdbcType=BIGINT}," +
        "joined_groups=#{joinedGroups,jdbcType=VARCHAR}"})
    int insertOrUpdate(Email record);
}