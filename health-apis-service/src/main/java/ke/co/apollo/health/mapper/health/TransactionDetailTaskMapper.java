package ke.co.apollo.health.mapper.health;

import java.util.Date;
import java.util.List;
import ke.co.apollo.health.common.domain.model.TransactionDetailTask;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Component
public interface TransactionDetailTaskMapper {
    @Delete({
        "delete from transaction_detail_task",
        "where task_id = #{taskId,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(String taskId);

    @Insert({
        "insert into transaction_detail_task (task_id, schedule_time, ",
        "type, amount, checkout_request_id, ",
        "payment_status, response_code, ",
        "response_desc, result_code, ",
        "result_desc, create_time, ",
        "update_time, create_by, ",
        "update_by)",
        "values (#{taskId,jdbcType=VARCHAR}, #{scheduleTime,jdbcType=TIMESTAMP}, ",
        "#{type,jdbcType=VARCHAR}, #{amount,jdbcType=VARCHAR}, #{checkoutRequestId,jdbcType=VARCHAR}, ",
        "#{paymentStatus,jdbcType=VARCHAR}, #{responseCode,jdbcType=INTEGER}, ",
        "#{responseDesc,jdbcType=VARCHAR}, #{resultCode,jdbcType=INTEGER}, ",
        "#{resultDesc,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateTime,jdbcType=TIMESTAMP}, #{createBy,jdbcType=VARCHAR}, ",
        "#{updateBy,jdbcType=VARCHAR})"
    })
    @SelectKey(keyProperty = "taskId", resultType = String.class, before = true,
        statement = "select MD5(uuid());")
    int insert(TransactionDetailTask record);

    @Select({
        "select",
        "task_id, schedule_time, type, amount, checkout_request_id, payment_status, response_code, ",
        "response_desc, result_code, result_desc, create_time, update_time, create_by, ",
        "update_by",
        "from transaction_detail_task",
        "where task_id = #{taskId,jdbcType=VARCHAR}"
    })
    @Results(id = "transactionDetailTaskResult", value = {
        @Result(column="task_id", property="taskId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="schedule_time", property="scheduleTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="type", property="type", jdbcType=JdbcType.VARCHAR),
        @Result(column="amount", property="amount", jdbcType=JdbcType.VARCHAR),
        @Result(column="checkout_request_id", property="checkoutRequestId", jdbcType=JdbcType.VARCHAR),
        @Result(column="payment_status", property="paymentStatus", jdbcType=JdbcType.VARCHAR),
        @Result(column="response_code", property="responseCode", jdbcType=JdbcType.INTEGER),
        @Result(column="response_desc", property="responseDesc", jdbcType=JdbcType.VARCHAR),
        @Result(column="result_code", property="resultCode", jdbcType=JdbcType.INTEGER),
        @Result(column="result_desc", property="resultDesc", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="create_by", property="createBy", jdbcType=JdbcType.VARCHAR),
        @Result(column="update_by", property="updateBy", jdbcType=JdbcType.VARCHAR)
    })
    TransactionDetailTask selectByPrimaryKey(String taskId);

    @Select({
        "select",
        "task_id, schedule_time, type, amount, checkout_request_id, payment_status, response_code, ",
        "response_desc, result_code, result_desc, create_time, update_time, create_by, ",
        "update_by",
        "from transaction_detail_task"
    })
    @ResultMap("transactionDetailTaskResult")
    List<TransactionDetailTask> selectAll();

    @Update({
        "update transaction_detail_task",
        "set schedule_time = #{scheduleTime,jdbcType=TIMESTAMP},",
          "type = #{type,jdbcType=VARCHAR},",
          "amount = #{amount,jdbcType=VARCHAR},",
          "checkout_request_id = #{checkoutRequestId,jdbcType=VARCHAR},",
          "payment_status = #{paymentStatus,jdbcType=VARCHAR},",
          "response_code = #{responseCode,jdbcType=INTEGER},",
          "response_desc = #{responseDesc,jdbcType=VARCHAR},",
          "result_code = #{resultCode,jdbcType=INTEGER},",
          "result_desc = #{resultDesc,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "create_by = #{createBy,jdbcType=VARCHAR},",
          "update_by = #{updateBy,jdbcType=VARCHAR}",
        "where task_id = #{taskId,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(TransactionDetailTask record);

    @Select({
        "select",
        "task_id, schedule_time, type, amount, checkout_request_id, payment_status, response_code, ",
        "response_desc, result_code, result_desc, create_time, update_time, create_by, ",
        "update_by",
        "from transaction_detail_task where schedule_time > #{nowDate,jdbcType=TIMESTAMP}"
    })
    @ResultMap("transactionDetailTaskResult")
    List<TransactionDetailTask> selectScheduledTasks(Date nowDate);

    @Update({
        "update transaction_detail_task",
        "set type = #{type,jdbcType=VARCHAR},",
        "amount = #{amount,jdbcType=VARCHAR},",
        "payment_status = #{paymentStatus,jdbcType=VARCHAR},",
        "response_code = #{responseCode,jdbcType=INTEGER},",
        "response_desc = #{responseDesc,jdbcType=VARCHAR},",
        "result_code = #{resultCode,jdbcType=INTEGER},",
        "result_desc = #{resultDesc,jdbcType=VARCHAR},",
        "update_by = #{updateBy,jdbcType=VARCHAR}",
        "where task_id = #{taskId,jdbcType=VARCHAR}"
    })
    int syncTransactionResponse(TransactionDetailTask record);

    @Insert({
        "insert into transaction_detail_task (task_id, schedule_time, checkout_request_id )",
        "values (#{taskId,jdbcType=VARCHAR}, #{scheduleTime,jdbcType=TIMESTAMP}, ",
        "#{checkoutRequestId,jdbcType=VARCHAR} )"
    })
    @SelectKey(keyProperty = "taskId", resultType = String.class, before = true,
        statement = "select MD5(uuid());")
    int initTransactionDetail(TransactionDetailTask record);
}
