package ke.co.apollo.health.mapper.health;

import ke.co.apollo.health.common.domain.model.Lead;
import ke.co.apollo.health.common.domain.model.SearchCondition;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

public class LeadSqlProvider {

  public String getLeadsKeySelective(SearchCondition searchCondition, String agentId) {
    SQL sql = new SQL();
    sql.SELECT("lead_id", "first_name", "last_name", "email", "mobile", "birthday", "product",
        "motor", "other", "creat_time", "update_time");
    sql.FROM("lead");
    sql.WHERE(" agent_id = #{agentId, jdbcType=VARCHAR}");
    if (StringUtils.isNotEmpty(searchCondition.getSearchKey())) {
      sql.WHERE("first_name like contact('%', #{searchCondition.searchKey,jdbcType=VARCHAR},'%')");
    }
    return sql.toString();
  }

  public String updateByPrimaryKeySelective(@Param(value = "lead") Lead lead,
      @Param(value = "agentId") String agentId) {
    SQL sql = new SQL();
    sql.UPDATE("lead");
    sql.SET("first_name = #{lead.firstName,jdbcType=VARCHAR}");
    sql.SET("last_name = #{lead.lastName,jdbcType=VARCHAR}");
    sql.SET("email = #{lead.email,jdbcType=VARCHAR}");
    sql.SET("mobile = #{lead.mobile,jdbcType=VARCHAR}");
    sql.SET("birthday = #{lead.dob,jdbcType=TIMESTAMP}");
    sql.SET(
        "interest = #{lead.interest,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.InterestTypeHandler}");
    sql.SET("update_time = #{lead.updateTime,jdbcType=TIMESTAMP}");

    sql.WHERE("lead_id = #{lead.leadId,jdbcType=VARCHAR}");
    sql.WHERE("agent_id = #{agentId,jdbcType=VARCHAR}");

    return sql.toString();
  }
}
