package cc.w0rm.ghost.mysql.mapper;

import cc.w0rm.ghost.mysql.po.Commodity;
import cc.w0rm.ghost.mysql.po.CommodityExample;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

@Mapper
public interface CommodityMapper {
    @SelectProvider(type=CommoditySqlProvider.class, method="countByExample")
    long countByExample(CommodityExample example);

    @DeleteProvider(type=CommoditySqlProvider.class, method="deleteByExample")
    int deleteByExample(CommodityExample example);

    @Delete({
        "delete from commodity",
        "where _id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into commodity (_id, name, ",
        "sku, final_price, ",
        "detail, commodity_id, ",
        "insert_time)",
        "values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
        "#{sku,jdbcType=VARCHAR}, #{finalPrice,jdbcType=DOUBLE}, ",
        "#{detail,jdbcType=VARCHAR}, #{commodityId,jdbcType=VARCHAR}, ",
        "#{insertTime,jdbcType=LONGVARCHAR})"
    })
    int insert(Commodity record);

    @InsertProvider(type=CommoditySqlProvider.class, method="insertSelective")
    int insertSelective(Commodity record);

    @SelectProvider(type=CommoditySqlProvider.class, method="selectByExampleWithBLOBs")
    @Results({
        @Result(column="_id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="sku", property="sku", jdbcType=JdbcType.VARCHAR),
        @Result(column="final_price", property="finalPrice", jdbcType=JdbcType.DOUBLE),
        @Result(column="detail", property="detail", jdbcType=JdbcType.VARCHAR),
        @Result(column="commodity_id", property="commodityId", jdbcType=JdbcType.VARCHAR),
        @Result(column="insert_time", property="insertTime", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<Commodity> selectByExampleWithBLOBs(CommodityExample example);

    @SelectProvider(type=CommoditySqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="_id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="sku", property="sku", jdbcType=JdbcType.VARCHAR),
        @Result(column="final_price", property="finalPrice", jdbcType=JdbcType.DOUBLE),
        @Result(column="detail", property="detail", jdbcType=JdbcType.VARCHAR),
        @Result(column="commodity_id", property="commodityId", jdbcType=JdbcType.VARCHAR)
    })
    List<Commodity> selectByExample(CommodityExample example);

    @Select({
        "select",
        "_id, name, sku, final_price, detail, commodity_id, insert_time",
        "from commodity",
        "where _id = #{id,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="_id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="sku", property="sku", jdbcType=JdbcType.VARCHAR),
        @Result(column="final_price", property="finalPrice", jdbcType=JdbcType.DOUBLE),
        @Result(column="detail", property="detail", jdbcType=JdbcType.VARCHAR),
        @Result(column="commodity_id", property="commodityId", jdbcType=JdbcType.VARCHAR),
        @Result(column="insert_time", property="insertTime", jdbcType=JdbcType.LONGVARCHAR)
    })
    Commodity selectByPrimaryKey(Integer id);

    @UpdateProvider(type=CommoditySqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") Commodity record, @Param("example") CommodityExample example);

    @UpdateProvider(type=CommoditySqlProvider.class, method="updateByExampleWithBLOBs")
    int updateByExampleWithBLOBs(@Param("record") Commodity record, @Param("example") CommodityExample example);

    @UpdateProvider(type=CommoditySqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") Commodity record, @Param("example") CommodityExample example);

    @UpdateProvider(type=CommoditySqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Commodity record);

    @Update({
        "update commodity",
        "set name = #{name,jdbcType=VARCHAR},",
          "sku = #{sku,jdbcType=VARCHAR},",
          "final_price = #{finalPrice,jdbcType=DOUBLE},",
          "detail = #{detail,jdbcType=VARCHAR},",
          "commodity_id = #{commodityId,jdbcType=VARCHAR},",
          "insert_time = #{insertTime,jdbcType=LONGVARCHAR}",
        "where _id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKeyWithBLOBs(Commodity record);

    @Update({
        "update commodity",
        "set name = #{name,jdbcType=VARCHAR},",
          "sku = #{sku,jdbcType=VARCHAR},",
          "final_price = #{finalPrice,jdbcType=DOUBLE},",
          "detail = #{detail,jdbcType=VARCHAR},",
          "commodity_id = #{commodityId,jdbcType=VARCHAR}",
        "where _id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Commodity record);
}