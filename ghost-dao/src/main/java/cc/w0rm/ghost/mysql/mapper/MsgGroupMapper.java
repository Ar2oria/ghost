package cc.w0rm.ghost.mysql.mapper;

import cc.w0rm.ghost.mysql.po.MsgGroup;
import cc.w0rm.ghost.mysql.po.MsgGroupExample;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

@Mapper
public interface MsgGroupMapper {
    
    @SelectProvider(type = MsgGroupSqlProvider.class, method = "countByExample")
    long countByExample(MsgGroupExample example);
    
    @DeleteProvider(type = MsgGroupSqlProvider.class, method = "deleteByExample")
    int deleteByExample(MsgGroupExample example);
    
    @Delete({"delete from msg_group", "where _id = #{id,jdbcType=INTEGER}"})
    int deleteByPrimaryKey(Integer id);
    
    @Insert({"insert into msg_group (_id, commodity_id, ", "groups)", "values (#{id,jdbcType=INTEGER}, #{commodityId," +
        "jdbcType=VARCHAR}, ", "#{groups,jdbcType=VARCHAR})"})
    int insert(MsgGroup record);
    
    @InsertProvider(type = MsgGroupSqlProvider.class, method = "insertSelective")
    int insertSelective(MsgGroup record);
    
    @SelectProvider(type = MsgGroupSqlProvider.class, method = "selectByExample")
    @Results({@Result(column = "_id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column =
        "commodity_id", property = "commodityId", jdbcType = JdbcType.VARCHAR), @Result(column = "groups", property =
        "groups", jdbcType = JdbcType.VARCHAR)})
    List<MsgGroup> selectByExample(MsgGroupExample example);
    
    @Select({"select", "_id, commodity_id, groups", "from msg_group", "where _id = #{id,jdbcType=INTEGER}"})
    @Results({@Result(column = "_id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column =
        "commodity_id", property = "commodityId", jdbcType = JdbcType.VARCHAR), @Result(column = "groups", property =
        "groups", jdbcType = JdbcType.VARCHAR)})
    MsgGroup selectByPrimaryKey(Integer id);
    
    @UpdateProvider(type = MsgGroupSqlProvider.class, method = "updateByExampleSelective")
    int updateByExampleSelective(@Param("record") MsgGroup record, @Param("example") MsgGroupExample example);
    
    @UpdateProvider(type = MsgGroupSqlProvider.class, method = "updateByExample")
    int updateByExample(@Param("record") MsgGroup record, @Param("example") MsgGroupExample example);
    
    @UpdateProvider(type = MsgGroupSqlProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(MsgGroup record);
    
    @Update({"update msg_group", "set commodity_id = #{commodityId,jdbcType=VARCHAR},", "groups = #{groups," +
        "jdbcType=VARCHAR}", "where _id = #{id,jdbcType=INTEGER}"})
    int updateByPrimaryKey(MsgGroup record);
    
    @Results({@Result(column = "_id", property = "id", jdbcType = JdbcType.VARCHAR, id = true), @Result(column =
        "groups", property = "groups", jdbcType = JdbcType.VARCHAR), @Result(column = "commodity_id", property =
        "commodityId", jdbcType = JdbcType.LONGVARCHAR)})
    MsgGroup selectByCommdityId(String id);
    
    @Insert({"insert into msg_group (_id, commodity_id, ", "groups)", "values (#{id,jdbcType=INTEGER}, #{commodityId," +
        "jdbcType=VARCHAR}, ", "#{groups,jdbcType=VARCHAR}) on duplicate key update _id = #{id,jdbcType=INTEGER}," +
        "commodity_id=#{commodityId,jdbcType=VARCHAR},groups=#{groups,jdbcType=VARCHAR}"})
    int insertOrUpdate(MsgGroup record);
}