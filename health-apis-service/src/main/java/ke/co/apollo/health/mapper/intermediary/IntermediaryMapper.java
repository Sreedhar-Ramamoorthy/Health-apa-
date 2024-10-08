package ke.co.apollo.health.mapper.intermediary;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Intermediary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface IntermediaryMapper {

  @Select({
      "select",
      "agent_id, parent_agent_id,agent_code, entity_id, first_name, last_name, phone_number, limited_company, ",
      "status, deleted, role, email, po_box, postal_code, office_contact_number, bank_id, ",
      "bank_account_number, branch_id,organization,enabled, ",
      "(SELECT bank_name FROM bank WHERE entity_id = intermediary.bank_id) bank_name,",
      "(SELECT branch_name FROM branch WHERE branch_id = intermediary.branch_id) branch_name,",
      "create_time,update_time",
      "from intermediary",
      "where agent_id = #{agentId,jdbcType=VARCHAR} and deleted = 0"
  })
  @Results(id = "intermediaryDomainResult", value = {
      @Result(column = "agent_id", property = "agentId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "parent_agent_id", property = "parentAgentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "agent_code", property = "agentCode", jdbcType = JdbcType.VARCHAR),
      @Result(column = "organization", property = "organization", jdbcType = JdbcType.VARCHAR),
      @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "phone_number", property = "phoneNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "limited_company", property = "limitedCompany", jdbcType = JdbcType.TINYINT),
      @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
      @Result(column = "deleted", property = "deleted", jdbcType = JdbcType.TINYINT),
      @Result(column = "enabled", property = "enabled", jdbcType = JdbcType.TINYINT),
      @Result(column = "role", property = "role", jdbcType = JdbcType.VARCHAR),
      @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
      @Result(column = "po_box", property = "poBox", jdbcType = JdbcType.VARCHAR),
      @Result(column = "postal_code", property = "postalCode", jdbcType = JdbcType.VARCHAR),
      @Result(column = "office_contact_number", property = "officeContactNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "bank_id", property = "bankId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "bank_account_number", property = "bankAccountNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "branch_id", property = "branchId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "bank_name", property = "bankName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "branch_name", property = "branchName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "last_login_time", property = "lastLoginTime", jdbcType = JdbcType.TIMESTAMP),
  })
  Intermediary selectByPrimaryKey(String agentId);

  @Select({
      "select",
      "agent_id, parent_agent_id,agent_code, entity_id, first_name, last_name, phone_number, limited_company, ",
      "status, deleted, role, email, po_box, postal_code, office_contact_number, bank_id, ",
      "bank_account_number, branch_id,organization,enabled, ",
      "(SELECT bank_name FROM bank WHERE entity_id = intermediary.bank_id) bank_name,",
      "(SELECT branch_name FROM branch WHERE branch_id = intermediary.branch_id) branch_name,",
      "create_time,update_time",
      "from intermediary",
      "where parent_agent_id = #{agentId,jdbcType=VARCHAR} and deleted = 0"
  })
  @ResultMap("intermediaryDomainResult")
  List<Intermediary> selectUserList(String agentId);
}
