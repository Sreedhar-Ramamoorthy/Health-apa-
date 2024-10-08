package ke.co.apollo.health.policy.mapper.hms;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.DependantDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface EntityHMSMapper {

  @Select({
          "SELECT DISTINCT ENTITYID, TITLE, FIRSTNAME, SURNAME, ",
          " CONVERT(DATE, DOB) AS DOB, ",
          "GENDER, INFO, PHONENUMBER ",
          "FROM GET_ENTITYDETAILS ",
          "WHERE (INFO = #{phoneNumber,jdbcType=VARCHAR} OR PHONENUMBER = #{phoneNumber,jdbcType=VARCHAR})"
  })
  @Results(id = "customerDomainResult", value = {
      @Result(column = "ENTITYID", property = "entityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "TITLE", property = "title", jdbcType = JdbcType.VARCHAR),
      @Result(column = "FIRSTNAME", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "SURNAME", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "DOB", property = "dateOfBirth",jdbcType = JdbcType.DATE),
      @Result(column = "GENDER", property = "gender", jdbcType = JdbcType.VARCHAR)
  })
  List<Customer> getClientDetailsByPhoneNumber(@Param(value = "phoneNumber") String phoneNumber);


  @Select({
          "SELECT DISTINCT E.ENTITYID, E.TITLE, E.FIRSTNAME, E.SURNAME, ",
          " CONVERT(DATE, DOB) AS DOB, ",
          " E.GENDER, ISNULL(E.INFO, E.PHONENUMBER) AS PHONENUMBER ",
          " FROM GET_ENTITYDETAILS E, POLICY P, POLICYATTRIBUTE PA ",
          " WHERE E.ENTITYID = P.POLICYHOLDERID ",
          "  AND P.POLICYID = PA.POLICYID ",
          "  AND P.EFFECTIVEDATE = PA.EFFECTIVEDATE ",
          "  AND PA.POLICYATTRIBUTEID = 18 ",
          "  AND PA.POLICYATTRIBUTEVALUE = #{policyNumber,jdbcType=VARCHAR}"
  })
  @ResultMap("customerDomainResult")
  List<Customer> getClientDetailsByPolicyNumber(@Param(value = "policyNumber") String policyNumber);


  @Select({
          "SELECT",
          "    P.POLICYID,",
          "    P.EFFECTIVEDATE,",
          "    E.TITLE,",
          "    E.FIRSTNAME,",
          "    E.SURNAME,",
          "    E.ENTITYID,",
          "    SUBSTRING(R1.INFO, 1, 10) AS DOB,",
          "    R2.INFO AS GENDER,",
          "    CASE",
          "        WHEN C.DESCRIPTION = 'Company Employee' THEN 'Policy Holder'",
          "        ELSE ISNULL(C.DESCRIPTION, 'Policy Holder')",
          "    END AS RELATIONSHIP",
          "FROM",
          "    POLICYEXTRELATIONSHIP P",
          "    INNER JOIN ENTITY E ON P.SYSTEMKEY = E.ENTITYID",
          "    INNER JOIN ENTITYROLEINFO R1 ON R1.ENTITYID = E.ENTITYID AND R1.INFOID = 11",
          "    INNER JOIN ENTITYROLEINFO R2 ON R2.ENTITYID = E.ENTITYID AND R2.INFOID = 18",
          "    LEFT JOIN ENTITYRELATIONSHIP RL ON RL.CHILDENTITYID = E.ENTITYID",
          "    LEFT JOIN CODE C ON C.CODEID = RL.RELATIONSHIPTYPE AND CODESET = 111",
          "WHERE",
          "    P.EXTRELATIONSHIPTYPE = 'Beneficiary'",
          "    AND P.POLICYID = #{policyId,jdbcType=INTEGER}",
          "    AND P.EFFECTIVEDATE = CONVERT(DATE, #{effectiveDate,jdbcType=VARCHAR})"
  })
  @Results(id = "dependantDomainResult", value = {
      @Result(column = "POLICYID", property = "policyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "EFFECTIVEDATE", property = "effectiveDate", jdbcType = JdbcType.DATE),
      @Result(column = "TITLE", property = "title", jdbcType = JdbcType.VARCHAR),
      @Result(column = "FIRSTNAME", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "SURNAME", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "ENTITYID", property = "entityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "DOB", property = "dateOfBirth", jdbcType = JdbcType.DATE),
      @Result(column = "GENDER", property = "gender", jdbcType = JdbcType.VARCHAR),
      @Result(column = "RELATIONSHIP", property = "relationship", jdbcType = JdbcType.VARCHAR)
  })
  List<DependantDetail> getDependantByPolicyIdAndEffectiveDate(
      @Param(value = "policyId") Integer policyId,
      @Param(value = "effectiveDate") String effectiveDate);

}
