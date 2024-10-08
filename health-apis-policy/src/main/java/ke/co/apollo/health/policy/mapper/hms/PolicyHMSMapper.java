package ke.co.apollo.health.policy.mapper.hms;

import java.util.Date;
import java.util.List;

import ke.co.apollo.health.common.domain.model.*;
import ke.co.apollo.health.common.domain.model.request.IntermediaryPolicyDetailsRequest;
import ke.co.apollo.health.policy.model.AgentBranchDetails;
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
public interface PolicyHMSMapper {

  @Select({
      " <script>",
      " SELECT P.PRODUCTID, P.POLICYID, P.EFFECTIVEDATE,P.EFFECTIVEDATE AS STARTDATE, ",
      " CONVERT(DATE, A.POLICYATTRIBUTEVALUE, 23) as RENEWALDATE, ",
      " P.POLICYHOLDERID, P.PAYMENTMETHOD, P.POLICYSTATUS AS POLICYSTATUS, 'Family' AS LEVELOFCOVER,",
      " P.POLICYAMOUNT,  PN.POLICYATTRIBUTEVALUE AS POLICYNUMBER",
      " FROM POLICY P",
      "      JOIN POLICYATTRIBUTE A",
      "         ON A.POLICYID = P.POLICYID AND A.EFFECTIVEDATE = P.EFFECTIVEDATE",
      "      JOIN POLICYATTRIBUTE PN",
      "         ON PN.POLICYID = P.POLICYID AND PN.EFFECTIVEDATE = P.EFFECTIVEDATE",
      " WHERE     A.POLICYATTRIBUTEID = 21",
      "      AND POLICYHOLDERID = #{policyHolderId,jdbcType=INTEGER}",
      "      AND P.POLICYSTATUS in ('LA', 'L', 'RL')",
      "      AND P.PRODUCTID = 49",
      "      AND PN.POLICYATTRIBUTEID = 18",
      " <if test='filter != null and filter != \"\"'>",
      " and PN.POLICYATTRIBUTEVALUE like concat(concat('%', #{filter,jdbcType=VARCHAR}),'%')",
      " </if> ",
      " <if test='sortColumn != null and sortColumn != \"\"'>",
      " order by ${sortColumn} ${sort}",
      " </if> ",
      " </script>",
  })
  @Results({
      @Result(column = "PRODUCTID", property = "productId", jdbcType = JdbcType.INTEGER),
      @Result(column = "POLICYID", property = "policyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "EFFECTIVEDATE", property = "policyEffectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "STARTDATE", property = "policyStartDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "RENEWALDATE", property = "policyRenewalDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "POLICYHOLDERID", property = "policyHolderEntityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "PAYMENTMETHOD", property = "paymentStyle", jdbcType = JdbcType.VARCHAR),
      @Result(column = "POLICYSTATUS", property = "policyStatus", jdbcType = JdbcType.VARCHAR),
      @Result(column = "LEVELOFCOVER", property = "levelOfCover", jdbcType = JdbcType.VARCHAR),
      @Result(column = "POLICYAMOUNT", property = "policyAmount", jdbcType = JdbcType.DECIMAL),
      @Result(column = "POLICYNUMBER", property = "policyNumber", jdbcType = JdbcType.VARCHAR)
  })
  List<Policy> getPolicyLists(@Param(value = "policyHolderId") Integer policyHolderId,
      @Param(value = "filter") String filter, @Param(value = "sortColumn") String sortColumn,
      @Param(value = "sort") String sort);


  @Select({
          " SELECT ",
          " P.POLICYID, PA2.POLICYATTRIBUTEVALUE AS POLICYNUMBER, ",
          " ISNULL(COMPANYNAME, (FIRSTNAME + ' ' + SURNAME)) AS POLICYHOLDERNAME, ",
          " POLICYSTATUS, POLICYHOLDERID, P.PRODUCTID, C.DESCRIPTION AS PRODUCTNAME, ",
          " (E.TITLE + ' ' + FIRSTNAME + ' ' + SURNAME) AS PRINCIPALMEMBER, ",
          " E.ENTITYID AS PRINCIPALID, P.EFFECTIVEDATE AS STARTDATE, ",
          " CONVERT(DATE, BD.INFO, 23) AS PRINCIPAL_DOB, ",
          " CONVERT(DATE, PA1.POLICYATTRIBUTEVALUE, 23) AS RENEWALDATE, ",
          " P.PAYMENTMETHOD, P.POLICYAMOUNT AS TOTALPREMIUM, ",
          " SUM (PD.AMOUNTPAID) AS PREMIUMPAID, (P.POLICYAMOUNT - SUM (PD.AMOUNTPAID)) AS PREMIUMLEFTTOPAY ",
          " FROM POLICY P ",
          " INNER JOIN ENTITY E ON P.POLICYHOLDERID = E.ENTITYID ",
          " INNER JOIN COMPONENT C ON P.PRODUCTID = C.COMPONENTID ",
          " INNER JOIN ENTITYROLEINFO BD ON BD.ENTITYID = E.ENTITYID AND BD.INFOID = 11 ",
          " INNER JOIN POLICYATTRIBUTE PA1 ON PA1.POLICYID = P.POLICYID AND PA1.EFFECTIVEDATE = P.EFFECTIVEDATE AND PA1.POLICYATTRIBUTEID = 21 ",
          " INNER JOIN POLICYATTRIBUTE PA2 ON PA2.POLICYID = P.POLICYID AND PA2.EFFECTIVEDATE = P.EFFECTIVEDATE AND PA2.POLICYATTRIBUTEID = 18 ",
          " LEFT JOIN PREMIUMDUE PD ON PD.POLICYID = P.POLICYID AND PD.POLEFFDATE = P.EFFECTIVEDATE AND PD.PAYMENTSTATUS IN (2, 3) ",
          " WHERE PA2.POLICYATTRIBUTEVALUE = 'COR1100341' ",
          " AND ",
          " P.EFFECTIVEDATE = CONVERT(DATE, '2021-01-01', 23) ",
          " AND C.COMPONENTTYPEID = 16 ",
          " GROUP BY ",
          " P.POLICYID, PA2.POLICYATTRIBUTEVALUE, ",
          " ISNULL(COMPANYNAME, (FIRSTNAME + ' ' + SURNAME)), ",
          " POLICYSTATUS, POLICYHOLDERID, P.PRODUCTID, C.DESCRIPTION, (E.TITLE + ' ' + FIRSTNAME + ' ' + SURNAME), ",
          " E.ENTITYID, BD.INFO, P.EFFECTIVEDATE, P.PAYMENTMETHOD, PA1.POLICYATTRIBUTEVALUE, P.POLICYAMOUNT",})
  @Results({
      @Result(column = "POLICYID", property = "policyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "POLICYNUMBER", property = "policyNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "POLICYHOLDERNAME", property = "policyHolderName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "POLICYSTATUS", property = "policyStatus", jdbcType = JdbcType.VARCHAR),
      @Result(column = "POLICYHOLDERID", property = "policyHolderEntityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "PRODUCTID", property = "productId", jdbcType = JdbcType.INTEGER),
      @Result(column = "PRODUCTNAME", property = "productName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "PRINCIPALMEMBER", property = "principalMember", jdbcType = JdbcType.VARCHAR),
      @Result(column = "PRINCIPALID", property = "principalId", jdbcType = JdbcType.INTEGER),
      @Result(column = "PRINCIPAL_DOB", property = "principalDob", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "STARTDATE", property = "policyStartDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "RENEWALDATE", property = "policyRenewalDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "STARTDATE", property = "policyEffectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "PAYMENTMETHOD", property = "paymentStyle", jdbcType = JdbcType.VARCHAR),
      @Result(column = "TOTALPREMIUM", property = "totalPremium", jdbcType = JdbcType.DECIMAL),
      @Result(column = "PREMIUMPAID", property = "premiumPaid", jdbcType = JdbcType.DECIMAL),
      @Result(column = "PREMIUMLEFTTOPAY", property = "premiumLeftToPay", jdbcType = JdbcType.DECIMAL)
  })
  PolicyDetail getPolicyDetail(@Param(value = "policyNumber") String policyNumber,
      @Param(value = "effectiveDate") String effectiveDate);

  @Select({
          "<script>",
          " SELECT PRODUCTID, POLICYID, EFFECTIVEDATE, STARTDATE,",
          " CONVERT(DATE, RENEWALDATE) AS RENEWALDATE,",
          " POLICYHOLDERID, PAYMENTMETHOD, POLICYSTATUS, 'Family' AS LEVELOFCOVER,",
          " TOTALPREMIUM, POLICYNUMBER",
          " FROM GET_POLICYDETAILS",
          " WHERE POLICYHOLDERID = #{policyHolderId,jdbcType=INTEGER}",
          " AND POLICYSTATUS IN ('LA', 'L', 'RL')",
          " AND PRODUCTID = 49 ",
          "<if test='filter != null and filter != \"\"'>",
          "    AND POLICYNUMBER LIKE '%' + #{filter,jdbcType=VARCHAR} + '%'",
          "</if>",
          "<if test='sortColumn != null and sortColumn != \"\"'>",
          "    ORDER BY ${sortColumn} ${sort}",
          "</if>",
          "</script>",
  })
  @Results(id = "policyListResult", value = {
      @Result(column = "PRODUCTID", property = "productId", jdbcType = JdbcType.INTEGER),
      @Result(column = "POLICYID", property = "policyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "EFFECTIVEDATE", property = "policyEffectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "STARTDATE", property = "policyStartDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "RENEWALDATE", property = "policyRenewalDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "POLICYHOLDERID", property = "policyHolderEntityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "PAYMENTMETHOD", property = "paymentStyle", jdbcType = JdbcType.VARCHAR),
      @Result(column = "POLICYSTATUS", property = "policyStatus", jdbcType = JdbcType.VARCHAR),
      @Result(column = "LEVELOFCOVER", property = "levelOfCover", jdbcType = JdbcType.VARCHAR),
      @Result(column = "TOTALPREMIUM", property = "policyAmount", jdbcType = JdbcType.DECIMAL),
      @Result(column = "POLICYNUMBER", property = "policyNumber", jdbcType = JdbcType.VARCHAR)
  })
  List<Policy> searchPolicyLists(@Param(value = "policyHolderId") Integer policyHolderId,
      @Param(value = "filter") String filter, @Param(value = "sortColumn") String sortColumn,
      @Param(value = "sort") String sort);

  @Select({
      " <script>",
      " SELECT PRODUCTID, POLICYID, EFFECTIVEDATE, STARTDATE, ",
      " CONVERT(DATE, RENEWALDATE) as RENEWALDATE, ",
      " POLICYHOLDERID, PAYMENTMETHOD, POLICYSTATUS, 'Family' AS LEVELOFCOVER,",
      " TOTALPREMIUM,  POLICYNUMBER",
      " FROM GET_POLICYDETAILS",
      " WHERE POLICYSTATUS in ('LA', 'L', 'RL')",
      "   AND PRODUCTID = 49",
      "   AND POLICYHOLDERID in ",
      " <foreach item=\"item\" collection=\"policyHolderIds\" separator=\", \" open=\"(\" close=\")\" index=\"\">",
      " #{item, jdbcType=INTEGER}",
      " </foreach> ",
      " ORDER BY POLICYHOLDERID",
      " </script>",
  })
  @ResultMap("policyListResult")
  List<Policy> searchBatchPolicyLists(
      @Param(value = "policyHolderIds") List<Integer> policyHolderIds);

  @Select({
      " <script>",
      " SELECT PRODUCTID, POLICYID, EFFECTIVEDATE, STARTDATE, ",
      " CONVERT(DATE, RENEWALDATE) as RENEWALDATE, ",
      " POLICYHOLDERID, PAYMENTMETHOD, POLICYSTATUS, 'Family' AS LEVELOFCOVER,",
      " TOTALPREMIUM,  POLICYNUMBER",
      " FROM GET_POLICYDETAILS",
      " WHERE POLICYSTATUS in ('L')",
      "   AND PRODUCTID = 49",
      "   AND POLICYID in ",
      " <foreach item=\"item\" collection=\"policyIds\" separator=\", \" open=\"(\" close=\")\" index=\"\">",
      " #{item, jdbcType=INTEGER}",
      " </foreach> ",
      " </script>",
  })
  @ResultMap("policyListResult")
  List<Policy> searchBatchPolicyListsById(List<Integer> policyIds);

  @Select({
      " SELECT PRODUCTID, POLICYID, EFFECTIVEDATE, STARTDATE, ",
      " CONVERT(DATE, RENEWALDATE, 23) AS RENEWALDATE, ",
      " POLICYHOLDERID, PAYMENTMETHOD, POLICYSTATUS, 'Family' AS LEVELOFCOVER,",
      " TOTALPREMIUM, POLICYNUMBER",
      " FROM GET_POLICYDETAILS",
      " WHERE POLICYID = #{policyId,jdbcType=INTEGER}",
      "   AND EFFECTIVEDATE < #{effectiveDate,jdbcType=TIMESTAMP}",
      "   AND POLICYSTATUS in ('H')",
      "   AND PRODUCTID = 49",
      " ORDER BY EFFECTIVEDATE DESC"
  })
  @ResultMap("policyListResult")
  List<Policy> searchPolicyHistoryLists(@Param(value = "policyId") Integer policyId,
      @Param(value = "effectiveDate") Date effectiveDate);

  @Select({
          " SELECT TOP 1 POLICYID, POLICYNUMBER, POLICYHOLDERNAME, ",
          " POLICYSTATUS, POLICYHOLDERID, PRODUCTID, PRODUCTNAME, ",
          " PRINCIPALMEMBER, PRINCIPALID, STARTDATE, EFFECTIVEDATE, ",
          " CONVERT(DATE, PRINCIPAL_DOB) AS PRINCIPAL_DOB, ",
          " CONVERT(DATE, RENEWALDATE) AS RENEWALDATE, ",
          " PAYMENTMETHOD, TOTALPREMIUM, PREMIUMPAID, PREMIUMLEFTTOPAY ",
          " FROM GET_POLICYDETAILS ",
          " WHERE POLICYNUMBER = #{policyNumber,jdbcType=VARCHAR} ORDER BY EFFECTIVEDATE DESC "
  })
  @Results({
          @Result(column = "POLICYID", property = "policyId", jdbcType = JdbcType.INTEGER),
          @Result(column = "POLICYNUMBER", property = "policyNumber", jdbcType = JdbcType.VARCHAR),
          @Result(column = "POLICYHOLDERNAME", property = "policyHolderName", jdbcType = JdbcType.VARCHAR),
          @Result(column = "POLICYSTATUS", property = "policyStatus", jdbcType = JdbcType.VARCHAR),
          @Result(column = "POLICYHOLDERID", property = "policyHolderEntityId", jdbcType = JdbcType.INTEGER),
          @Result(column = "PRODUCTID", property = "productId", jdbcType = JdbcType.INTEGER),
          @Result(column = "PRODUCTNAME", property = "productName", jdbcType = JdbcType.VARCHAR),
          @Result(column = "PRINCIPALMEMBER", property = "principalMember", jdbcType = JdbcType.VARCHAR),
          @Result(column = "PRINCIPALID", property = "principalId", jdbcType = JdbcType.INTEGER),
          @Result(column = "PRINCIPAL_DOB", property = "principalDob", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "STARTDATE", property = "policyStartDate", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "RENEWALDATE", property = "policyRenewalDate", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "EFFECTIVEDATE", property = "policyEffectiveDate", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "PAYMENTMETHOD", property = "paymentStyle", jdbcType = JdbcType.VARCHAR),
          @Result(column = "TOTALPREMIUM", property = "totalPremium", jdbcType = JdbcType.DECIMAL),
          @Result(column = "PREMIUMPAID", property = "premiumPaid", jdbcType = JdbcType.DECIMAL),
          @Result(column = "PREMIUMLEFTTOPAY", property = "premiumLeftToPay", jdbcType = JdbcType.DECIMAL)
  })
  PolicyDetail searchPolicyDetail(@Param(value = "policyNumber") String policyNumber,
                                  @Param(value = "effectiveDate") String effectiveDate);

  @Select({
      " SELECT b.policyid AS POLICYID, ",
      " b.poleffdate AS EFFECTIVEDATE, ",
      " b.benefitid AS BENEFITID, ",
      " c.description AS DESCRIPTION, ",
      " c.productid AS PRODUCTID, ",
      " a.componentvalue AS BENEFIT, ",
      " BN.COMPONENTVALUE as LIMIT",
      " FROM relation r, ",
      " component c, ",
      " POLBILLABLEBENEFIT b, ",
      " componentattribute a, ",
      " componentattribute BN ",
      " WHERE r.parentcomptypeid = 17 ",
      " AND r.childcomptypeid = 18 ",
      " AND r.parentcomptypeid = c.componenttypeid ",
      " AND c.componentid = r.parentcompid ",
      " AND c.componentid in (242,243) ",
      " AND b.benefittypeid = 18 ",
      " AND a.componenttypeid = b.benefittypeid ",
      " AND a.componentid = b.benefitid ",
      " AND r.childcompid = b.benefitid ",
      " AND a.attributeid = 197 ",
      " AND b.policyid = #{policyId,jdbcType=INTEGER} ",
      " AND b.poleffdate = CONVERT(DATE, #{effectiveDate,jdbcType=VARCHAR}, 23) ",
      " AND BN.componentid=b.benefitid",
      " AND BN.attributeid=203",
      " AND BN.componenttypeid=b.benefittypeid",
  })
  @Results({
      @Result(column = "POLICYID", property = "policyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "EFFECTIVEDATE", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "BENEFITID", property = "benefitId", jdbcType = JdbcType.INTEGER),
      @Result(column = "DESCRIPTION", property = "description", jdbcType = JdbcType.VARCHAR),
      @Result(column = "PRODUCTID", property = "productId", jdbcType = JdbcType.INTEGER),
      @Result(column = "BENEFIT", property = "benefit", jdbcType = JdbcType.VARCHAR),
      @Result(column = "LIMIT", property = "limit", jdbcType = JdbcType.VARCHAR)
  })
  List<PolicyBenefit> getPolicyBenefit(@Param(value = "policyId") Integer policyId,
      @Param(value = "effectiveDate") String effectiveDate);


  @Select({
      " SELECT p.policyid AS POLICYID, p.effectivedate AS EFFECTIVEDATE, p.POLICYAMOUNT, ",
      " a.POLICYATTRIBUTEVALUE AS ADJUSTMENT",
      " FROM POLICY p, policyattribute a ",
      " WHERE p.policyid = a.policyid ",
      " AND p.effectivedate = a.effectivedate ",
      " AND POLICYATTRIBUTEID = 732 ",
      " AND p.POLICYID = #{policyId,jdbcType=INTEGER}  ",
      " AND p.EFFECTIVEDATE = CONVERT(DATE, #{effectiveDate,jdbcType=VARCHAR} ,23) ",
  })
  @Results({
      @Result(column = "POLICYID", property = "policyId", jdbcType = JdbcType.INTEGER),
      @Result(column = "EFFECTIVEDATE", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "POLICYAMOUNT", property = "policyAmount", jdbcType = JdbcType.DECIMAL),
      @Result(column = "ADJUSTMENT", property = "adjustment", jdbcType = JdbcType.DECIMAL),
  })
  PolicyAdjustment getPolicyAdjustment(@Param(value = "policyId") Integer policyId,
      @Param(value = "effectiveDate") String effectiveDate);

  @Select({"SELECT TOP 1 E.ENTITYID, E.FIRSTNAME, E.SURNAME, INFO BRANCH\n" +
          "FROM ENTITYROLEINFO I\n" +
          "         JOIN ENTITY E ON I.ENTITYID = E.ENTITYID\n" +
          "WHERE INFOID = 1423\n" +
          "  AND E.ENTITYID = #{entityId,jdbcType=INTEGER}"})
  @Results({
          @Result(column = "ENTITYID", property = "entityId", jdbcType = JdbcType.INTEGER),
          @Result(column = "FIRSTNAME", property = "firstName", jdbcType = JdbcType.VARCHAR),
          @Result(column = "SURNAME", property = "surName", jdbcType = JdbcType.VARCHAR),
          @Result(column = "BRANCH", property = "branch", jdbcType = JdbcType.VARCHAR),
  })
  AgentBranchDetails getAgentBranchDetails(@Param(value = "entityId") Integer entityId);

  @Select("WITH policydetails AS ( " +
          "   SELECT " +
          "       p.policyid, " +
          "       e.entityid, " +
          "       p.effectivedate, " +
          "       pn.policyattributevalue AS policynumber, " +
          "       firstname + ' ' + surname AS principalname, " +
          "       CONVERT(DATE, a.policyattributevalue) AS renewaldate, " +
          "       paymentmethod AS [plan], " +
          "       CaptureDate, " +
          "       policyamount " +
          "   FROM " +
          "       policy p " +
          "       INNER JOIN policyattribute pn ON p.policyid = pn.policyid AND p.effectivedate = pn.effectivedate AND pn.policyattributeid = 18 " +
          "       INNER JOIN policyattribute a ON p.policyid = a.policyid AND p.effectivedate = a.effectivedate AND a.policyattributeid = 21 " +
          "       INNER JOIN entity e ON p.policyholderid = e.entityid " +
          "   WHERE " +
          "       policystatus = 'L' " +
          "       AND productid = 49 " +
          "       AND policytype = 2 " +
          "       AND CaptureDate >= CAST(GETDATE() as DATE)" +
          "), " +
          "claimsdetails AS ( " +
          "   SELECT " +
          "       policyid, " +
          "       a.effectivedate, " +
          "       SUM(settledamount) AS settledamount " +
          "   FROM " +
          "       claimassessment a " +
          "       INNER JOIN claimtreatment t ON a.assessmentid = t.assessmentid " +
          "       INNER JOIN claimtreatmentinvoice i ON t.treatmentid = i.treatmentid " +
          "       INNER JOIN claimtreatmentinvoiceline l ON i.invoiceid = l.invoiceid " +
          "   WHERE " +
          "       i.invoicestatus = 'Paid' " +
          "   GROUP BY " +
          "       policyid, " +
          "       a.effectivedate " +
          "), " +
          "interestedparties AS ( " +
          "   SELECT " +
          "       i.policyid, " +
          "       i.effectivedate, " +
          "       entityid AS agentid, " +
          "       ISNULL(companyname, firstname + ' ' + surname) AS agentname " +
          "   FROM " +
          "       policyextrelationinfo i " +
          "       INNER JOIN entity e ON E.ENTITYID = SUBSTRING(ExtRelationshipType, PATINDEX('%[0-9]%', ExtRelationshipType), LEN(ExtRelationshipType)) " +
          "   WHERE " +
          "       POLICYINFO IN ('Tied Agent', 'Independent Agent', 'Broker') " +
          "       AND POLICYINFOID = 27 " +
          "), " +
          "phonenumber AS ( " +
          "   SELECT " +
          "       entityid, " +
          "       phonenumber AS mobile " +
          "   FROM " +
          "       phone p " +
          "       INNER JOIN entityphone ph ON p.phoneid = ph.phoneid " +
          "   WHERE " +
          "       phonetype = 3 " +
          "), " +
          "emails AS ( " +
          "   SELECT " +
          "       entityid, " +
          "       info AS email " +
          "   FROM " +
          "       entityroleinfo " +
          "   WHERE " +
          "       infoid = 16 " +
          ") " +
          "SELECT " +
          "   policynumber, " +
          "   effectivedate, " +
          "   principalname, " +
          "   renewaldate, " +
          "   [plan], " +
          "   ISNULL(agentname, 'Direct') AS agentname, " +
          "   ISNULL(agentid, 0) AS asagentid, " +
          "   policyamount, " +
          "   ISNULL(settledamount, 0) AS claims, " +
          "   email, " +
          "   mobile, " +
          "   CaptureDate " +
          "FROM " +
          "   ( " +
          "       SELECT " +
          "           pd.policynumber, " +
          "           pd.effectivedate, " +
          "           pd.principalname, " +
          "           pd.renewaldate, " +
          "           pd.[plan], " +
          "           ip.agentname, " +
          "           ip.agentid, " +
          "           pd.policyamount, " +
          "           cd.settledamount, " +
          "           e.email, " +
          "           pn.mobile, " +
          "           pd.CaptureDate, "+
          "           ROW_NUMBER() OVER (ORDER BY pd.renewaldate) AS rowno " +
          "       FROM " +
          "           policydetails pd " +
          "           LEFT JOIN claimsdetails cd ON pd.policyid = cd.policyid AND pd.effectivedate = cd.effectivedate " +
          "           LEFT JOIN interestedparties ip ON pd.policyid = ip.policyid AND pd.effectivedate = ip.effectivedate " +
          "           LEFT JOIN phonenumber pn ON pd.entityid = pn.entityid " +
          "           LEFT JOIN emails e ON pd.entityid = e.entityid " +
          "   ) AS table_alias ")
  @Results({
          @Result(column = "policyNumber", property = "policyNumber", jdbcType = JdbcType.INTEGER),
          @Result(column = "principalName", property = "principalName", jdbcType = JdbcType.INTEGER),
          @Result(column = "renewalDate", property = "renewalDate", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "effectiveDate", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "plan", property = "plan", jdbcType = JdbcType.VARCHAR),
          @Result(column = "agentName", property = "agentName", jdbcType = JdbcType.VARCHAR),
          @Result(column = "asagentId", property = "asagentId", jdbcType = JdbcType.VARCHAR),
          @Result(column = "policyAmount", property = "policyAmount", jdbcType = JdbcType.VARCHAR),
          @Result(column = "claims", property = "claims", jdbcType = JdbcType.VARCHAR),
          @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
          @Result(column = "mobile", property = "mobile", jdbcType = JdbcType.VARCHAR),
          @Result(column = "CaptureDate", property = "CaptureDate", jdbcType = JdbcType.TIMESTAMP),

  })
  List<PolicyOverComing> getComingPolicyList();

  @Select({
          "WITH policydetails AS (",
          "   SELECT",
          "      p.policyid,",
          "      e.entityid,",
          "      p.effectivedate,",
          "      pn.policyattributevalue AS policynumber,",
          "      firstname + ' ' + surname AS principalname,",
          "      CONVERT(DATE, a.policyattributevalue) AS renewaldate,",
          "      paymentmethod AS [plan],",
          "      policyamount",
          "   FROM",
          "      policy p",
          "      INNER JOIN policyattribute pn ON p.policyid = pn.policyid",
          "      INNER JOIN policyattribute a ON p.policyid = a.policyid AND p.effectivedate = a.effectivedate",
          "      INNER JOIN entity e ON p.policyholderid = e.entityid",
          "   WHERE",
          "      pn.policyattributeid = 18",
          "      AND pn.effectivedate = p.effectivedate",
          "      AND a.POLICYATTRIBUTEID = 21",
          "      AND policystatus = 'L'",
          "      AND productid = 49",
          "      AND policytype = 2",
          "),",
          "claimsdetails AS (",
          "   SELECT",
          "      policyid,",
          "      a.effectivedate,",
          "      SUM(settledamount) AS settledamount",
          "   FROM",
          "      claimassessment a",
          "      INNER JOIN claimtreatment t ON a.assessmentid = t.assessmentid",
          "      INNER JOIN claimtreatmentinvoice i ON t.treatmentid = i.treatmentid",
          "      INNER JOIN claimtreatmentinvoiceline l ON i.invoiceid = l.invoiceid",
          "   WHERE",
          "      i.invoicestatus = 'Paid'",
          "   GROUP BY",
          "      policyid,",
          "      a.effectivedate",
          "),",
          "interestedparties AS (",
          "   SELECT",
          "      i.policyid,",
          "      i.effectivedate,",
          "      entityid AS agentid,",
          "      ISNULL(companyname, firstname + ' ' + surname) AS agentname",
          "   FROM",
          "      policyextrelationinfo i",
          "      INNER JOIN entity e ON e.entityid = SUBSTRING(ExtRelationshipType, PATINDEX('%[0-9]%', ExtRelationshipType), LEN(ExtRelationshipType))",
          "   WHERE",
          "      POLICYINFO IN ('Tied Agent', 'Independent Agent', 'Broker')",
          "      AND POLICYINFOID = 27",
          "),",
          "phonenumber AS (",
          "   SELECT",
          "      entityid,",
          "      phonenumber AS mobile",
          "   FROM",
          "      phone p",
          "      INNER JOIN entityphone ph ON p.phoneid = ph.phoneid",
          "   WHERE",
          "      phonetype = 3",
          "),",
          "emails AS (",
          "   SELECT",
          "      entityid,",
          "      info AS email",
          "   FROM",
          "      entityroleinfo",
          "   WHERE",
          "      infoid = 16",
          ")",
          "SELECT",
          "   COUNT(policynumber) AS total",
          "FROM",
          "   policydetails p",
          "   LEFT JOIN claimsdetails d ON p.policyid = d.policyid AND p.effectivedate = d.effectivedate",
          "   LEFT JOIN interestedparties i ON p.policyid = i.policyid AND p.effectivedate = i.effectivedate",
          "   LEFT JOIN phonenumber pn ON p.entityid = pn.entityid",
          "   LEFT JOIN emails em ON p.entityid = em.entityid"
  })
  @Results({
          @Result(column = "total", property = "total", jdbcType = JdbcType.INTEGER),
  })
  PolicyOverComingSize getComingPolicyListSize();


  @Select({
          " <script>",
          " <if test='fromRenewalDate != null'>",
          " SELECT A.* FROM ( ",
          " </if> ",
          " SELECT POLICYNUMBER, ",
          "       POLICYID, ",
          "       PRODUCTID, ",
          "       ENTITYID AS AGENTID, ",
          "       AGENT_NAME, ",
          "       EFFECTIVEDATE AS STARTDATE, ",
          "       EFFECTIVEDATE, ",
          "       POLICYHOLDERID, ",
          "       POLICYHOLDERNAME, ",
          "  CONVERT(DATE, RENEWALDATE) AS RENEWALDATE,",
          "       POLICYAMOUNT, ",
          "       PAYMENTMETHOD, ",
          "       POLICYSTATUS, ",
          "       PHONENUMBER   ",
          "  FROM INTERMEDIARY_POLICYLISTTABLE ",
          " WHERE ENTITYID = #{agentId, jdbcType=INTEGER} ",
          "       AND PRODUCTID = 49 ",
          "       AND POLICYSTATUS IN ('L', 'RL') ",
          " <if test='filter != null and filter != \"\"'>",
          " AND (POLICYHOLDERNAME like concat(concat('%', #{filter,jdbcType=VARCHAR}),'%') ",
          "  or POLICYNUMBER like concat(concat('%', #{filter,jdbcType=VARCHAR}),'%')) ",
          " </if> ",
          " <if test='policyHolderId != null'>",
          " AND POLICYHOLDERID = #{policyHolderId,jdbcType=INTEGER} ",
          " </if> ",
          " <if test='fromRenewalDate != null'>",
          " ) as A",
          " WHERE RENEWALDATE &gt;= #{fromRenewalDate,jdbcType=TIMESTAMP} ",
          " </if> ",
          " <if test='fromRenewalDate != null and toRenewalDate != null'>",
          " AND RENEWALDATE &lt; #{toRenewalDate,jdbcType=TIMESTAMP} ",
          " </if> ",
          " <if test='sortColumn != null and sortColumn != \"\"'>",
          " ORDER BY ${sortColumn} ${sort}",
          " </if> ",
          " </script>"
  })
  @Results(id = "intermediaryPoliciesList", value = {
          @Result(column = "POLICYNUMBER", property = "policyNumber", jdbcType = JdbcType.VARCHAR),
          @Result(column = "POLICYID", property = "policyId", jdbcType = JdbcType.INTEGER),
          @Result(column = "PRODUCTID", property = "productId", jdbcType = JdbcType.INTEGER),
          @Result(column = "AGENTID", property = "agentId", jdbcType = JdbcType.INTEGER),
          @Result(column = "AGENT_NAME", property = "agentName", jdbcType = JdbcType.VARCHAR),
          @Result(column = "STARTDATE", property = "startDate", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "EFFECTIVEDATE", property = "effectiveDate", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "POLICYHOLDERID", property = "policyHolderId", jdbcType = JdbcType.INTEGER),
          @Result(column = "POLICYHOLDERNAME", property = "policyHolderName", jdbcType = JdbcType.VARCHAR),
          @Result(column = "RENEWALDATE", property = "renewalDate", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "POLICYAMOUNT", property = "policyAmount", jdbcType = JdbcType.DECIMAL),
          @Result(column = "PAYMENTMETHOD", property = "paymentMethod", jdbcType = JdbcType.VARCHAR),
          @Result(column = "POLICYSTATUS", property = "policyStatus", jdbcType = JdbcType.VARCHAR),
          @Result(column = "PHONENUMBER", property = "phoneNumber", jdbcType = JdbcType.VARCHAR),

  })
  List<ke.co.apollo.health.policy.model.Policy> getPolicyListByIntermediary(IntermediaryPolicyDetailsRequest request);
}
