package ke.co.apollo.health.policy.mapper.health;

import java.util.Date;
import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.Children;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.Dependant;
import ke.co.apollo.health.common.domain.model.DependantBenefit;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.policy.mapper.typehandler.BenefitTypeHandler;
import ke.co.apollo.health.policy.mapper.typehandler.ChildrenTypeHandler;
import ke.co.apollo.health.policy.mapper.typehandler.DependantBenefitTypeHandler;
import ke.co.apollo.health.policy.mapper.typehandler.DependantTypeHandler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface HealthMapper {

  @Select({"<script> ",
      " select id, code, product_id, customer_id, agent_id, start_date, effective_date, renewal_date, benefit, status, premium, itl, phcf, stamp_duty, total_premium, balance, ",
      " payment_style, ext_policy_id, ext_policy_number, create_time, update_time, create_by, update_by ",
      " from quote",
      " where ext_policy_id = #{policyId,jdbcType=INTEGER} ",
      " <if test='effectiveDate != null' >",
      " and date_format(effective_date, '%Y-%m-%d') = date_format(#{effectiveDate,jdbcType=TIMESTAMP}, '%Y-%m-%d') ",
      " </if>",
      " and product_id = 49 ",
      " </script> "})
  @Results(id = "quoteDomainResult", value = {
      @Result(column = "id", property = "id", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR),
      @Result(column = "product_id", property = "productId", jdbcType = JdbcType.INTEGER),
      @Result(column = "agent_id", property = "agentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "start_date", property = "startDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "effective_date", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "renewal_date", property = "renewalDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "benefit", property = "benefit", jdbcType = JdbcType.VARCHAR, javaType = Benefit.class, typeHandler = BenefitTypeHandler.class),
      @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
      @Result(column = "premium", property = "premium.premium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "itl", property = "premium.itl", jdbcType = JdbcType.DECIMAL),
      @Result(column = "phcf", property = "premium.phcf", jdbcType = JdbcType.DECIMAL),
      @Result(column = "stamp_duty", property = "premium.stampDuty", jdbcType = JdbcType.DECIMAL),
      @Result(column = "total_premium", property = "premium.totalPremium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "balance", property = "balance", jdbcType = JdbcType.DECIMAL),
      @Result(column = "payment_style", property = "paymentStyle", jdbcType = JdbcType.VARCHAR),
      @Result(column = "ext_policy_id", property = "extPolicyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "ext_policy_number", property = "extPolicyNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "create_by", property = "createBy", jdbcType = JdbcType.VARCHAR),
      @Result(column = "update_by", property = "updateBy", jdbcType = JdbcType.VARCHAR)
  })
  Quote getQuoteByPolicyId(@Param(value = "policyId") Integer policyId,
      @Param(value = "effectiveDate") Date effectiveDate);

  @Select({
      "select",
      "customer_id, parent_id, agent_id, entity_id, quote_id, super_customer_id, first_name, ",
      "last_name, date_of_birth, title, gender, phone_number, email, start_date, relationship_desc, ",
      "spouse_summary, children_summary, benefit,id_no,kra_pin,create_time,update_time",
      "from customer",
      "where entity_id = #{entityId,jdbcType=INTEGER}"
  })
  @Results(id = "customerDomainResult", value = {
      @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "parent_id", property = "parentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "agent_id", property = "agentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "quote_id", property = "quoteId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "super_customer_id", property = "superCustomerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "date_of_birth", property = "dateOfBirth", jdbcType = JdbcType.DATE),
      @Result(column = "title", property = "title", jdbcType = JdbcType.VARCHAR),
      @Result(column = "gender", property = "gender", jdbcType = JdbcType.VARCHAR),
      @Result(column = "phone_number", property = "phoneNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
      @Result(column = "start_date", property = "startDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "spouse_summary", property = "spouseSummary", jdbcType = JdbcType.VARCHAR, javaType = Dependant.class, typeHandler = DependantTypeHandler.class),
      @Result(column = "children_summary", property = "childrenSummary", jdbcType = JdbcType.VARCHAR, javaType = Children.class, typeHandler = ChildrenTypeHandler.class),
      @Result(column = "benefit", property = "benefit", jdbcType = JdbcType.VARCHAR, javaType = DependantBenefit.class, typeHandler = DependantBenefitTypeHandler.class),
      @Result(column = "relationship_desc", property = "relationshipDesc", jdbcType = JdbcType.VARCHAR),
      @Result(column = "id_no", property = "idNo", jdbcType = JdbcType.VARCHAR),
      @Result(column = "kra_pin", property = "kraPin", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  Customer getCustomerByEntityId(Integer entityId);

}
