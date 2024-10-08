package ke.co.apollo.health.notification.mapper.health;

import java.util.List;
import ke.co.apollo.health.common.domain.model.CognitoSns;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CognitoSnsMapper {

  @Delete({
      "delete from cognito_sns",
      "where cognito_id = #{cognitoId,jdbcType=VARCHAR}"
  })
  int deleteByPrimaryKey(String cognitoId);

  @Insert({
      "insert into cognito_sns (cognito_id, firebase_token, ",
      "endpoint_arn",
      ")",
      "values (#{cognitoId,jdbcType=VARCHAR}, #{firebaseToken,jdbcType=VARCHAR}, ",
      "#{endpointArn,jdbcType=VARCHAR})"
  })
  int insert(CognitoSns record);

  @Select({
      "select",
      "cognito_id, firebase_token, endpoint_arn, create_time, update_time",
      "from cognito_sns",
      "where cognito_id = #{cognitoId,jdbcType=VARCHAR}"
  })
  @Results({
      @Result(column = "cognito_id", property = "cognitoId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "firebase_token", property = "firebaseToken", jdbcType = JdbcType.VARCHAR),
      @Result(column = "endpoint_arn", property = "endpointArn", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  CognitoSns selectByPrimaryKey(String cognitoId);

  @Select({
      "select",
      "cognito_id, firebase_token, endpoint_arn, create_time, update_time",
      "from cognito_sns"
  })
  @Results({
      @Result(column = "cognito_id", property = "cognitoId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "firebase_token", property = "firebaseToken", jdbcType = JdbcType.VARCHAR),
      @Result(column = "endpoint_arn", property = "endpointArn", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  List<CognitoSns> selectAll();

  @Update({
      "<script>",
      "update cognito_sns",
      "set ",
      "<if test='firebaseToken != null and firebaseToken != \"\"'> firebase_token = #{firebaseToken,jdbcType=VARCHAR},</if> ",
      "<if test='endpointArn != null and endpointArn != \"\"'> endpoint_arn = #{endpointArn,jdbcType=VARCHAR},</if> ",
      "update_time = CURRENT_TIMESTAMP",
      "where cognito_id = #{cognitoId,jdbcType=VARCHAR}",
      "</script>"
  })
  int updateByPrimaryKey(CognitoSns record);
}
