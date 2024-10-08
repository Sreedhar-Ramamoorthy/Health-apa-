package ke.co.apollo.health.mapper.health;

import java.util.Date;
import java.util.List;
import ke.co.apollo.health.common.domain.model.PaymentTransaction;
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
public interface PaymentTransactionMapper {


  @Insert({
      "insert into payment_transaction (id, customer_id, ",
      "order_id, transaction_ref, amount, merchant_id, domain, ",
      "preauth, terminal_id, currency, payment_customer_id, payment_method, quote_id, quote_number, ",
      "policy_id, policy_number, effective_date, renewal, status, client_message, client_result, payment_message, balance_message, balance_result, ",
      "create_time, update_time)",
      "values (#{id,jdbcType=VARCHAR}, #{customerId,jdbcType=VARCHAR}, ",
      "#{orderId,jdbcType=VARCHAR}, #{transactionRef,jdbcType=VARCHAR}, #{amount,jdbcType=DECIMAL}, ",
      "#{merchantId,jdbcType=VARCHAR}, #{domain,jdbcType=VARCHAR}, ",
      "#{preauth,jdbcType=VARCHAR}, #{terminalId,jdbcType=VARCHAR}, ",
      "#{currency,jdbcType=VARCHAR}, #{paymentCustomerId,jdbcType=VARCHAR}, ",
      "#{paymentMethod,jdbcType=VARCHAR}, #{quoteId,jdbcType=VARCHAR}, #{quoteNumber,jdbcType=VARCHAR}, ",
      "#{policyId,jdbcType=VARCHAR}, #{policyNumber,jdbcType=VARCHAR}, #{effectiveDate,jdbcType=TIMESTAMP}, ",
      "#{renewal,jdbcType=BOOLEAN}, #{status,jdbcType=VARCHAR}, ",
      "#{clientMessage,jdbcType=VARCHAR}, #{clientResult,jdbcType=BOOLEAN}, ",
      "#{paymentMessage,jdbcType=VARCHAR}, ",
      "#{balanceMessage,jdbcType=VARCHAR}, #{balanceResult,jdbcType=BOOLEAN}, ",
      "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
  })
  @SelectKey(keyProperty = "id", resultType = String.class, before = true, statement = "select MD5(uuid());")
  int insert(PaymentTransaction record);

  @Select({
      "select",
      "id, customer_id, ",
      "order_id, transaction_ref, amount, merchant_id, domain, ",
      "preauth, terminal_id, currency, payment_customer_id, payment_method, quote_id, quote_number, ",
      "policy_id, policy_number, effective_date, renewal, status, client_message, client_result, payment_message, balance_message, balance_result, ",
      "create_time, update_time",
      "from payment_transaction",
      "where customer_id = #{customerId,jdbcType=VARCHAR}",
      "and order_id = #{orderId,jdbcType=VARCHAR}",
      "and transaction_ref = #{transactionRef,jdbcType=VARCHAR}",
  })
  @Results(id = "paymentTransactionResult", value = {
      @Result(column = "id", property = "id", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "renewal", property = "renewal", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "order_id", property = "orderId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "transaction_ref", property = "transactionRef", jdbcType = JdbcType.VARCHAR),
      @Result(column = "amount", property = "amount", jdbcType = JdbcType.DECIMAL),
      @Result(column = "merchant_id", property = "merchantId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "domain", property = "domain", jdbcType = JdbcType.VARCHAR),
      @Result(column = "preauth", property = "preauth", jdbcType = JdbcType.VARCHAR),
      @Result(column = "currency", property = "currency", jdbcType = JdbcType.VARCHAR),
      @Result(column = "terminal_id", property = "terminalId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "payment_customer_id", property = "paymentCustomerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "payment_method", property = "paymentMethod", jdbcType = JdbcType.VARCHAR),
      @Result(column = "quote_id", property = "quoteId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "quote_number", property = "quoteNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "policy_id", property = "policyId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "policy_number", property = "policyNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "effective_date", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
      @Result(column = "client_message", property = "clientMessage", jdbcType = JdbcType.VARCHAR),
      @Result(column = "client_result", property = "clientResult", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "payment_message", property = "paymentMessage", jdbcType = JdbcType.VARCHAR),
      @Result(column = "balance_message", property = "balanceMessage", jdbcType = JdbcType.VARCHAR),
      @Result(column = "balance_result", property = "balanceResult", jdbcType = JdbcType.BOOLEAN),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  PaymentTransaction select(String customerId, String orderId, String transactionRef);

  @Select({
      "select",
      "id, customer_id, ",
      "order_id, transaction_ref, amount, merchant_id, domain, ",
      "preauth, terminal_id, currency, payment_customer_id, payment_method, quote_id, quote_number, ",
      "policy_id, policy_number, effective_date, renewal, status, client_message, client_result, payment_message, balance_message, balance_result, ",
      "create_time, update_time",
      "from payment_transaction",
      "where policy_number = #{policyNumber,jdbcType=VARCHAR}",
      "and date_format(effective_date, '%Y-%m-%d') = date_format(#{effectiveDate,jdbcType=TIMESTAMP}, '%Y-%m-%d') "
  })
  @ResultMap("paymentTransactionResult")
  List<PaymentTransaction> selectList(String policyNumber, Date effectiveDate);

  @Select({
      "select",
      "id, customer_id, ",
      "order_id, transaction_ref, amount, merchant_id, domain, ",
      "preauth, terminal_id, currency, payment_customer_id, payment_method, quote_id, quote_number, ",
      "policy_id, policy_number, effective_date, renewal, status, client_message, client_result, payment_message, balance_message, balance_result, ",
      "create_time, update_time",
      "from payment_transaction",
      "where customer_id = #{customerId,jdbcType=VARCHAR}",
      "and policy_number = #{policyNumber,jdbcType=VARCHAR}",
      "and status = #{status,jdbcType=VARCHAR} "
  })
  @ResultMap("paymentTransactionResult")
  List<PaymentTransaction> selectByCustomerIdAndPolicyNumber(String customerId, String policyNumber,
      String status);

  @Update({
      "update payment_transaction",
      "set ",
      "order_id = #{orderId,jdbcType=VARCHAR},",
      "transaction_ref = #{transactionRef,jdbcType=VARCHAR},",
      "merchant_id = #{merchantId,jdbcType=VARCHAR},",
      "terminal_id = #{terminalId,jdbcType=VARCHAR},",
      "domain = #{domain,jdbcType=VARCHAR},",
      "preauth = #{preauth,jdbcType=VARCHAR},",
      "currency = #{currency,jdbcType=VARCHAR},",
      "payment_customer_id = #{paymentCustomerId,jdbcType=VARCHAR},",
      "update_time = #{updateTime,jdbcType=TIMESTAMP}",
      "where id = #{id,jdbcType=VARCHAR}",
  })
  int updateByPrimaryKey(PaymentTransaction record);

  @Update({
      "update payment_transaction",
      "set payment_method = #{paymentMethod,jdbcType=VARCHAR},",
      "status = #{status,jdbcType=VARCHAR},",
      "client_message = #{clientMessage,jdbcType=VARCHAR},",
      "client_result = #{clientResult,jdbcType=BOOLEAN},",
      "payment_message = #{paymentMessage,jdbcType=VARCHAR},",
      "balance_message = #{balanceMessage,jdbcType=VARCHAR},",
      "balance_result = #{balanceResult,jdbcType=BOOLEAN},",
      "update_time = #{updateTime,jdbcType=TIMESTAMP}",
      "where customer_id = #{customerId,jdbcType=VARCHAR}",
      "and order_id = #{orderId,jdbcType=VARCHAR}",
      "and transaction_ref = #{transactionRef,jdbcType=VARCHAR}",
  })
  int update(PaymentTransaction record);

}
