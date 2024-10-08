package ke.co.apollo.health.mapper.health;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Interest;
import ke.co.apollo.health.common.domain.model.Lead;
import ke.co.apollo.health.common.domain.model.LeadSearchCondition;
import ke.co.apollo.health.mapper.health.typehandler.InterestTypeHandler;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface LeadMapper {

  @Insert({
      "insert into lead (lead_id, first_name, last_name, email, mobile, birthday, interest, create_time, update_time, agent_id)",
      "values (#{lead.leadId,jdbcType=VARCHAR}, #{lead.firstName,jdbcType=VARCHAR}, #{lead.lastName,jdbcType=VARCHAR}, #{lead.email,jdbcType=VARCHAR}, ",
      "#{lead.mobile,jdbcType=VARCHAR}, #{lead.dob,jdbcType=TIMESTAMP}, #{lead.interest,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.InterestTypeHandler}, ",
      "#{lead.createTime,jdbcType=TIMESTAMP}, #{lead.updateTime,jdbcType=TIMESTAMP},",
      "#{agentId,jdbcType=VARCHAR })"
  })
  @SelectKey(keyProperty = "lead.leadId", keyColumn = "lead_id", resultType = String.class, before = true, statement = "select md5(uuid())")
  int insert(@Param(value = "lead") Lead lead, @Param(value = "agentId") String agentId);

  @Update({
      "update lead",
      "set first_name = #{lead.firstName,jdbcType=VARCHAR},",
      "last_name = #{lead.lastName,jdbcType=VARCHAR},",
      "email = #{lead.email,jdbcType=VARCHAR},",
      "mobile = #{lead.mobile,jdbcType=VARCHAR},",
      "birthday = #{lead.dob,jdbcType=TIMESTAMP},",
      "interest = #{lead.interest,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.InterestTypeHandler},",
      "update_time = #{lead.updateTime,jdbcType=TIMESTAMP}",
      "where lead_id = #{lead.leadId,jdbcType=VARCHAR}",
      "and agent_id = #{agentId,jdbcType=VARCHAR}"
  })
  int updateLead(@Param(value = "lead") Lead lead, @Param(value = "agentId") String agentId);

  @UpdateProvider(type = LeadSqlProvider.class, method = "updateByPrimaryKeySelective")
  int update(@Param(value = "lead") Lead lead, @Param(value = "agentId") String agentId);

  @Delete({
      "delete from lead",
      "where lead_id = #{leadId,jdbcType=VARCHAR}",
      "and agent_id = #{agentId,jdbcType=VARCHAR}"
  })
  int delete(@Param(value = "leadId") String leadId, @Param(value = "agentId") String agentId);

  @Select("<script> "
      + " select lead_id, first_name, last_name, email, mobile, birthday, interest, create_time, update_time"
      + " from lead"
      + " where agent_id = #{agentId,jdbcType=VARCHAR} "
      + " <if test='leadSearchCondition.name != null'> and (first_name like concat('%', #{leadSearchCondition.name,jdbcType=VARCHAR},'%')"
      + "   or last_name like concat('%', #{leadSearchCondition.name,jdbcType=VARCHAR},'%')) </if> "
      + " <if test='leadSearchCondition.product != null'> and JSON_LENGTH(interest,#{leadSearchCondition.product}) > 0 </if> "
      + "  order by ${leadSearchCondition.orderbyCause} "
      + " </script> ")
  @Results({
      @Result(column = "lead_id", property = "leadId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
      @Result(column = "mobile", property = "mobile", jdbcType = JdbcType.VARCHAR),
      @Result(column = "birthday", property = "dob", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "interest", property = "interest", jdbcType = JdbcType.VARCHAR, javaType = Interest.class, typeHandler = InterestTypeHandler.class),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP)
  })
  List<Lead> getLeads(@Param(value = "leadSearchCondition") LeadSearchCondition leadSearchCondition,
      @Param(value = "agentId") String agentId);

  @Select({
      "select count(lead_id) from lead",
      "where agent_id = #{agentId,jdbcType=VARCHAR}"
  })
  int getTotalLeads(@Param(value = "agentId") String agentId);
}
