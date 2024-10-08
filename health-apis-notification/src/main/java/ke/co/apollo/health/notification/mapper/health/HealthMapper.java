package ke.co.apollo.health.notification.mapper.health;

import java.util.List;
import ke.co.apollo.health.common.domain.model.CustomerPolicyCache;
import ke.co.apollo.health.common.domain.model.Policy;
import ke.co.apollo.health.common.domain.model.PolicyNotificationTask;
import ke.co.apollo.health.notification.mapper.health.typehandler.PolicyListTypeHandler;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface HealthMapper {

  @Insert({
      "insert into policy_notification_task (task_id, type, subtype, destination, subject, text, schedule_time, status, failure_number, policy_number, category, create_time)",
      "values (#{taskId,jdbcType=VARCHAR},#{type,jdbcType=VARCHAR}, #{subtype,jdbcType=VARCHAR}, #{destination,jdbcType=VARCHAR}, #{subject,jdbcType=VARCHAR}, #{text,jdbcType=VARCHAR}, #{scheduleTime,jdbcType=VARCHAR},",
      "#{status,jdbcType=VARCHAR}, #{failureNumber,jdbcType=INTEGER}, #{policyNumber,jdbcType=VARCHAR},  #{category,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP})"
  })
  @SelectKey(keyProperty = "taskId", keyColumn = "task_id", resultType = String.class, before = true, statement = "select md5(uuid())")
  int insert(PolicyNotificationTask task);

  @Update({
      "update policy_notification_task",
      "set status = #{status,jdbcType=VARCHAR},",
      "failure_number = #{failureNumber,jdbcType=INTEGER},",
      "update_time = #{updateTime,jdbcType=TIMESTAMP}",
      "where task_id = #{taskId,jdbcType=VARCHAR}"
  })
  int updateStatus(PolicyNotificationTask task);

  @Select({
      "select",
      "task_id, type, subtype, destination, subject, text, schedule_time, status, message_id, failure_number, policy_number, category, create_time, update_time",
      "from policy_notification_task",
      "where status = #{status,jdbcType=VARCHAR}",
      "and type = #{type,jdbcType=VARCHAR}",
      "and failure_number < 3 ",
      "order by create_time"
  })
  @Results(id = "policyNotificationResult", value = {
      @Result(column = "task_id", property = "taskId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "type", property = "type", jdbcType = JdbcType.VARCHAR),
      @Result(column = "subtype", property = "subtype", jdbcType = JdbcType.VARCHAR),
      @Result(column = "destination", property = "destination", jdbcType = JdbcType.VARCHAR),
      @Result(column = "subject", property = "subject", jdbcType = JdbcType.VARCHAR),
      @Result(column = "text", property = "text", jdbcType = JdbcType.VARCHAR),
      @Result(column = "schedule_time", property = "scheduleTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
      @Result(column = "failure_number", property = "failureNumber", jdbcType = JdbcType.INTEGER),
      @Result(column = "policy_number", property = "policyNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "category", property = "category", jdbcType = JdbcType.VARCHAR),
      @Result(column = "message_id", property = "messageId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  List<PolicyNotificationTask> selectByTypeAndStatus(String type, String status);

  @Select({
      "<script>",
      "select",
      "task_id, type, subtype, destination, subject, text, schedule_time, status, message_id, failure_number, policy_number, category, create_time, update_time",
      "from policy_notification_task",
      "where failure_number &lt; 3 ",
      " and status in ",
      " <foreach item=\"item\" collection=\"statusList\" separator=\", \" open=\"(\" close=\")\" index=\"\">",
      " #{item, jdbcType=VARCHAR}",
      " </foreach> ",
      " order by status, create_time",
      "</script>"
  })
  @ResultMap("policyNotificationResult")
  List<PolicyNotificationTask> selectByType(List<String> statusList);

  @Select({
      "select",
      "task_id, type, subtype, destination, subject, text, schedule_time, status, message_id, failure_number, policy_number, category, create_time, update_time",
      "from policy_notification_task",
      "where policy_number = #{policyNumber,jdbcType=VARCHAR}",
      "and type = #{type,jdbcType=VARCHAR}",
      "and failure_number < 3 ",
      "order by create_time"
  })
  @ResultMap("policyNotificationResult")
  List<PolicyNotificationTask> selectByTypeAndPolicyNumber(String type, String policyNumber);

  @Delete({
      "delete from policy_notification_task",
      "where task_id = #{taskId,jdbcType=VARCHAR}"
  })
  int delete(String taskId);

  @Select({
      "select",
      "entity_id, policy, check_time, create_time, update_time",
      "from customer_policy_cache"
  })
  @Results(id = "customerPolicyResult", value = {
      @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "policy", property = "policyList", jdbcType = JdbcType.VARCHAR, javaType = Policy.class, typeHandler = PolicyListTypeHandler.class),
      @Result(column = "check_time", property = "checkTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  List<CustomerPolicyCache> selectCustomerPolicyCache();

  @Update({
      "update customer_policy_cache",
      "set ",
      "check_time = #{checkTime,jdbcType=TIMESTAMP}",
      "where entity_id = #{entityId,jdbcType=VARCHAR}"
  })
  int updateCustomerPolicyCache(CustomerPolicyCache cache);

}
