package ke.co.apollo.health.policy.mapper.hms;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Claim;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ClaimHMSMapper {

  @Select({
      "SELECT A.ASSESSMENTID, ",
      "A.POLICYID, ",
      "I.EFFECTIVEDATE, ",
      "BENEFICIARYID, ",
      "TREATMENTDATE, ",
      "INVOICEREFERENCE, ",
      "INVOICESTATUS, ",
      "I.ADMISSIONSTATUS, ",
      "INVOICEBENEFIT, ",
      "SUM (SETTLEDAMOUNT) AS SETTLEDAMOUNT ",
      "FROM CLAIMASSESSMENT A, ",
      "CLAIMTREATMENT T, ",
      "CLAIMTREATMENTINVOICE I, ",
      "CLAIMTREATMENTINVOICELINE L ",
      "WHERE A.ASSESSMENTID = T.ASSESSMENTID",
      "AND T.TREATMENTID = I.TREATMENTID",
      "AND I.INVOICEID = L.INVOICEID",
      "AND I.INVOICESTATUS = 'Paid'",
      "AND I.INVOICETYPE <> 5",
      "AND A.POLICYID = #{policyId,jdbcType=INTEGER}",
      "GROUP BY A.ASSESSMENTID, A.POLICYID, I.EFFECTIVEDATE, BENEFICIARYID, TREATMENTDATE, INVOICEREFERENCE,INVOICESTATUS,I.ADMISSIONSTATUS,INVOICEBENEFIT",
      "ORDER BY TREATMENTDATE DESC"
  })
  @Results({
      @Result(column = "ASSESSMENTID", property = "assessmentId", jdbcType = JdbcType.INTEGER),
      @Result(column = "POLICYID", property = "policyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "EFFECTIVEDATE", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "BENEFICIARYID", property = "beneficiaryId", jdbcType = JdbcType.INTEGER),
      @Result(column = "TREATMENTDATE", property = "treatmentDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "INVOICEREFERENCE", property = "invoiceReference", jdbcType = JdbcType.VARCHAR),
      @Result(column = "INVOICESTATUS", property = "invoiceStatus", jdbcType = JdbcType.VARCHAR),
      @Result(column = "ADMISSIONSTATUS", property = "admissionStatus", jdbcType = JdbcType.VARCHAR),
      @Result(column = "INVOICEBENEFIT", property = "invoiceBenefit", jdbcType = JdbcType.VARCHAR),
      @Result(column = "SETTLEDAMOUNT", property = "settledAmount", jdbcType = JdbcType.DECIMAL)
  })
  List<Claim> getPolicyClaims(@Param(value = "policyId") Integer policyId);


}
