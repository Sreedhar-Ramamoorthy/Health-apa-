package ke.co.apollo.health.policy.mapper.hms;

import ke.co.apollo.health.common.domain.model.IntermediaryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface IntermediaryHMSMapper {

  @Select({
      " SELECT E.ENTITYID, "
          + " CASE WHEN ENTITYTYPE=1 THEN 'CORPORATE' "
          + " ELSE 'INDIVIDUAL' "
          + " END AS ENTITYTYPEDESC, "
          + " CASE WHEN R.ROLEID=59 THEN 'AGENT INDEPENDENT' "
          + " ELSE 'AGENT TIED' "
          + " END AS ROLEDESC,"
          + " ROLEID, "
          + " FIRSTNAME, SURNAME, COMPANYNAME "
          + " FROM ENTITY E, ENTITYROLE R "
          + " WHERE E.ENTITYID=R.ENTITYID "
          + " AND ROLEID IN (59,54) "
          + " AND ACTIVEENTITY='Y' "
          + " AND E.ENTITYID = #{entityId,jdbcType=INTEGER}"})
  @Results({
      @Result(column = "ENTITYID", property = "entityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "ENTITYTYPEDESC", property = "entityTypeDesc", jdbcType = JdbcType.VARCHAR),
      @Result(column = "ROLEDESC", property = "roleDesc", jdbcType = JdbcType.VARCHAR),
      @Result(column = "ROLEID", property = "roleId", jdbcType = JdbcType.INTEGER),
      @Result(column = "FIRSTNAME", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "SURNAME", property = "surname", jdbcType = JdbcType.VARCHAR),
      @Result(column = "COMPANYNAME", property = "companyName", jdbcType = JdbcType.VARCHAR)
  })
  IntermediaryEntity getIntermediary(@Param(value = "entityId") Integer entityId);

}
