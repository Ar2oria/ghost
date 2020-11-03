package cc.w0rm.ghost.mysql.mapper;

import cc.w0rm.ghost.mysql.po.MsgGroup;
import cc.w0rm.ghost.mysql.po.MsgGroupExample;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface MsgGroupMapper {
    
    @SelectProvider(type = MsgGroupSqlProvider.class, method = "countByExample")
    long countByExample(MsgGroupExample example);
    
    @DeleteProvider(type = MsgGroupSqlProvider.class, method = "deleteByExample")
    int deleteByExample(MsgGroupExample example);
    
    @Delete({"delete from msg_group", "where _id = #{id,jdbcType=INTEGER}"})
    int deleteByPrimaryKey(Integer id);
    
    @Insert({"insert into msg_group (_id, commodity_id, ", "_group, insert_time)",
        "values (#{id,jdbcType=INTEGER}, " + "#{commodityId,jdbcType=VARCHAR}, ", "#{group,jdbcType=BIGINT}, " +
        "#{insertTime,jdbcType=LONGVARCHAR})"})
    int insert(MsgGroup record);
    
    @InsertProvider(type = MsgGroupSqlProvider.class, method = "insertSelective")
    int insertSelective(MsgGroup record);
    
    @SelectProvider(type = MsgGroupSqlProvider.class, method = "selectByExampleWithBLOBs")
    @Results({@Result(column = "_id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column =
        "commodity_id", property = "commodityId", jdbcType = JdbcType.VARCHAR), @Result(column = "_group", property =
        "group", jdbcType = JdbcType.BIGINT), @Result(column = "insert_time", property = "insertTime", jdbcType =
        JdbcType.LONGVARCHAR)})
    List<MsgGroup> selectByExampleWithBLOBs(MsgGroupExample example);
    
    @SelectProvider(type = MsgGroupSqlProvider.class, method = "selectByExample")
    @Results({@Result(column = "_id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column =
        "commodity_id", property = "commodityId", jdbcType = JdbcType.VARCHAR), @Result(column = "_group", property =
        "group", jdbcType = JdbcType.BIGINT)})
    List<MsgGroup> selectByExample(MsgGroupExample example);
    
    @Select({"select", "_id, commodity_id, _group, insert_time", "from msg_group", "where _id = #{id," +
        "jdbcType=INTEGER}"})
    @Results({@Result(column = "_id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column =
        "commodity_id", property = "commodityId", jdbcType = JdbcType.VARCHAR), @Result(column = "_group", property =
        "group", jdbcType = JdbcType.BIGINT), @Result(column = "insert_time", property = "insertTime", jdbcType =
        JdbcType.LONGVARCHAR)})
    MsgGroup selectByPrimaryKey(Integer id);
    
    @UpdateProvider(type = MsgGroupSqlProvider.class, method = "updateByExampleSelective")
    int updateByExampleSelective(@Param("record") MsgGroup record, @Param("example") MsgGroupExample example);
    
    @UpdateProvider(type = MsgGroupSqlProvider.class, method = "updateByExampleWithBLOBs")
    int updateByExampleWithBLOBs(@Param("record") MsgGroup record, @Param("example") MsgGroupExample example);
    
    @UpdateProvider(type = MsgGroupSqlProvider.class, method = "updateByExample")
    int updateByExample(@Param("record") MsgGroup record, @Param("example") MsgGroupExample example);
    
    @UpdateProvider(type = MsgGroupSqlProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(MsgGroup record);
    
    @Update({"update msg_group", "set commodity_id = #{commodityId,jdbcType=VARCHAR},", "_group = #{_group," +
        "jdbcType=BIGINT},", "insert_time = #{insertTime,jdbcType=LONGVARCHAR}", "where _id = #{id,jdbcType=INTEGER}"})
    int updateByPrimaryKeyWithBLOBs(MsgGroup record);
    
    @Update({"update msg_group", "set commodity_id = #{commodityId,jdbcType=VARCHAR},", "_group = #{_group," +
        "jdbcType=BIGINT}", "where _id = #{id,jdbcType=INTEGER}"})
    int updateByPrimaryKey(MsgGroup record);
    
    @Select({"select", "_id, commodity_id, _group, insert_time", "from msg_group", "where commodity_id = #{id," +
        "jdbcType=LONGVARCHAR}"})
    @Results({@Result(column = "_id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column =
        "commodity_id", property = "commodityId", jdbcType = JdbcType.VARCHAR), @Result(column = "_group", property =
        "group", jdbcType = JdbcType.BIGINT), @Result(column = "insert_time", property = "insertTime", jdbcType =
        JdbcType.LONGVARCHAR)})
    List<MsgGroup> selectByCommdityId(String id);
    
    @Insert({"insert into msg_group (_id,commodity_id,_group,insert_time)", "values (#{id,jdbcType=INTEGER}, " +
        "#{commodityId," + "jdbcType=LONGVARCHAR}, ", "#{group,jdbcType=BIGINT},#{insertTime,jdbcType=LONGVARCHAR}) "
        + "on " + "duplicate key update _id = #{id,jdbcType=INTEGER}," + "commodity_id=#{commodityId," +
        "jdbcType=VARCHAR}," + "_group=#{group,jdbcType=BIGINT},insert_time = #{insertTime,jdbcType=LONGVARCHAR}"})
    int insertOrUpdate(MsgGroup record);
}