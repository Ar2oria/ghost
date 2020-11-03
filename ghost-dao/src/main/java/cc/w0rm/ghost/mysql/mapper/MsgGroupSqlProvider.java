package cc.w0rm.ghost.mysql.mapper;

import cc.w0rm.ghost.mysql.po.MsgGroup;
import cc.w0rm.ghost.mysql.po.MsgGroupExample.Criteria;
import cc.w0rm.ghost.mysql.po.MsgGroupExample.Criterion;
import cc.w0rm.ghost.mysql.po.MsgGroupExample;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class MsgGroupSqlProvider {

    public String countByExample(MsgGroupExample example) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM("msg_group");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String deleteByExample(MsgGroupExample example) {
        SQL sql = new SQL();
        sql.DELETE_FROM("msg_group");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String insertSelective(MsgGroup record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("msg_group");
        
        if (record.getId() != null) {
            sql.VALUES("_id", "#{id,jdbcType=INTEGER}");
        }
        
        if (record.getCommodityId() != null) {
            sql.VALUES("commodity_id", "#{commodityId,jdbcType=VARCHAR}");
        }
        
        if (record.getGroup() != null) {
            sql.VALUES("group", "#{group,jdbcType=BIGINT}");
        }
        
        if (record.getInsertTime() != null) {
            sql.VALUES("insert_time", "#{insertTime,jdbcType=LONGVARCHAR}");
        }
        
        return sql.toString();
    }

    public String selectByExampleWithBLOBs(MsgGroupExample example) {
        SQL sql = new SQL();
        if (example != null && example.isDistinct()) {
            sql.SELECT_DISTINCT("_id");
        } else {
            sql.SELECT("_id");
        }
        sql.SELECT("commodity_id");
        sql.SELECT("group");
        sql.SELECT("insert_time");
        sql.FROM("msg_group");
        applyWhere(sql, example, false);
        
        if (example != null && example.getOrderByClause() != null) {
            sql.ORDER_BY(example.getOrderByClause());
        }
        
        return sql.toString();
    }

    public String selectByExample(MsgGroupExample example) {
        SQL sql = new SQL();
        if (example != null && example.isDistinct()) {
            sql.SELECT_DISTINCT("_id");
        } else {
            sql.SELECT("_id");
        }
        sql.SELECT("commodity_id");
        sql.SELECT("group");
        sql.FROM("msg_group");
        applyWhere(sql, example, false);
        
        if (example != null && example.getOrderByClause() != null) {
            sql.ORDER_BY(example.getOrderByClause());
        }
        
        return sql.toString();
    }

    public String updateByExampleSelective(Map<String, Object> parameter) {
        MsgGroup record = (MsgGroup) parameter.get("record");
        MsgGroupExample example = (MsgGroupExample) parameter.get("example");
        
        SQL sql = new SQL();
        sql.UPDATE("msg_group");
        
        if (record.getId() != null) {
            sql.SET("_id = #{record.id,jdbcType=INTEGER}");
        }
        
        if (record.getCommodityId() != null) {
            sql.SET("commodity_id = #{record.commodityId,jdbcType=VARCHAR}");
        }
        
        if (record.getGroup() != null) {
            sql.SET("group = #{record.group,jdbcType=BIGINT}");
        }
        
        if (record.getInsertTime() != null) {
            sql.SET("insert_time = #{record.insertTime,jdbcType=LONGVARCHAR}");
        }
        
        applyWhere(sql, example, true);
        return sql.toString();
    }

    public String updateByExampleWithBLOBs(Map<String, Object> parameter) {
        SQL sql = new SQL();
        sql.UPDATE("msg_group");
        
        sql.SET("_id = #{record.id,jdbcType=INTEGER}");
        sql.SET("commodity_id = #{record.commodityId,jdbcType=VARCHAR}");
        sql.SET("group = #{record.group,jdbcType=BIGINT}");
        sql.SET("insert_time = #{record.insertTime,jdbcType=LONGVARCHAR}");
        
        MsgGroupExample example = (MsgGroupExample) parameter.get("example");
        applyWhere(sql, example, true);
        return sql.toString();
    }

    public String updateByExample(Map<String, Object> parameter) {
        SQL sql = new SQL();
        sql.UPDATE("msg_group");
        
        sql.SET("_id = #{record.id,jdbcType=INTEGER}");
        sql.SET("commodity_id = #{record.commodityId,jdbcType=VARCHAR}");
        sql.SET("group = #{record.group,jdbcType=BIGINT}");
        
        MsgGroupExample example = (MsgGroupExample) parameter.get("example");
        applyWhere(sql, example, true);
        return sql.toString();
    }

    public String updateByPrimaryKeySelective(MsgGroup record) {
        SQL sql = new SQL();
        sql.UPDATE("msg_group");
        
        if (record.getCommodityId() != null) {
            sql.SET("commodity_id = #{commodityId,jdbcType=VARCHAR}");
        }
        
        if (record.getGroup() != null) {
            sql.SET("group = #{group,jdbcType=BIGINT}");
        }
        
        if (record.getInsertTime() != null) {
            sql.SET("insert_time = #{insertTime,jdbcType=LONGVARCHAR}");
        }
        
        sql.WHERE("_id = #{id,jdbcType=INTEGER}");
        
        return sql.toString();
    }

    protected void applyWhere(SQL sql, MsgGroupExample example, boolean includeExamplePhrase) {
        if (example == null) {
            return;
        }
        
        String parmPhrase1;
        String parmPhrase1_th;
        String parmPhrase2;
        String parmPhrase2_th;
        String parmPhrase3;
        String parmPhrase3_th;
        if (includeExamplePhrase) {
            parmPhrase1 = "%s #{example.oredCriteria[%d].allCriteria[%d].value}";
            parmPhrase1_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
            parmPhrase2 = "%s #{example.oredCriteria[%d].allCriteria[%d].value} and #{example.oredCriteria[%d].criteria[%d].secondValue}";
            parmPhrase2_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{example.oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
            parmPhrase3 = "#{example.oredCriteria[%d].allCriteria[%d].value[%d]}";
            parmPhrase3_th = "#{example.oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
        } else {
            parmPhrase1 = "%s #{oredCriteria[%d].allCriteria[%d].value}";
            parmPhrase1_th = "%s #{oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
            parmPhrase2 = "%s #{oredCriteria[%d].allCriteria[%d].value} and #{oredCriteria[%d].criteria[%d].secondValue}";
            parmPhrase2_th = "%s #{oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
            parmPhrase3 = "#{oredCriteria[%d].allCriteria[%d].value[%d]}";
            parmPhrase3_th = "#{oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
        }
        
        StringBuilder sb = new StringBuilder();
        List<Criteria> oredCriteria = example.getOredCriteria();
        boolean firstCriteria = true;
        for (int i = 0; i < oredCriteria.size(); i++) {
            Criteria criteria = oredCriteria.get(i);
            if (criteria.isValid()) {
                if (firstCriteria) {
                    firstCriteria = false;
                } else {
                    sb.append(" or ");
                }
                
                sb.append('(');
                List<Criterion> criterions = criteria.getAllCriteria();
                boolean firstCriterion = true;
                for (int j = 0; j < criterions.size(); j++) {
                    Criterion criterion = criterions.get(j);
                    if (firstCriterion) {
                        firstCriterion = false;
                    } else {
                        sb.append(" and ");
                    }
                    
                    if (criterion.isNoValue()) {
                        sb.append(criterion.getCondition());
                    } else if (criterion.isSingleValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase1, criterion.getCondition(), i, j));
                        } else {
                            sb.append(String.format(parmPhrase1_th, criterion.getCondition(), i, j,criterion.getTypeHandler()));
                        }
                    } else if (criterion.isBetweenValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase2, criterion.getCondition(), i, j, i, j));
                        } else {
                            sb.append(String.format(parmPhrase2_th, criterion.getCondition(), i, j, criterion.getTypeHandler(), i, j, criterion.getTypeHandler()));
                        }
                    } else if (criterion.isListValue()) {
                        sb.append(criterion.getCondition());
                        sb.append(" (");
                        List<?> listItems = (List<?>) criterion.getValue();
                        boolean comma = false;
                        for (int k = 0; k < listItems.size(); k++) {
                            if (comma) {
                                sb.append(", ");
                            } else {
                                comma = true;
                            }
                            if (criterion.getTypeHandler() == null) {
                                sb.append(String.format(parmPhrase3, i, j, k));
                            } else {
                                sb.append(String.format(parmPhrase3_th, i, j, k, criterion.getTypeHandler()));
                            }
                        }
                        sb.append(')');
                    }
                }
                sb.append(')');
            }
        }
        
        if (sb.length() > 0) {
            sql.WHERE(sb.toString());
        }
    }
}