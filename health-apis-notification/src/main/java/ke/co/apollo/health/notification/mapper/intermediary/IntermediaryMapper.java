package ke.co.apollo.health.notification.mapper.intermediary;

import java.util.List;
import ke.co.apollo.health.common.domain.model.MarketingPreference;
import ke.co.apollo.health.common.domain.model.Notification;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface IntermediaryMapper {

  @Select({
      "<script> ",
      "select",
      "agent_id, entity_id, product, sms, phone, email, mail, create_time, update_time",
      "from marketing_preference",
      "where entity_id = #{entityId,jdbcType=VARCHAR}",
      " <if test='agentId != null and agentId != \"\"'>",
      " and agent_id = #{agentId,jdbcType=VARCHAR}",
      " </if> ",
      " <if test='product != null and product != \"\"'>",
      " and product = #{product,jdbcType=VARCHAR}",
      " </if> ",
      "</script> "
  })
  @Results(id = "marketingPreferenceResult", value = {
      @Result(column = "agent_id", property = "agentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "product", property = "product", jdbcType = JdbcType.VARCHAR),
      @Result(column = "sms", property = "sms", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "phone", property = "phone", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "email", property = "email", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "mail", property = "mail", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  List<MarketingPreference> selectMarketingPreference(String agentId, String entityId,
      String product);

  @Insert({
      "insert into notification (id, super_customer_id, ",
      "title, type, content, ",
      "status )",
      "values (#{id,jdbcType=VARCHAR}, #{superCustomerId,jdbcType=VARCHAR}, ",
      "#{title,jdbcType=VARCHAR}, #{type,jdbcType=VARCHAR}, #{content,jdbcType=VARCHAR}, ",
      "#{status,jdbcType=VARCHAR} )"
  })
  @SelectKey(keyProperty = "id", resultType = String.class, before = true,
      statement = "select MD5(uuid());")
  int insert(Notification record);

  @Select({
      "select",
      "id, super_customer_id, title, type, content, status, create_time, update_time",
      "from notification",
      "where id = #{id,jdbcType=VARCHAR}"
  })
  @Results(id = "notificationResult", value = {
      @Result(column = "id", property = "id", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "super_customer_id", property = "superCustomerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "title", property = "title", jdbcType = JdbcType.VARCHAR),
      @Result(column = "type", property = "type", jdbcType = JdbcType.VARCHAR),
      @Result(column = "content", property = "content", jdbcType = JdbcType.VARCHAR),
      @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  Notification selectByPrimaryKey(String id);

}
