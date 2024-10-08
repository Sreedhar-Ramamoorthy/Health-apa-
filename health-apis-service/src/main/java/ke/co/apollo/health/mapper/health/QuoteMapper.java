package ke.co.apollo.health.mapper.health;

import java.util.List;
import ke.co.apollo.health.common.domain.model.ApplicationQuote;
import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.HealthQuote;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.domain.ApplicationQuoteListSearchFilter;
import ke.co.apollo.health.domain.QuoteListSearchFilter;
import ke.co.apollo.health.domain.request.SoftDeleteQuoteByAgentRequest;
import ke.co.apollo.health.domain.response.IdAndHideList;
import ke.co.apollo.health.mapper.health.typehandler.BenefitTypeHandler;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
public interface QuoteMapper {

  @Insert({
      "insert into quote (id, code, product_id, customer_id, agent_id, benefit, status, premium, itl, phcf, stamp_duty, total_premium, balance,",
      "payment_style, ext_policy_id, ext_policy_number, children_only, create_time, create_by, update_by, quote_status)",
      "values (#{quote.id,jdbcType=VARCHAR},#{quote.code,jdbcType=VARCHAR}, #{quote.productId,jdbcType=INTEGER}, #{quote.customerId,jdbcType=VARCHAR}, ",
      "#{quote.agentId,jdbcType=VARCHAR}, ",
      "#{quote.benefit,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.BenefitTypeHandler}, ",
      "#{quote.status,jdbcType=VARCHAR}, #{quote.premium.premium,jdbcType=DECIMAL}, #{quote.premium.itl,jdbcType=DECIMAL}, #{quote.premium.phcf,jdbcType=DECIMAL}, #{quote.premium.stampDuty,jdbcType=DECIMAL} ,#{quote.premium.totalPremium,jdbcType=DECIMAL},",
      "#{quote.balance,jdbcType=DECIMAL}, #{quote.paymentStyle,jdbcType=VARCHAR}, #{quote.extPolicyId,jdbcType=INTEGER},",
      "#{quote.extPolicyNumber,jdbcType=VARCHAR}, #{quote.isChildrenOnly,jdbcType=BOOLEAN},",
      "#{quote.createTime,jdbcType=TIMESTAMP},",
      "#{quote.createBy,jdbcType=VARCHAR}, #{quote.updateBy, jdbcType=VARCHAR}, 'ACTIVE')"
  })
  @SelectKey(keyProperty = "quote.id", keyColumn = "id", resultType = String.class, before = true, statement = "select md5(uuid())")
  int insert(@Param(value = "quote") Quote quote);

  @Update({"<script>",
      "update quote",
      "set start_date = #{quote.startDate,jdbcType=TIMESTAMP},",
      "effective_date = #{quote.effectiveDate,jdbcType=TIMESTAMP},",
      "renewal_date = #{quote.renewalDate,jdbcType=TIMESTAMP},",
      "ext_policy_id = #{quote.extPolicyId,jdbcType=INTEGER},",
      "ext_policy_number = #{quote.extPolicyNumber,jdbcType=VARCHAR},",
      "benefit = #{quote.benefit,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.BenefitTypeHandler},",
      "code = #{quote.code,jdbcType=VARCHAR},",
      "status = #{quote.status,jdbcType=VARCHAR},",
      "premium = #{quote.premium.premium,jdbcType=DECIMAL},",
      "itl = #{quote.premium.itl,jdbcType=DECIMAL},",
      "phcf = #{quote.premium.phcf,jdbcType=DECIMAL},",
      "stamp_duty = #{quote.premium.stampDuty,jdbcType=DECIMAL},",
      "total_premium = #{quote.premium.totalPremium,jdbcType=DECIMAL},",
      "balance = #{quote.balance,jdbcType=DECIMAL},",
      "update_time = #{quote.updateTime,jdbcType=TIMESTAMP},",
      "update_by = #{quote.updateBy,jdbcType=VARCHAR},",
      "quote_status = 'ACTIVE'",
      "where id = #{quote.id,jdbcType=VARCHAR}",
      " and customer_id = #{quote.customerId,jdbcType=VARCHAR}",
      " <if test='quote.agentId != null and quote.agentId != \"\"'>",
      " and agent_id = #{quote.agentId,jdbcType=VARCHAR} ",
      " </if> ",
      "</script>"
  })
  int update(@Param(value = "quote") Quote quote);

  @Delete({"<script>",
      "delete from quote",
      "where customer_id = #{customerId,jdbcType=VARCHAR}",
      " <if test='quoteId != null and quoteId != \"\"'>",
      "and id = #{quoteId,jdbcType=VARCHAR}",
      " </if> ",
      " <if test='agentId != null and agentId != \"\"'>",
      " and agent_id = #{agentId,jdbcType=VARCHAR}",
      " </if> ",
      "</script>"
  })
  int delete(String quoteId,
      String customerId,
      String agentId);

  @Select({"<script>",
      "select  id, hide from quote",
      "where id in ",
           " <foreach item=\"item\" collection=\"quoteId\" separator=\", \" open=\"(\" close=\")\" index=\"\">",
           " #{item, jdbcType=VARCHAR}",
           " </foreach> ",
      "</script>"
  })
  List<IdAndHideList> getIdAndHideResult(List<String> quoteId);

  @Delete({
      "delete from quote",
      "where customer_id = #{customerId,jdbcType=VARCHAR}",
      "and status in ('New','Enquiry')"
  })
  int deleteQuoteByCustomerId(@Param(value = "customerId") String customerId);

  @Delete({
          "delete from quote",
          "where customer_id = #{customerId,jdbcType=VARCHAR}",
          "and agent_id = #{agentId,jdbcType=VARCHAR} and product_id in(50,51)",
          "and status in ('New','Enquiry')"
  })
  int deleteDependentBenefits(@Param(value = "customerId" ) String customerId, @Param(value = "agentId") String agentId);

  @Update({
    "update quote",
    "set quote_status = 'REJECTED'",
    "where customer_id = #{customerId, jdbcType=VARCHAR}"
})
int softDeleteQuoteByCustomerId(@Param(value = "customerId") String customerId);

@Update({
        "update quote",
        "set quote_status = 'DELETED',",
        "hide = 1",
        "where id = #{quoteId, jdbcType=VARCHAR}"
})
int updateQuotStatusToDeleted(SoftDeleteQuoteByAgentRequest quote);

  @Update({
          "update quote",
          "set status = 'Viewed'",
          "where customer_id = #{customerId, jdbcType=VARCHAR}"
  })
  int updateQuotStatus(String customerId);

  @Select("<script> "
      + " select id, code, product_id, customer_id, agent_id, start_date, effective_date, renewal_date, benefit, status, premium, itl, phcf, stamp_duty, total_premium, balance, "
      + " payment_style, ext_policy_id, ext_policy_number, children_only, archived, create_time, update_time, create_by, update_by, quote_status "
      + " from quote"
      + " where (quote_status != 'REJECTED' "
      + " or quote_status is null) "
      + " <if test='agentId != null and agentId != \"\"'>"
      + " and agent_id = #{agentId,jdbcType=VARCHAR} "
      + " </if> "
      + " <if test='customerId != null and customerId != \"\"'>"
      + " and customer_id = #{customerId,jdbcType=VARCHAR} "
      + " </if> "
      + " <if test='productId != null'>"
      + " and (product_id = 49 or product_id = 52) "
      + " </if>"
      + " </script> ")
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
      @Result(column = "children_only", property = "isChildrenOnly", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "archived", property = "archived", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "create_by", property = "createBy", jdbcType = JdbcType.VARCHAR),
      @Result(column = "update_by", property = "updateBy", jdbcType = JdbcType.VARCHAR),
      @Result(column = "quote_status", property = "quoteStatus", jdbcType = JdbcType.VARCHAR)
  })
  List<Quote> getQuotes(@Param(value = "customerId") String customerId,
      @Param(value = "agentId") String agentId, @Param(value="productId") int productId);

  @Select("<script> "
      + " select quote.id, quote.code, quote.product_id, quote.customer_id, quote.agent_id, quote.start_date, quote.effective_date, quote.renewal_date, quote.benefit, quote.status, quote.premium, quote.itl, quote.phcf, quote.stamp_duty, quote.total_premium, quote.balance,"
      + " quote.payment_style, quote.ext_policy_id, quote.ext_policy_number, quote.create_time, quote.update_time, quote.create_by, quote.update_by, quote.quote_status, customer.first_name, customer.last_name, customer.phone_number, CONCAT(customer.first_name, customer.last_name) name "
      + " from quote, customer"
      + " where quote.customer_id = customer.customer_id"
      + " and quote.agent_id in "
      + " <foreach item=\"item\" collection=\"agentIds\" separator=\", \" open=\"(\" close=\")\" index=\"\">"
      + " #{item, jdbcType=VARCHAR}"
      + " </foreach> "
      + " <if test='filter != null and filter != \"\"'>"
      + " and (concat(customer.first_name, ' ', customer.last_name) like concat('%', #{filter,jdbcType=VARCHAR},'%') "
      + " or quote.code like concat('%', #{filter,jdbcType=VARCHAR},'%')) "
      + " </if> "
      + " and (quote.product_id = 49 or quote.product_id = 52) " +
          " and quote.quote_status = #{quoteStatus, jdbcType=VARCHAR}"
      + " <if test = 'hide != null'>  and hide =  ${hide} </if> "
      + " <if test='sortColumn != null and sortColumn != \"\"'>"
      + " order by ${sortColumn} "
      + " <if test='sort != null and sort != \"\"'>"
      + " ${sort}"
      + " </if> "
      + " </if> LIMIT #{index,jdbcType=INTEGER} , #{limit,jdbcType=INTEGER} "
      + " </script> ")

  @Results(id = "quoteInfoResult", value = {
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
      @Result(column = "update_by", property = "updateBy", jdbcType = JdbcType.VARCHAR),
      @Result(column = "quote_status", property = "quoteStatus", jdbcType = JdbcType.VARCHAR),
      @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "phone_number", property = "phoneNumber", jdbcType = JdbcType.VARCHAR)
  })
  List<HealthQuote> searchQuotes(QuoteListSearchFilter quoteListSearchFilter);

  @Select("<script> "
          + " select count(quote.id)"
          + " from quote, customer"
          + " where quote.customer_id = customer.customer_id"
          + " and quote.quote_status = #{quoteStatus, jdbcType=VARCHAR} "
          + " and quote.agent_id in "
          + " <foreach item=\"item\" collection=\"agentIds\" separator=\", \" open=\"(\" close=\")\" index=\"\">"
          + " #{item, jdbcType=VARCHAR}"
          + " </foreach> "
          + " <if test='filter != null and filter != \"\"'>"
          + " and (concat(customer.first_name, ' ', customer.last_name) like concat('%', #{filter,jdbcType=VARCHAR},'%') "
          + " or quote.code like concat('%', #{filter,jdbcType=VARCHAR},'%')) "
          + " </if> "
          + " and (quote.product_id = 49 OR quote.product_id = 52) "
          + " <if test = 'hide != null'>  and hide =  ${hide} </if> "
          + " </script> ")
  int searchQuotesCount(QuoteListSearchFilter quoteListSearchFilter);

  @Select("<script> "
      + " select quote.id, quote.code, quote.product_id, quote.customer_id, quote.agent_id, quote.start_date, quote.effective_date, quote.renewal_date, quote.benefit, quote.status, quote.premium, quote.itl, quote.phcf, quote.stamp_duty, quote.total_premium, quote.balance,"
      + " quote.payment_style, quote.ext_policy_id, quote.ext_policy_number, quote.create_time, quote.update_time, quote.create_by, quote.update_by, customer.first_name, customer.last_name, customer.phone_number, CONCAT(customer.first_name, customer.last_name) name "
      + " from quote, customer"
      + " where quote.customer_id = customer.customer_id"
      + " and (quote.product_id = 49 or quote.product_id = 52) "
      + " and quote.status = #{status,jdbcType=VARCHAR}"
      + " </script> ")

  @ResultMap("quoteInfoResult")
  List<HealthQuote> getQuotesByStatus(String status);

  @Select("<script> "
      + " select id, code, product_id, customer_id, agent_id, start_date, effective_date, renewal_date, benefit, status, premium, itl, phcf, stamp_duty, total_premium, balance,"
      + " payment_style, ext_policy_id, ext_policy_number, children_only, create_time, update_time, create_by, update_by "
      + " from quote"
      + " where status = #{status,jdbcType=VARCHAR}"
      + " <if test='agentId != null and agentId != \"\"'>"
      + " and agent_id = #{agentId,jdbcType=VARCHAR} "
      + " </if> "
      + " <if test='customerId != null and customerId != \"\"'>"
      + " and customer_id = #{customerId,jdbcType=VARCHAR} "
      + " </if> "
      + " and (product_id = 49 OR product_id = 52) "
      + " </script> ")
  @ResultMap("quoteDomainResult")
  List<Quote> getCustomerQuotes(@Param(value = "customerId") String customerId,
      @Param(value = "agentId") String agentId, @Param(value = "status") String status);

  @Select("<script> "
      + " select id, code, product_id, customer_id, agent_id, start_date, effective_date, renewal_date, benefit, status, premium, itl, phcf, stamp_duty, total_premium, balance,"
      + " payment_style, ext_policy_id, ext_policy_number, children_only, archived, create_time, update_time, create_by, update_by "
      + " from quote"
      + " where id = #{quoteId,jdbcType=VARCHAR}"
      + " <if test='agentId != null and agentId != \"\"'>"
      + " and agent_id = #{agentId,jdbcType=VARCHAR} "
      + " </if> "
      + " <if test='customerId != null and customerId != \"\"'>"
      + " and customer_id = #{customerId,jdbcType=VARCHAR} "
      + " </if> "
      + " </script> ")
  @ResultMap("quoteDomainResult")
  Quote getQuote(
      @Param(value = "quoteId") String quoteId, @Param(value = "customerId") String customerId,
      @Param(value = "agentId") String agentId);

  @Select("<script> "
      + " select id, code, product_id, customer_id, agent_id, start_date, effective_date, renewal_date, benefit, status, premium, itl, phcf, stamp_duty, total_premium, balance,"
      + " payment_style, ext_policy_id, ext_policy_number, children_only, archived, create_time, update_time, create_by, update_by "
      + " from quote"
      + " where customer_id = #{customerId,jdbcType=VARCHAR} "
      + " and ext_policy_number = #{policyNumber,jdbcType=VARCHAR} "
      + " </script> ")
  @ResultMap("quoteDomainResult")
  Quote getQuoteByPolicyNumber(@Param(value = "customerId") String customerId,
      @Param(value = "policyNumber") String policyNumber);

  @Select("<script> "
          + " select id, code, product_id, customer_id, agent_id, start_date, effective_date, renewal_date, benefit, status, premium, itl, phcf, stamp_duty, total_premium, balance,"
          + " payment_style, ext_policy_id, ext_policy_number, children_only, archived, create_time, update_time, create_by, update_by "
          + " from quote"
          + " where customer_id = #{customerId,jdbcType=VARCHAR} "
          + " and code = #{code,jdbcType=VARCHAR} "
          + " </script> ")
  @ResultMap("quoteDomainResult")
  Quote getQuoteByCode(@Param(value = "customerId") String customerId,
                               @Param(value = "code") String code);

  @Select({
      "select count(id) from quote",
      "where agent_id = #{agentId,jdbcType=VARCHAR}"
  })
  int getTotalQuotes(@Param(value = "agentId") String agentId);

  @Select("<script> "
      + " select quote.id, quote.code, quote.product_id, quote.customer_id, quote.agent_id, quote.start_date, quote.effective_date, quote.renewal_date, quote.benefit, quote.status, quote.premium, quote.itl, quote.phcf, quote.stamp_duty, quote.total_premium, quote.balance,"
      + " quote.payment_style, quote.ext_policy_id, quote.ext_policy_number, quote.archived, quote.create_time, quote.update_time, quote.create_by, quote.update_by, customer.first_name, customer.last_name, customer.phone_number, CONCAT(customer.first_name, ' ', customer.last_name) customer_name, "
      + " (select name from product where id = quote.product_id) product_name "
      + " from quote, customer"
      + " where quote.customer_id = customer.customer_id"
      + " and quote.status not in ('New', 'Enquiry')"
      + " <if test='filter != null and filter != \"\"'>"
      + " and (customer.phone_number like concat('%', #{filter,jdbcType=VARCHAR},'%') "
      + " or quote.ext_policy_number like concat('%', #{filter,jdbcType=VARCHAR},'%')) "
      + " </if> "
      + " and (quote.product_id = 49 OR quote.product_id = 52) "
      + " and ifnull(quote.archived, 0) = #{archived,jdbcType=TINYINT}"
      + " <if test='paid == true'>"
      + " and quote.balance &lt;= 0"
      + " </if> "
      + " <if test='paid == false'>"
      + " and quote.balance &gt; 0"
      + " </if> "
      + " <if test='sortColumn != null and sortColumn != \"\"'>"
      + " order by ${sortColumn} "
      + " <if test='sort != null and sort != \"\"'>"
      + " ${sort}"
      + " </if> "
      + " </if> "
      + " </script> ")

  @Results(id = "applicationQuoteResult", value = {
      @Result(column = "id", property = "quoteId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "code", property = "quoteNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "product_id", property = "productId", jdbcType = JdbcType.INTEGER),
      @Result(column = "product_name", property = "productName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "agent_id", property = "intermediaryId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "start_date", property = "startDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "effective_date", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "renewal_date", property = "renewalDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
      @Result(column = "premium", property = "premium.premium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "itl", property = "premium.itl", jdbcType = JdbcType.DECIMAL),
      @Result(column = "phcf", property = "premium.phcf", jdbcType = JdbcType.DECIMAL),
      @Result(column = "stamp_duty", property = "premium.stampDuty", jdbcType = JdbcType.DECIMAL),
      @Result(column = "total_premium", property = "premium.totalPremium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "total_premium", property = "premiumAmount", jdbcType = JdbcType.DECIMAL),
      @Result(column = "balance", property = "balance", jdbcType = JdbcType.DECIMAL),
      @Result(column = "payment_style", property = "paymentStyle", jdbcType = JdbcType.VARCHAR),
      @Result(column = "ext_policy_id", property = "policyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "ext_policy_number", property = "policyNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "create_by", property = "createBy", jdbcType = JdbcType.VARCHAR),
      @Result(column = "update_by", property = "updateBy", jdbcType = JdbcType.VARCHAR),
      @Result(column = "customer_name", property = "principalMember", jdbcType = JdbcType.VARCHAR),
      @Result(column = "phone_number", property = "mobileNumber", jdbcType = JdbcType.VARCHAR)
  })
  List<ApplicationQuote> searchApplicationQuotes(
      ApplicationQuoteListSearchFilter quoteListSearchFilter);

  @Select("<script> "
      + " select quote.id, quote.code, quote.product_id, quote.customer_id, quote.agent_id, quote.start_date, quote.effective_date, quote.renewal_date, quote.benefit, quote.status, quote.premium, quote.itl, quote.phcf, quote.stamp_duty, quote.total_premium, quote.balance,"
      + " quote.payment_style, quote.ext_policy_id, quote.ext_policy_number, quote.archived, quote.create_time, quote.update_time, quote.create_by, quote.update_by, customer.first_name, customer.last_name, customer.phone_number, CONCAT(customer.first_name, ' ', customer.last_name) customer_name, "
      + " (select name from product where id = quote.product_id) product_name "
      + " from quote, customer"
      + " where quote.customer_id = customer.customer_id"
      + " and quote.id = #{quoteId,jdbcType=VARCHAR}"
      + " and customer.customer_id = #{customerId,jdbcType=VARCHAR}"
      + " and quote.product_id = 49"
      + " </script> ")

  @ResultMap("applicationQuoteResult")
  ApplicationQuote searchApplicationQuote(String quoteId, String customerId);

  @Select("<script> "
      + " select count(quote.id)"
      + " from quote, customer"
      + " where quote.customer_id = customer.customer_id"
      + " and quote.status not in ('New', 'Enquiry')"
      + " <if test='filter != null and filter != \"\"'>"
      + " and (customer.phone_number like concat('%', #{filter,jdbcType=VARCHAR},'%') "
      + " or quote.ext_policy_number like concat('%', #{filter,jdbcType=VARCHAR},'%')) "
      + " </if> "
      + " and (quote.product_id = 49 OR quote.product_id = 52) "
      + " and ifnull(quote.archived, 0) = #{archived,jdbcType=TINYINT}"
      + " <if test='paid == true'>"
      + " and quote.balance &lt;= 0"
      + " </if> "
      + " <if test='paid == false'>"
      + " and quote.balance &gt; 0"
      + " </if> "
      + " </script> ")
  int searchApplicationQuotesCount(ApplicationQuoteListSearchFilter quoteListSearchFilter);

  @Update({
      "update quote",
      "set ",
      "hide = #{hide,jdbcType=BOOLEAN} ",
      "where id = #{id,jdbcType=VARCHAR} "
  })
  int hideQuote(boolean hide,String id);


  @Update({
      "update quote",
      "set ",
      "archived = #{archived,jdbcType=BOOLEAN},",
      "update_time = #{updateTime,jdbcType=TIMESTAMP}",
      "where id = #{id,jdbcType=VARCHAR} ",
      "and customer_id = #{customerId,jdbcType=VARCHAR} ",
  })
  int archiveApplicationQuote(Quote quote);

  @Select({
      "select id",
      "from quote",
      "where ext_policy_id = #{policyId,jdbcType=INTEGER}",
      "and ext_policy_number = #{policyNumber,jdbcType=VARCHAR} ",
  })
  String searchQuoteByPolicyId(Integer policyId, String policyNumber);

  @Select({
          "select id",
          "from quote",
          "where customer_id = #{customerId,jdbcType=VARCHAR}",
          " and (product_id = 49 or product_id = 52) ",
          " and quote_status = 'ACTIVE'"
  })
  String getSpouseQuoteId(String customerId);
}
