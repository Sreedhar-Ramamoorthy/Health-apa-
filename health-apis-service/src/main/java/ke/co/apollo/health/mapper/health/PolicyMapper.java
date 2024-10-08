package ke.co.apollo.health.mapper.health;

import java.util.Date;
import java.util.List;
import ke.co.apollo.health.common.domain.model.ApplicationRenewalPolicy;
import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.CustomerPolicyCache;
import ke.co.apollo.health.common.domain.model.HealthPolicy;
import ke.co.apollo.health.common.domain.model.Policy;
import ke.co.apollo.health.domain.ApplicationPolicyListSearchFilter;
import ke.co.apollo.health.mapper.health.typehandler.BenefitTypeHandler;
import ke.co.apollo.health.mapper.health.typehandler.PolicyListTypeHandler;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface PolicyMapper {

  @Insert({
      "insert into policy (policy_id, policy_number, quote_id, policy_holder_id, product_id, benefit, status, start_date, effective_date, renewal_date, ",
      "phcf, itl, premium, total_premium, stamp_duty, balance, create_time, update_time, loading, discount, loss_ratio, earned_premium, claims_paid, manual_adjustment, renewal_phcf, renewal_itl, renewal_premium, renewal_total_premium, renewal_stamp_duty, renewal_balance)",
      "values (#{policyId,jdbcType=INTEGER}, #{policyNumber,jdbcType=VARCHAR}, #{quoteId,jdbcType=VARCHAR}, #{policyHolderId,jdbcType=BIGINT}, #{productId,jdbcType=INTEGER},",
      "#{benefit,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.BenefitTypeHandler}, ",
      "#{status,jdbcType=VARCHAR}, #{startDate,jdbcType=TIMESTAMP}, #{effectiveDate,jdbcType=TIMESTAMP}, #{renewalDate,jdbcType=TIMESTAMP}, ",
      "#{premium.phcf,jdbcType=DECIMAL},#{premium.itl,jdbcType=DECIMAL},#{premium.premium,jdbcType=DECIMAL},#{premium.totalPremium,jdbcType=DECIMAL},#{premium.stampDuty,jdbcType=DECIMAL}, #{balance,jdbcType=DECIMAL},",
      "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP},",
      "#{renewalPremium.loading,jdbcType=DECIMAL},#{renewalPremium.discount,jdbcType=DECIMAL},",
      "#{renewalPremium.lossRatio,jdbcType=DECIMAL},#{renewalPremium.earnedPremium,jdbcType=DECIMAL},#{renewalPremium.claimsPaid,jdbcType=DECIMAL},#{renewalPremium.manualAdjustment,jdbcType=DECIMAL},",
      "#{renewalPremium.phcf,jdbcType=DECIMAL},#{renewalPremium.itl,jdbcType=DECIMAL},#{renewalPremium.premium,jdbcType=DECIMAL},#{renewalPremium.totalPremium,jdbcType=DECIMAL},#{renewalPremium.stampDuty,jdbcType=DECIMAL}, #{renewalBalance,jdbcType=DECIMAL})"
  })
  int insert(HealthPolicy policy);

  @Select({
      "select",
      "policy_id, policy_number, quote_id, policy_holder_id, product_id, benefit, status, start_date, effective_date, renewal_date, ",
      "phcf, itl, premium, total_premium, stamp_duty, balance, create_time, update_time, loading, discount, loss_ratio, earned_premium, claims_paid, manual_adjustment, renewal_phcf, renewal_itl, renewal_premium, renewal_total_premium, renewal_stamp_duty, renewal_balance",
      "from policy",
      "where policy_number = #{policyNumber,jdbcType=VARCHAR} ",
      "and date_format(effective_date, '%Y-%m-%d') = date_format(#{effectiveDate,jdbcType=TIMESTAMP}, '%Y-%m-%d') "
  })
  @Results(id = "policyResult", value = {
      @Result(column = "policy_id", property = "policyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "policy_number", property = "policyNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "quote_id", property = "quoteId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "policy_holder_id", property = "policyHolderId", jdbcType = JdbcType.BIGINT),
      @Result(column = "product_id", property = "productId", jdbcType = JdbcType.INTEGER),
      @Result(column = "benefit", property = "benefit", jdbcType = JdbcType.VARCHAR, javaType = Benefit.class, typeHandler = BenefitTypeHandler.class),
      @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
      @Result(column = "start_date", property = "startDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "effective_date", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "renewal_date", property = "renewalDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "phcf", property = "premium.phcf", jdbcType = JdbcType.DECIMAL),
      @Result(column = "itl", property = "premium.itl", jdbcType = JdbcType.DECIMAL),
      @Result(column = "premium", property = "premium.premium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "total_premium", property = "premium.totalPremium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "stamp_duty", property = "premium.stampDuty", jdbcType = JdbcType.DECIMAL),
      @Result(column = "balance", property = "balance", jdbcType = JdbcType.DECIMAL),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "loading", property = "renewalPremium.loading", jdbcType = JdbcType.DECIMAL),
      @Result(column = "discount", property = "renewalPremium.discount", jdbcType = JdbcType.DECIMAL),
      @Result(column = "loss_ratio", property = "renewalPremium.lossRatio", jdbcType = JdbcType.DECIMAL),
      @Result(column = "earned_premium", property = "renewalPremium.earnedPremium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "claims_paid", property = "renewalPremium.claimsPaid", jdbcType = JdbcType.DECIMAL),
      @Result(column = "manual_adjustment", property = "renewalPremium.manualAdjustment", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_phcf", property = "renewalPremium.phcf", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_itl", property = "renewalPremium.itl", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_premium", property = "renewalPremium.premium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_total_premium", property = "renewalPremium.totalPremium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_stamp_duty", property = "renewalPremium.stampDuty", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_balance", property = "renewalBalance", jdbcType = JdbcType.DECIMAL),
  })
  HealthPolicy select(String policyNumber, Date effectiveDate);

  @Update({
      "update policy",
      "set ",
      "renewal_balance = #{renewalBalance,jdbcType=VARCHAR},",
      "status = #{status,jdbcType=VARCHAR},",
      "update_time = #{updateTime,jdbcType=TIMESTAMP}",
      "where policy_number = #{policyNumber,jdbcType=VARCHAR} ",
      "and date_format(effective_date, '%Y-%m-%d') = date_format(#{effectiveDate,jdbcType=TIMESTAMP}, '%Y-%m-%d') "
  })
  int updateRenewalBalance(HealthPolicy policy);

  @Insert({
      "insert into customer_policy_cache (entity_id, policy, create_time, update_time)",
      "values (#{entityId,jdbcType=VARCHAR}, ",
      "#{policyList,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.PolicyListTypeHandler},",
      "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
  })
  int insertCustomerPolicy(CustomerPolicyCache record);

  @Select({
      "select",
      "entity_id, policy, create_time, update_time",
      "from customer_policy_cache",
      "where entity_id = #{entityId,jdbcType=VARCHAR} "
  })
  @Results(id = "customerPolicyResult", value = {
      @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "policy", property = "policyList", jdbcType = JdbcType.VARCHAR, javaType = Policy.class, typeHandler = PolicyListTypeHandler.class),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  CustomerPolicyCache selectCustomerPolicyCache(String entityId);

  @Select({
      "select",
      "entity_id, policy, create_time, update_time",
      "from customer_policy_cache",
      "where datediff(#{date,jdbcType=TIMESTAMP}, update_time) >= 7 "
  })
  @ResultMap("customerPolicyResult")
  List<CustomerPolicyCache> selectCustomerPolicyCacheList(Date date);

  @Update({
      "update customer_policy_cache",
      "set ",
      "policy = #{policyList,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.PolicyListTypeHandler},",
      "update_time = #{updateTime,jdbcType=TIMESTAMP}",
      "where entity_id = #{entityId,jdbcType=VARCHAR}"
  })
  int updateCustomerPolicyCache(CustomerPolicyCache cache);

  @Select({
      "<script> ",
      "select",
      "policy.policy_id, policy.policy_number, policy.quote_id, policy.policy_holder_id, policy.product_id, policy.benefit, policy.status, policy.start_date, policy.effective_date, policy.renewal_date, ",
      "policy.create_time, policy.update_time, policy.loading, policy.discount, policy.loss_ratio, policy.earned_premium, policy.claims_paid, policy.manual_adjustment, policy.renewal_phcf, policy.renewal_itl, policy.renewal_premium, policy.renewal_total_premium, policy.renewal_stamp_duty, policy.renewal_balance,",
      "customer.phone_number, CONCAT(customer.first_name, ' ', customer.last_name) customer_name, customer.customer_id, customer.agent_id, ",
      " (select name from product where id = policy.product_id) product_name ",
      "from policy, customer",
      "where policy.policy_holder_id = customer.entity_id",
      " and ifnull(policy.archived, 0) = #{archived,jdbcType=TINYINT}",
      " <if test='paid == true'>",
      " and policy.renewal_balance &lt;= 0",
      " </if> ",
      " <if test='paid == false'>",
      " and policy.renewal_balance &gt; 0",
      " </if> ",
      " <if test='filter != null and filter != \"\"'>",
         " and (customer.phone_number like concat('%', #{filter,jdbcType=VARCHAR},'%') ",
         " or policy.policy_number like concat('%', #{filter,jdbcType=VARCHAR},'%')) ",
      " </if> ",
      " <if test='sortColumn != null and sortColumn != \"\"'>",
         " order by ${sortColumn} ",
         " <if test='sort != null and sort != \"\"'>",
         " ${sort}",
         " </if> ",
      " </if> ",
      "</script> "
  })
  @Results(id = "renewalPolicyResult", value = {
      @Result(column = "quote_id", property = "quoteId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "product_id", property = "productId", jdbcType = JdbcType.INTEGER),
      @Result(column = "product_name", property = "productName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "agent_id", property = "intermediaryId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "policy_holder_id", property = "customerEntityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "start_date", property = "startDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "effective_date", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "renewal_date", property = "renewalDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
      @Result(column = "renewal_total_premium", property = "premiumAmount", jdbcType = JdbcType.DECIMAL),
      @Result(column = "loading", property = "premium.loading", jdbcType = JdbcType.DECIMAL),
      @Result(column = "discount", property = "premium.discount", jdbcType = JdbcType.DECIMAL),
      @Result(column = "loss_ratio", property = "premium.lossRatio", jdbcType = JdbcType.DECIMAL),
      @Result(column = "earned_premium", property = "premium.earnedPremium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "claims_paid", property = "premium.claimsPaid", jdbcType = JdbcType.DECIMAL),
      @Result(column = "manual_adjustment", property = "premium.manualAdjustment", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_phcf", property = "premium.phcf", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_itl", property = "premium.itl", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_premium", property = "premium.premium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_total_premium", property = "premium.totalPremium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_stamp_duty", property = "premium.stampDuty", jdbcType = JdbcType.DECIMAL),
      @Result(column = "renewal_balance", property = "balance", jdbcType = JdbcType.DECIMAL),
      @Result(column = "policy_id", property = "policyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "policy_number", property = "policyNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "archived", property = "archived", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "create_by", property = "createBy", jdbcType = JdbcType.VARCHAR),
      @Result(column = "update_by", property = "updateBy", jdbcType = JdbcType.VARCHAR),
      @Result(column = "customer_name", property = "principalMember", jdbcType = JdbcType.VARCHAR),
      @Result(column = "phone_number", property = "mobileNumber", jdbcType = JdbcType.VARCHAR)
  })
  List<ApplicationRenewalPolicy> searchRenewalPolicyList(
      ApplicationPolicyListSearchFilter applicationPolicyListSearchFilter);

  @Select({
      "<script> ",
      "select",
      "policy.policy_id, policy.policy_number, policy.quote_id, policy.policy_holder_id, policy.product_id, policy.benefit, policy.status, policy.start_date, policy.effective_date, policy.renewal_date, ",
      "policy.create_time, policy.update_time, policy.loading, policy.discount, policy.loss_ratio, policy.earned_premium, policy.claims_paid, policy.manual_adjustment, policy.renewal_phcf, policy.renewal_itl, policy.renewal_premium, policy.renewal_total_premium, policy.renewal_stamp_duty, policy.renewal_balance,",
      "customer.phone_number, CONCAT(customer.first_name, ' ', customer.last_name) customer_name, customer.customer_id, customer.agent_id, ",
      " (select name from product where id = policy.product_id) product_name ",
      "from policy, customer",
      "where policy.policy_holder_id = customer.entity_id",
      " and policy.renewal_balance - policy.renewal_total_premium &gt;= 0",
      " and policy.policy_id in ",
      " <foreach item=\"item\" collection=\"ids\" separator=\", \" open=\"(\" close=\")\" index=\"\">",
      " #{item, jdbcType=INTEGER}",
      " </foreach> ",
      "</script> "
  })
  @ResultMap("renewalPolicyResult")
  List<ApplicationRenewalPolicy> searchRenewedPolicyList(List<Integer> ids);

  @Select({
          "<script> ",
          "select",
          "policy.policy_id, policy.policy_number, policy.quote_id, policy.policy_holder_id, policy.product_id, policy.benefit, policy.status, policy.start_date, policy.effective_date, policy.renewal_date, ",
          "policy.create_time, policy.update_time, policy.loading, policy.discount, policy.loss_ratio, policy.earned_premium, policy.claims_paid, policy.manual_adjustment, policy.renewal_phcf, policy.renewal_itl, policy.renewal_premium, policy.renewal_total_premium, policy.renewal_stamp_duty, policy.renewal_balance,",
          "customer.phone_number, CONCAT(customer.first_name, ' ', customer.last_name) customer_name, customer.customer_id, customer.agent_id, ",
          " (select name from product where id = policy.product_id) product_name ",
          "from policy, customer",
          "where policy.policy_holder_id = customer.entity_id",
          " and policy.policy_id in ",
          " <foreach item=\"item\" collection=\"ids\" separator=\", \" open=\"(\" close=\")\" index=\"\">",
          " #{item, jdbcType=INTEGER}",
          " </foreach> ",
          "</script> "
  })
  @ResultMap("renewalPolicyResult")
  List<ApplicationRenewalPolicy> searchPolicyList(List<Integer> ids);


  @Select({
      "<script> ",
      "select count(policy.policy_id)",
      "from policy, customer",
      "where policy.policy_holder_id = customer.entity_id",
      " and ifnull(policy.archived, 0) = #{archived,jdbcType=TINYINT}",
      " <if test='paid == true'>",
      " and policy.renewal_balance &lt;= 0",
      " </if> ",
      " <if test='paid == false'>",
      " and policy.renewal_balance &gt; 0",
      " </if> ",
      " <if test='filter != null and filter != \"\"'>",
      " and (customer.phone_number like concat('%', #{filter,jdbcType=VARCHAR},'%') ",
      " or policy.policy_number like concat('%', #{filter,jdbcType=VARCHAR},'%')) ",
      " </if> ",
      "</script> "
  })
  int searchRenewalPolicyListCount(
      ApplicationPolicyListSearchFilter applicationPolicyListSearchFilter);


  @Select({
      "<script> ",
      "select",
      "policy.policy_id, policy.policy_number, policy.quote_id, policy.policy_holder_id, policy.product_id, policy.benefit, policy.status, policy.start_date, policy.effective_date, policy.renewal_date, ",
      "policy.create_time, policy.update_time, policy.loading, policy.discount, policy.loss_ratio, policy.earned_premium, policy.claims_paid, policy.manual_adjustment, policy.renewal_phcf, policy.renewal_itl, policy.renewal_premium, policy.renewal_total_premium, policy.renewal_stamp_duty, policy.renewal_balance,",
      "customer.phone_number, CONCAT(customer.first_name, ' ', customer.last_name) customer_name, customer.customer_id, customer.agent_id, ",
      " (select name from product where id = policy.product_id) product_name ",
      "from policy, customer",
      "where policy.policy_holder_id = customer.entity_id",
      "and policy_number = #{policyNumber,jdbcType=VARCHAR} ",
      "and date_format(effective_date, '%Y-%m-%d') = date_format(#{effectiveDate,jdbcType=TIMESTAMP}, '%Y-%m-%d') ",
      "</script> "
  })
  @ResultMap("renewalPolicyResult")
  ApplicationRenewalPolicy searchRenewalPolicy(String policyNumber, Date effectiveDate);

  @Update({
      "update policy",
      "set ",
      "archived = #{archived,jdbcType=BOOLEAN},",
      "update_time = #{updateTime,jdbcType=TIMESTAMP}",
      "where policy_number = #{policyNumber,jdbcType=VARCHAR} ",
      "and date_format(effective_date, '%Y-%m-%d') = date_format(#{effectiveDate,jdbcType=TIMESTAMP}, '%Y-%m-%d') "
  })
  int archiveApplicationRenewalPolicy(HealthPolicy policy);
}
