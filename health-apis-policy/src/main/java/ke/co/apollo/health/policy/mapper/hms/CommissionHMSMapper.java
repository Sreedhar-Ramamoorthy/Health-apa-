package ke.co.apollo.health.policy.mapper.hms;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import ke.co.apollo.health.policy.model.AgentCommission;
import ke.co.apollo.health.policy.model.Commission;



@Mapper
@Component
public interface CommissionHMSMapper {

  @Select(  
    {
    " WITH Commissions " +
    " AS (  SELECT entityID, " +
    "             Agent_Name, " +
    " CASE " +
    "    WHEN paymentStatus = 3 THEN COUNT (DISTINCT JournalID) ELSE 0 " +
    " END " +
    "   countPaid, " +
        
    " CASE  " +
    "   WHEN paymentStatus = 3 THEN SUM (nvl(net_commission,0)) ELSE 0 " +
    " END " +
    "   totalPaid, " +
    " CASE " +
    "   WHEN paymentStatus IN (2, 1) THEN COUNT (DISTINCT JournalID) ELSE 0 " +
    " END " +
    "   countDueProcessed, " +
    " CASE " +
    "   WHEN paymentStatus IN (2, 1) THEN SUM(nvl(net_commission,0)) ELSE 0 " +
    " END " +
    "   totalDueProcessed, " +
    " CASE " +
    "   WHEN paymentStatus IS NULL THEN COUNT (DISTINCT JournalID) ELSE 0 " +
    " END " +
    "   countDueNotProcessed, " +
    " CASE  " +
    " WHEN paymentStatus IS NULL THEN SUM (nvl(net_commission,0)) ELSE 0 " +
    " END " +
    "   totalDueNotProcessed    " +
    "         FROM commission_records " +
    "     GROUP BY entityid, Agent_Name, paymentStatus) " +
    " SELECT " +
    "     SUM (countPaid) countPaid, " +
    "     SUM (totalPaid) totalPaid, " +
    "     SUM (countDueProcessed) countDueProcessed, " +
    "     SUM (totalDueProcessed) totalDueProcessed, " +
    "     SUM (countDueNotProcessed) countDueNotProcessed, " +
    "     SUM (totalDueNotProcessed) totalDueNotProcessed " +
    " FROM Commissions " +
    " WHERE ENTITYID=#{agentId,jdbcType=INTEGER} " +
    " GROUP BY entityID, Agent_Name " 
    }
  )
  @Results({
      @Result(column = "countPaid", property = "countPaid", jdbcType = JdbcType.BIGINT),
      @Result(column = "totalPaid", property = "totalPaid", jdbcType = JdbcType.FLOAT),
      @Result(column = "countDueProcessed", property = "countDueProcessed", jdbcType = JdbcType.BIGINT),
      @Result(column = "totalDueProcessed", property = "totalDueProcessed", jdbcType = JdbcType.FLOAT),
      @Result(column = "countDueNotProcessed", property = "countDueNotProcessed", jdbcType = JdbcType.BIGINT),
      @Result(column = "totalDueNotProcessed", property = "totalDueNotProcessed", jdbcType = JdbcType.FLOAT)
  })
  List<Commission> getCommissions(@Param(value = "agentId") Integer agentId);



  @Select( 
   {
  " WITH COMMISSIONDETAILS " +
     " AS (SELECT E.JOURNALID," +
     "          J.DESCRIPTION," +
     "          J.EVENTTYPE," +
     "          I.ENTITYID," +
     "          PA.POLICYATTRIBUTEVALUE AS POLICYNUMBER," +
     "          P.POLICYID," +
     "          EN.TITLE || ' ' || EN.FIRSTNAME || ' ' || EN.SURNAME" +
     "             AS POLICYHOLDERNAME," +
     "          POLICYEFFECTIVEDATE," +
     "          POSTINGDATE," +
     "          CASE WHEN POSTINGTYPE = 2 THEN E.AMOUNT END AS CREDIT," +
     "          CASE WHEN POSTINGTYPE = 1 THEN E.AMOUNT END AS DEBIT," +
     "          ALLOCATIONSTATUS," +
     "          E.LINKREF," +
     "          PM.PAYMENTID," +
     "          PM.TRANSFERNUMBER," +
     "          PM.PAYMENTDATE," +
     "          NVL(A.COMPANYNAME,  A.TITLE || ' ' || A.FIRSTNAME || ' ' || A.SURNAME) AS AGENT_NAME" +
     "     FROM ENTITYROLEINFO I," +
     "          ACCOUNTENTRIES E," +
     "          JOURNAL J," +
     "          POLICY P," +
     "          ENTITY EN," +
     "          PAYMENT PM," +
     "          POLICYATTRIBUTE PA," +
     "          ENTITY A" +
     "    WHERE    INFOID = 1189" +
     "          AND ROLEID  IN ( 54,59)" +
     "          AND I.INFO = E.ACCOUNTNUMBER" +
     "          AND E.JOURNALID = J.JOURNALID" +
     "          AND E.POLICYID = P.POLICYID" +
     "          AND E.POLICYEFFECTIVEDATE = P.EFFECTIVEDATE" +
     "          AND P.PRODUCTID = 49" +
     "          AND EN.ENTITYID = P.POLICYHOLDERID" +
     "          AND PM.LINKREF(+) = E.LINKREF" +
     "          AND PM.LINKREF(+) <> 0" +
     "          AND PA.POLICYID = P.POLICYID" +
     "          AND PA.EFFECTIVEDATE = P.EFFECTIVEDATE" +
     "          AND PA.POLICYATTRIBUTEID = 18" +
     "          AND I.ENTITYID=A.ENTITYID" +
     "          AND J.EVENTTYPE = 10)" +
     " SELECT JOURNALID," +
     "   DESCRIPTION," +
     "   EVENTTYPE," +
     "   LINKREF," +
     "   ENTITYID," +
     "   AGENT_NAME," +
     "   POLICYNUMBER," +
     "   POLICYID," +
     "   POLICYEFFECTIVEDATE," +
     "   POSTINGDATE," +
     "   PAYMENTID," +
     "   TRANSFERNUMBER," +
     "   PAYMENTDATE," +
     "   SUM (CREDIT) AS CREDIT," +
     "   SUM (DEBIT) AS DEBIT," +
     "   SUM (CREDIT) - SUM (DEBIT) AS NET_COMMISSION" +
     " FROM COMMISSIONDETAILS" +
     " WHERE ENTITYID=#{agentId,jdbcType=INTEGER} "+
     " GROUP BY JOURNALID," +
     "   DESCRIPTION," +
     "   EVENTTYPE," +
     "   LINKREF," +
     "   ENTITYID," +
     "   POLICYNUMBER," +
     "   POLICYID," +
     "   POLICYEFFECTIVEDATE," +
     "   POSTINGDATE," +
     "   PAYMENTID," +
     "   TRANSFERNUMBER," +
     "   PAYMENTDATE," +
     "   AGENT_NAME" 
    //  " FETCH FIRST 10 ROWS ONLY"
    }
  )
  @Results({
      @Result(column = "JOURNALID", property = "journalId", jdbcType = JdbcType.BIGINT),
      @Result(column = "DESCRIPTION", property = "description", jdbcType = JdbcType.VARCHAR),
      @Result(column = "EVENTTYPE", property = "eventType", jdbcType = JdbcType.INTEGER),
      @Result(column = "LINKREF", property = "linkRef", jdbcType = JdbcType.BIGINT),
      @Result(column = "AGENT_NAME", property = "agentName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "ENTITYID", property = "entityId", jdbcType = JdbcType.BIGINT),
      @Result(column = "POLICYNUMBER", property = "policyNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "POLICYID", property = "policyId", jdbcType = JdbcType.BIGINT),
      @Result(column = "POLICYEFFECTIVEDATE", property = "policyEffectiveDate", jdbcType = JdbcType.DATE),
      @Result(column = "POSTINGDATE", property = "postingDate", jdbcType = JdbcType.DATE),
      @Result(column = "PAYMENTID", property = "paymentId", jdbcType = JdbcType.BIGINT),
      @Result(column = "TRANSFERNUMBER", property = "transferNumber", jdbcType = JdbcType.BIGINT),
      @Result(column = "PAYMENTDATE", property = "paymentDate", jdbcType = JdbcType.DATE),
      @Result(column = "CREDIT", property = "credit", jdbcType = JdbcType.FLOAT),
      @Result(column = "DEBIT", property = "debit", jdbcType = JdbcType.FLOAT),
      @Result(column = "NET_COMMISSION", property = "netCommission", jdbcType = JdbcType.FLOAT)


  })
  List<AgentCommission> getActisureCommissions(@Param(value = "agentId") Integer agentId);

  
}