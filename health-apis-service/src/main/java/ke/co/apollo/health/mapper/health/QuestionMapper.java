package ke.co.apollo.health.mapper.health;

import ke.co.apollo.health.common.domain.model.Question;
import ke.co.apollo.health.common.domain.model.Question.Answers;
import ke.co.apollo.health.mapper.health.typehandler.AnswersTypeHandler;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface QuestionMapper {

  @Insert({
      "insert into question (customer_id, agent_id, quote_id, answers)",
      "values (#{question.customerId,jdbcType=VARCHAR}, #{question.agentId,jdbcType=VARCHAR}, #{question.quoteId,jdbcType=VARCHAR}, ",
      "#{question.answers,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.AnswersTypeHandler})"
  })
  int insert(@Param(value = "question") Question question);

  @Update({"<script>",
      "update question ",
      "set answers = #{question.answers,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.AnswersTypeHandler}",
      "where quote_id = #{question.quoteId,jdbcType=VARCHAR}",
      " and customer_id = #{question.customerId,jdbcType=VARCHAR}",
      " <if test='question.agentId != null and question.agentId != \"\"'>",
      " and agent_id = #{question.agentId,jdbcType=VARCHAR} ",
      " </if> ",
      "</script>"
  })
  int update(@Param(value = "question") Question question);

  @Delete({
      "<script>",
      "delete from question",
      "where customer_id = #{customerId,jdbcType=VARCHAR}",
      " <if test='quoteId != null and quoteId != \"\"'>",
      " and quote_id = #{quoteId,jdbcType=VARCHAR}",
      " </if> ",
      " <if test='agentId != null and agentId != \"\"'>",
      " and agent_id = #{agentId,jdbcType=VARCHAR}",
      " </if> ",
      "</script>"
  })
  int delete(@Param(value = "quoteId") String quoteId,
      @Param(value = "customerId") String customerId,
      @Param(value = "agentId") String agentId);


  @Select("<script> "
      + " select customer_id, agent_id, quote_id, answers "
      + " from question"
      + " where quote_id = #{quoteId,jdbcType=VARCHAR} "
      + " <if test='agentId != null and agentId != \"\"'>"
      + " and agent_id = #{agentId,jdbcType=VARCHAR} "
      + " </if> "
      + " <if test='customerId != null and customerId != \"\"'>"
      + " and customer_id = #{customerId,jdbcType=VARCHAR} "
      + " </if> "
      + " </script> ")
  @Results({
      @Result(column = "quote_id", property = "quoteId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "agent_id", property = "agentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "answers", property = "answers", jdbcType = JdbcType.VARCHAR, javaType = Answers.class, typeHandler = AnswersTypeHandler.class),
  })
  Question getQuestion(
      @Param(value = "quoteId") String quoteId, @Param(value = "customerId") String customerId,
      @Param(value = "agentId") String agentId);

}
