package cc.w0rm.ghost.mysql.po;

import java.util.ArrayList;
import java.util.List;

public class EmailExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EmailExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("_id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("_id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("_id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("_id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("_id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("_id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("_id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("_id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("_id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("_id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("_id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("_id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andQqAccountIsNull() {
            addCriterion("qq_account is null");
            return (Criteria) this;
        }

        public Criteria andQqAccountIsNotNull() {
            addCriterion("qq_account is not null");
            return (Criteria) this;
        }

        public Criteria andQqAccountEqualTo(Integer value) {
            addCriterion("qq_account =", value, "qqAccount");
            return (Criteria) this;
        }

        public Criteria andQqAccountNotEqualTo(Integer value) {
            addCriterion("qq_account <>", value, "qqAccount");
            return (Criteria) this;
        }

        public Criteria andQqAccountGreaterThan(Integer value) {
            addCriterion("qq_account >", value, "qqAccount");
            return (Criteria) this;
        }

        public Criteria andQqAccountGreaterThanOrEqualTo(Integer value) {
            addCriterion("qq_account >=", value, "qqAccount");
            return (Criteria) this;
        }

        public Criteria andQqAccountLessThan(Integer value) {
            addCriterion("qq_account <", value, "qqAccount");
            return (Criteria) this;
        }

        public Criteria andQqAccountLessThanOrEqualTo(Integer value) {
            addCriterion("qq_account <=", value, "qqAccount");
            return (Criteria) this;
        }

        public Criteria andQqAccountIn(List<Integer> values) {
            addCriterion("qq_account in", values, "qqAccount");
            return (Criteria) this;
        }

        public Criteria andQqAccountNotIn(List<Integer> values) {
            addCriterion("qq_account not in", values, "qqAccount");
            return (Criteria) this;
        }

        public Criteria andQqAccountBetween(Integer value1, Integer value2) {
            addCriterion("qq_account between", value1, value2, "qqAccount");
            return (Criteria) this;
        }

        public Criteria andQqAccountNotBetween(Integer value1, Integer value2) {
            addCriterion("qq_account not between", value1, value2, "qqAccount");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsIsNull() {
            addCriterion("joined_groups is null");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsIsNotNull() {
            addCriterion("joined_groups is not null");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsEqualTo(String value) {
            addCriterion("joined_groups =", value, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsNotEqualTo(String value) {
            addCriterion("joined_groups <>", value, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsGreaterThan(String value) {
            addCriterion("joined_groups >", value, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsGreaterThanOrEqualTo(String value) {
            addCriterion("joined_groups >=", value, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsLessThan(String value) {
            addCriterion("joined_groups <", value, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsLessThanOrEqualTo(String value) {
            addCriterion("joined_groups <=", value, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsLike(String value) {
            addCriterion("joined_groups like", value, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsNotLike(String value) {
            addCriterion("joined_groups not like", value, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsIn(List<String> values) {
            addCriterion("joined_groups in", values, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsNotIn(List<String> values) {
            addCriterion("joined_groups not in", values, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsBetween(String value1, String value2) {
            addCriterion("joined_groups between", value1, value2, "joinedGroups");
            return (Criteria) this;
        }

        public Criteria andJoinedGroupsNotBetween(String value1, String value2) {
            addCriterion("joined_groups not between", value1, value2, "joinedGroups");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}