package ke.co.apollo.health.mapper.health;

import java.util.List;
import ke.co.apollo.health.common.domain.model.PaymentHistory;
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
public interface PaymentHistoryMapper {

  @Delete({
      "delete from payment_history",
      "where id = #{id,jdbcType=VARCHAR}"
  })
  int deleteByPrimaryKey(String id);

  @Insert({
      "insert into payment_history (id, customer_id, ",
      "payment_phone, amount, ",
      "payment_type, quote_number, ",
      "premium, merchant_request_id, ",
      "checkout_request_id, response_code, ",
      "response_desc, customer_msg, ",
      "create_time, update_time)",
      "values (#{id,jdbcType=VARCHAR}, #{customerId,jdbcType=VARCHAR}, ",
      "#{paymentPhone,jdbcType=VARCHAR}, #{amount,jdbcType=VARCHAR}, ",
      "#{paymentType,jdbcType=VARCHAR}, #{quoteNumber,jdbcType=VARCHAR}, ",
      "#{premium,jdbcType=VARCHAR}, #{merchantRequestId,jdbcType=VARCHAR}, ",
      "#{checkoutRequestId,jdbcType=VARCHAR}, #{responseCode,jdbcType=VARCHAR}, ",
      "#{responseDesc,jdbcType=VARCHAR}, #{customerMsg,jdbcType=VARCHAR}, ",
      "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
  })
  int insert(PaymentHistory record);

  @Select({
      "select",
      "id,renewal, customer_id, payment_phone, amount, payment_type, quote_number,policy_number,effective_date, premium, ",
      "merchant_request_id, checkout_request_id, response_code, response_desc, customer_msg, ",
      "create_time, update_time",
      "from payment_history",
      "where id = #{id,jdbcType=VARCHAR}"
  })
  @Results(id = "paymentHistoryResult", value = {
      @Result(column = "id", property = "id", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "renewal", property = "renewal", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "payment_phone", property = "paymentPhone", jdbcType = JdbcType.VARCHAR),
      @Result(column = "amount", property = "amount", jdbcType = JdbcType.VARCHAR),
      @Result(column = "payment_type", property = "paymentType", jdbcType = JdbcType.VARCHAR),
      @Result(column = "quote_number", property = "quoteNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "policy_number", property = "policyNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "effective_date", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "premium", property = "premium", jdbcType = JdbcType.VARCHAR),
      @Result(column = "merchant_request_id", property = "merchantRequestId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "checkout_request_id", property = "checkoutRequestId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "response_code", property = "responseCode", jdbcType = JdbcType.VARCHAR),
      @Result(column = "response_desc", property = "responseDesc", jdbcType = JdbcType.VARCHAR),
      @Result(column = "customer_msg", property = "customerMsg", jdbcType = JdbcType.VARCHAR),
      @Result(column = "balance", property = "balance", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  PaymentHistory selectByPrimaryKey(String id);

  @Select({
      "select",
      "id, customer_id, payment_phone, amount, payment_type, quote_number, premium, ",
      "merchant_request_id, checkout_request_id, response_code, response_desc, customer_msg, ",
      "create_time, update_time",
      "from payment_history"
  })
  @ResultMap("paymentHistoryResult")
  List<PaymentHistory> selectAll();

  @Update({
      "update payment_history",
      "set customer_id = #{customerId,jdbcType=VARCHAR},",
      "payment_phone = #{paymentPhone,jdbcType=VARCHAR},",
      "amount = #{amount,jdbcType=VARCHAR},",
      "payment_type = #{paymentType,jdbcType=VARCHAR},",
      "quote_number = #{quoteNumber,jdbcType=VARCHAR},",
      "premium = #{premium,jdbcType=VARCHAR},",
      "merchant_request_id = #{merchantRequestId,jdbcType=VARCHAR},",
      "checkout_request_id = #{checkoutRequestId,jdbcType=VARCHAR},",
      "response_code = #{responseCode,jdbcType=VARCHAR},",
      "response_desc = #{responseDesc,jdbcType=VARCHAR},",
      "customer_msg = #{customerMsg,jdbcType=VARCHAR},",
      "create_time = #{createTime,jdbcType=TIMESTAMP},",
      "update_time = #{updateTime,jdbcType=TIMESTAMP}",
      "where id = #{id,jdbcType=VARCHAR}"
  })
  int updateByPrimaryKey(PaymentHistory record);


  @Insert({
      "insert into payment_history (id, customer_id, ",
      "payment_phone, amount, ",
      "payment_type, quote_number, ",
      "premium, renewal)",
      "values (#{id,jdbcType=VARCHAR}, #{customerId,jdbcType=VARCHAR}, ",
      "#{paymentPhone,jdbcType=VARCHAR}, #{amount,jdbcType=VARCHAR}, ",
      "#{paymentType,jdbcType=VARCHAR}, #{quoteNumber,jdbcType=VARCHAR}, ",
      "#{premium,jdbcType=VARCHAR}, #{renewal,jdbcType=TINYINT})"
  })
  @SelectKey(keyProperty = "id", resultType = String.class, before = true,
      statement = "select MD5(uuid());")
  int addPaymentHistory(PaymentHistory record);

  @Insert({
      "insert into payment_history (id, customer_id, ",
      "payment_phone, amount, ",
      "payment_type, policy_number, effective_date, ",
      "premium, renewal)",
      "values (#{id,jdbcType=VARCHAR}, #{customerId,jdbcType=VARCHAR}, ",
      "#{paymentPhone,jdbcType=VARCHAR}, #{amount,jdbcType=VARCHAR}, ",
      "#{paymentType,jdbcType=VARCHAR}, #{policyNumber,jdbcType=VARCHAR}, #{effectiveDate,jdbcType=TIMESTAMP},",
      "#{premium,jdbcType=VARCHAR}, #{renewal,jdbcType=TINYINT})"
  })
  @SelectKey(keyProperty = "id", resultType = String.class, before = true,
      statement = "select MD5(uuid());")
  int addPaymentHistoryRenewal(PaymentHistory record);

  @Update({
      "update payment_history",
      "set merchant_request_id = #{merchantRequestId,jdbcType=VARCHAR},",
      "checkout_request_id = #{checkoutRequestId,jdbcType=VARCHAR},",
      "response_code = #{responseCode,jdbcType=VARCHAR},",
      "response_desc = #{responseDesc,jdbcType=VARCHAR},",
      "customer_msg = #{customerMsg,jdbcType=VARCHAR} ",
      "where id = #{id,jdbcType=VARCHAR}"
  })
  void appendMpesaResponse(PaymentHistory record);

  @Select({
      "select",
      "id,renewal customer_id, payment_phone, amount, payment_type, quote_number,policy_number,effective_date, premium, ",
      "merchant_request_id, checkout_request_id, response_code, response_desc, customer_msg, ",
      "create_time, update_time",
      "from payment_history",
      "where checkout_request_id = #{checkoutRequestId,jdbcType=VARCHAR}"
  })
  @ResultMap("paymentHistoryResult")
  List<PaymentHistory> selectByCheckoutRequestId(PaymentHistory record);

  @Select({
      "select",
      "ph.id, ph.customer_id, ph.payment_phone, ph.amount, ph.payment_type, ph.quote_number, ph.premium, ",
      "ph.merchant_request_id, ph.checkout_request_id, ph.response_code, ph.response_desc, ph.customer_msg, ",
      "ph.create_time, ph.update_time ,q.balance ",
      "from payment_history ph, quote q",
      "where ph.quote_number = q.id and q.ext_policy_id = #{policyId,jdbcType=VARCHAR} order by ph.create_time desc"
  })
  @ResultMap("paymentHistoryResult")
  List<PaymentHistory> selectByPolicyId(String policyId);
}
