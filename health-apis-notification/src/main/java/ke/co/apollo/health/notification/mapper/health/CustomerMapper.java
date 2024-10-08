package ke.co.apollo.health.notification.mapper.health;

import ke.co.apollo.health.common.domain.model.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CustomerMapper {

  @Select({
      "select",
      "customer_id, parent_id, agent_id, entity_id, first_name, ",
      "last_name, date_of_birth, title, gender, phone_number, email, create_time,update_time",
      "from customer",
      "where entity_id = #{entityId,jdbcType=INTEGER}"
  })
  @Results(id = "customerDomainResult", value = {
      @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "parent_id", property = "parentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "agent_id", property = "agentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "date_of_birth", property = "dateOfBirth", jdbcType = JdbcType.DATE),
      @Result(column = "title", property = "title", jdbcType = JdbcType.VARCHAR),
      @Result(column = "gender", property = "gender", jdbcType = JdbcType.VARCHAR),
      @Result(column = "phone_number", property = "phoneNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  Customer getCustomerByEntityId(Integer entityId);
}
