package ke.co.apollo.health.mapper.health;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Children;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.Dependant;
import ke.co.apollo.health.common.domain.model.DependantBenefit;
import ke.co.apollo.health.common.domain.model.DependantDetails;
import ke.co.apollo.health.domain.request.DependantAddRequest;
import ke.co.apollo.health.mapper.health.typehandler.ChildrenTypeHandler;
import ke.co.apollo.health.mapper.health.typehandler.DependantBenefitTypeHandler;
import ke.co.apollo.health.mapper.health.typehandler.DependantTypeHandler;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CustomerMapper {

  @Delete({
      "delete from customer",
      "where customer_id = #{customerId,jdbcType=INTEGER}"
  })
  int deleteByPrimaryKey(Integer customerId);

  @Delete({
          "delete from customer",
          "where parent_id = #{customerId,jdbcType=INTEGER}"
  })
  int removeTemporary(String customerId);

  @Insert({
      "insert into customer (customer_id,agent_id, ",
      "first_name, last_name, ",
      "date_of_birth, title, ",
      "gender, phone_number, ",
      "email,super_customer_id,entity_id, relationship_desc)",
      "values (#{customerId,jdbcType=VARCHAR},#{agentId,jdbcType=VARCHAR}, ",
      "#{firstName,jdbcType=VARCHAR}, #{lastName,jdbcType=VARCHAR}, ",
      "#{dateOfBirth,jdbcType=TIMESTAMP}, #{title,jdbcType=VARCHAR}, ",
      "#{gender,jdbcType=VARCHAR}, #{phoneNumber,jdbcType=VARCHAR}, ",
      "#{email,jdbcType=VARCHAR},#{superCustomerId,jdbcType=VARCHAR},",
      "#{entityId,jdbcType=INTEGER},#{relationshipDesc,jdbcType=VARCHAR} )"
  })
  @SelectKey(keyProperty = "customerId", resultType = String.class, before = true,
      statement = "select MD5(uuid());")
  int addCustomer(Customer record);

  @Update({
      "update customer",
      "set start_date = #{startDate,jdbcType=TIMESTAMP},",
      "spouse_summary = #{spouse,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.DependantTypeHandler},",
      "children_summary = #{children,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.ChildrenTypeHandler},",
      "benefit = #{benefit,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.DependantBenefitTypeHandler}",
      "where customer_id = #{customerId,jdbcType=VARCHAR}"
  })
  int addCustomerDependant(DependantAddRequest record);

  @Select({
      "select",
      "customer_id, parent_id, agent_id, entity_id, quote_id, super_customer_id, first_name, ",
      "last_name, date_of_birth, title, gender, phone_number, email, start_date, relationship_desc, ",
      "spouse_summary, children_summary, benefit,id_no,kra_pin,create_time,update_time",
      "from customer",
      "where customer_id = #{customerId,jdbcType=INTEGER}"
  })
  @Results(id = "customerDomainResult", value = {
      @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "parent_id", property = "parentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "agent_id", property = "agentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "quote_id", property = "quoteId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "super_customer_id", property = "superCustomerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "date_of_birth", property = "dateOfBirth", jdbcType = JdbcType.DATE),
      @Result(column = "title", property = "title", jdbcType = JdbcType.VARCHAR),
      @Result(column = "gender", property = "gender", jdbcType = JdbcType.VARCHAR),
      @Result(column = "phone_number", property = "phoneNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
      @Result(column = "start_date", property = "startDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "relationship_desc", property = "relationshipDesc", jdbcType = JdbcType.VARCHAR),
      @Result(column = "spouse_summary", property = "spouseSummary", jdbcType = JdbcType.VARCHAR, javaType = Dependant.class, typeHandler = DependantTypeHandler.class),
      @Result(column = "children_summary", property = "childrenSummary", jdbcType = JdbcType.VARCHAR, javaType = Children.class, typeHandler = ChildrenTypeHandler.class),
      @Result(column = "benefit", property = "benefit", jdbcType = JdbcType.VARCHAR, javaType = DependantBenefit.class, typeHandler = DependantBenefitTypeHandler.class),
      @Result(column = "id_no", property = "idNo", jdbcType = JdbcType.VARCHAR),
      @Result(column = "kra_pin", property = "kraPin", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  Customer getCustomerByCustomerId(String customerId);

  @Select({
          "select",
          "customer_id, parent_id, agent_id, entity_id, quote_id, super_customer_id, first_name, ",
          "last_name, date_of_birth, title, gender, phone_number, email, start_date, relationship_desc, ",
          "spouse_summary, children_summary, benefit,id_no,kra_pin,create_time,update_time",
          "from customer",
          "where parent_id = #{parentId,jdbcType=INTEGER}"
  })
  @Results(id = "customerDomainResultParent", value = {
          @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR, id = true),
          @Result(column = "parent_id", property = "parentId", jdbcType = JdbcType.VARCHAR),
          @Result(column = "agent_id", property = "agentId", jdbcType = JdbcType.VARCHAR),
          @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.INTEGER),
          @Result(column = "quote_id", property = "quoteId", jdbcType = JdbcType.VARCHAR),
          @Result(column = "super_customer_id", property = "superCustomerId", jdbcType = JdbcType.VARCHAR),
          @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
          @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
          @Result(column = "date_of_birth", property = "dateOfBirth", jdbcType = JdbcType.DATE),
          @Result(column = "title", property = "title", jdbcType = JdbcType.VARCHAR),
          @Result(column = "gender", property = "gender", jdbcType = JdbcType.VARCHAR),
          @Result(column = "phone_number", property = "phoneNumber", jdbcType = JdbcType.VARCHAR),
          @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
          @Result(column = "start_date", property = "startDate", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "relationship_desc", property = "relationshipDesc", jdbcType = JdbcType.VARCHAR),
          @Result(column = "spouse_summary", property = "spouseSummary", jdbcType = JdbcType.VARCHAR, javaType = Dependant.class, typeHandler = DependantTypeHandler.class),
          @Result(column = "children_summary", property = "childrenSummary", jdbcType = JdbcType.VARCHAR, javaType = Children.class, typeHandler = ChildrenTypeHandler.class),
          @Result(column = "benefit", property = "benefit", jdbcType = JdbcType.VARCHAR, javaType = DependantBenefit.class, typeHandler = DependantBenefitTypeHandler.class),
          @Result(column = "id_no", property = "idNo", jdbcType = JdbcType.VARCHAR),
          @Result(column = "kra_pin", property = "kraPin", jdbcType = JdbcType.VARCHAR),
          @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
          @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  List<Customer> getCustomerByParentId(String parentId);


  @Select({
          "select ",
          "customer_id, parent_id, agent_id, entity_id, quote_id, super_customer_id, first_name, ",
          "last_name, date_of_birth, title, gender, phone_number, email, start_date, relationship_desc, ",
          " spouse_summary, children_summary, benefit,id_no,kra_pin,create_time,update_time",
          " from customer ",
          " where parent_id = #{parentId,jdbcType=VARCHAR} and relationship_desc = #{relationship,jdbcType=VARCHAR}"
  })
  @Results(id = "customerDomainSpouseResult", value = {
          @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR, id = true),
          @Result(column = "parent_id", property = "parentId", jdbcType = JdbcType.VARCHAR),
          @Result(column = "agent_id", property = "agentId", jdbcType = JdbcType.VARCHAR),
          @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.INTEGER),
          @Result(column = "quote_id", property = "quoteId", jdbcType = JdbcType.VARCHAR),
          @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  Customer getSpouseByParentId(String parentId, String relationship);


  @Select({
      "select",
      "customer_id, parent_id, agent_id, entity_id, quote_id, super_customer_id, first_name, ",
      "last_name, date_of_birth, title, gender, phone_number, email, start_date, relationship_desc, ",
      "spouse_summary, children_summary, benefit,id_no,kra_pin,create_time,update_time",
      "from customer",
      "where super_customer_id = #{superId,jdbcType=VARCHAR}"
  })
  @Results(id = "customerSuperResult", value = {
      @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "parent_id", property = "parentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "agent_id", property = "agentId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.INTEGER),
      @Result(column = "quote_id", property = "quoteId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "super_customer_id", property = "superCustomerId", jdbcType = JdbcType.VARCHAR),
      @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "date_of_birth", property = "dateOfBirth", jdbcType = JdbcType.DATE),
      @Result(column = "title", property = "title", jdbcType = JdbcType.VARCHAR),
      @Result(column = "gender", property = "gender", jdbcType = JdbcType.VARCHAR),
      @Result(column = "phone_number", property = "phoneNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
      @Result(column = "start_date", property = "startDate", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "relationship_desc", property = "relationshipDesc", jdbcType = JdbcType.VARCHAR),
      @Result(column = "spouse_summary", property = "spouseSummary", jdbcType = JdbcType.VARCHAR, javaType = Dependant.class, typeHandler = DependantTypeHandler.class),
      @Result(column = "children_summary", property = "childrenSummary", jdbcType = JdbcType.VARCHAR, javaType = Children.class, typeHandler = ChildrenTypeHandler.class),
      @Result(column = "benefit", property = "benefit", jdbcType = JdbcType.VARCHAR, javaType = DependantBenefit.class, typeHandler = DependantBenefitTypeHandler.class),
      @Result(column = "id_no", property = "idNo", jdbcType = JdbcType.VARCHAR),
      @Result(column = "kra_pin", property = "kraPin", jdbcType = JdbcType.VARCHAR),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)
  })
  Customer getCustomerBySuperId(String superId);

  @Select("<script> "
      + "select "
      + "customer_id, first_name, "
      + "last_name, date_of_birth,relationship_desc,title,gender,entity_id "
      + "from customer "
      + "where parent_id = #{customerId,jdbcType=VARCHAR} "
      + "<if test='quoteId != null'> and quote_id= #{quoteId,jdbcType=VARCHAR} </if> "
      + " </script> ")
  @Results({
      @Result(column = "customer_id", property = "dependantCode", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "date_of_birth", property = "dateOfBirth", jdbcType = JdbcType.DATE),
      @Result(column = "relationship_desc", property = "relationship", jdbcType = JdbcType.VARCHAR),
      @Result(column = "title", property = "title", jdbcType = JdbcType.VARCHAR),
      @Result(column = "gender", property = "gender", jdbcType = JdbcType.VARCHAR),
      @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.INTEGER)
  })
  List<Dependant> getCustomerDependant(String customerId, String quoteId);

  @Select("<script> "
          + "select "
          + "customer_id, first_name, "
          + "last_name, date_of_birth,relationship_desc,title,gender,phone_number,email,entity_id "
          + "from customer "
          + "where parent_id = #{customerId,jdbcType=VARCHAR} "
          + "<if test='quoteId != null'> and quote_id= #{quoteId,jdbcType=VARCHAR} </if> "
          + " </script> ")
  @Results({
      @Result(column = "customer_id", property = "dependantCode", jdbcType = JdbcType.VARCHAR, id = true),
      @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
      @Result(column = "date_of_birth", property = "dateOfBirth", jdbcType = JdbcType.DATE),
      @Result(column = "relationship_desc", property = "relationship", jdbcType = JdbcType.VARCHAR),
      @Result(column = "title", property = "title", jdbcType = JdbcType.VARCHAR),
      @Result(column = "gender", property = "gender", jdbcType = JdbcType.VARCHAR),
      @Result(column = "phone_number", property = "phoneNumber", jdbcType = JdbcType.VARCHAR),
      @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
      @Result(column = "entity_id", property = "entityId", jdbcType = JdbcType.INTEGER)
  })
  List<DependantDetails> getCustomerDependantDetails(String customerId, String quoteId);

  @Select("<script> "
      + "select "
      + "quote_id "
      + "from customer "
      + "where parent_id = #{customerId,jdbcType=VARCHAR} "
      + " </script> ")
  @Results({
      @Result(column = "quote_id", property = "quoteId", jdbcType = JdbcType.VARCHAR)
  })
  List<String> getCustomerDependantQuoteList(String customerId);


  @Update("<script> "
      + "update customer "
      + "set "
      + "<if test='agentId != null'> agent_id = #{agentId,jdbcType=VARCHAR},</if> "
      + "<if test='childrenSummary != null'> children_summary = #{childrenSummary,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.ChildrenTypeHandler},</if> "
      + "<if test='spouseSummary != null'> spouse_summary = #{spouseSummary,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.DependantTypeHandler},</if> "
       + "<if test='firstName != null'> first_name = #{firstName,jdbcType=VARCHAR},</if> "
      + "<if test='lastName != null'> last_name = #{lastName,jdbcType=VARCHAR},</if> "
      + "<if test='dateOfBirth != null'> date_of_birth = #{dateOfBirth,jdbcType=TIMESTAMP},</if> "
      + "<if test='title != null'> title = #{title,jdbcType=VARCHAR}, </if> "
      + "<if test='gender != null'> gender = #{gender,jdbcType=VARCHAR}, </if> "
      + "<if test='phoneNumber != null'> phone_number = #{phoneNumber,jdbcType=INTEGER},</if> "
      + "<if test='email != null'> email = #{email,jdbcType=VARCHAR},</if> "
      + "<if test='idNo != null'> id_no = #{idNo,jdbcType=VARCHAR},</if> "
      + "kra_pin = #{kraPin,jdbcType=VARCHAR} "
      + "where customer_id = #{customerId,jdbcType=INTEGER}"
      + " </script> ")
  int updatePrincipalCustomer(Customer record);

  @Update("<script> "
      + "update customer "
      + "set "
      + "<if test='agentId != null'> agent_id = #{agentId,jdbcType=VARCHAR},</if> "
      + "first_name = #{firstName,jdbcType=VARCHAR},"
      + "last_name = #{lastName,jdbcType=VARCHAR},"
      + "date_of_birth = #{dateOfBirth,jdbcType=TIMESTAMP},"
      + "title = #{title,jdbcType=VARCHAR},"
      + "gender = #{gender,jdbcType=VARCHAR},"
      + "phone_number = #{phoneNumber,jdbcType=INTEGER},"
      + "email = #{email,jdbcType=VARCHAR},"
      + "id_no = #{idNo,jdbcType=VARCHAR},"
      + "kra_pin = #{kraPin,jdbcType=VARCHAR} "
      + "where customer_id = #{customerId,jdbcType=INTEGER}"
      + " </script> ")
  int updateDependant(Customer record);

  @Insert(" <script>"
      + "insert into customer (customer_id, "
      + "first_name, last_name, "
      + "date_of_birth, title, "
      + "gender, parent_id, "
      + "relationship_desc,quote_id) "
      + "select MD5(uuid()), A.* from ( "
      + "<foreach collection=\"dependantList\" item=\"item\" index=\"index\" separator=\"union all\">"
      + "select  "
      + "#{item.firstName,jdbcType=VARCHAR} as first_name, #{item.lastName,jdbcType=VARCHAR} as last_name, "
      + "#{item.dateOfBirth,jdbcType=TIMESTAMP} as date_of_birth, #{item.title,jdbcType=VARCHAR} as title, "
      + "#{item.gender,jdbcType=VARCHAR} as gender, #{item.parentId ,jdbcType=VARCHAR} as parent_id, "
      + "#{item.relationshipDesc,jdbcType=VARCHAR} as relationship_desc, #{item.quoteId ,jdbcType=VARCHAR} as quote_id"
      + "</foreach> )A "
      + " </script>")
  @SelectKey(keyProperty = "customerId", resultType = String.class, before = true,
      statement = "select MD5(uuid());")
  int addDependant(List<Customer> dependantList);

  @Delete({"<script> "
      + "delete from customer "
      + "where parent_id = #{customerId,jdbcType=VARCHAR} "
      + "<if test='agentId != null'> and agent_id = #{agentId,jdbcType=VARCHAR} </if> "
      + "<if test='quoteId != null'> and quote_id = #{quoteId,jdbcType=VARCHAR} </if> "
      + " </script>"
  })
  int deleteDependants(String customerId, String quoteId, String agentId);

  @Delete({"<script> "
          + "delete from customer "
          + "where customer_id = #{dependentId,jdbcType=VARCHAR} "
          + " </script>"
  })
  int deleteDependantsByCustomerId(String dependentId);

  @Update({"<script> "
          + " update customer set children_summary = null "
          + " where customer_id = #{customerId, jdbcType=VARCHAR} "
          + " </script>"
  })
  int removeChildFromPrincipal(String customerId);

  @Update({"<script> "
          + " update customer set spouse_summary = null  where customer_id = #{customerId, jdbcType=VARCHAR} "
          + " </script>"
  })
  void removeSpouseFromPrincipal(String customerId);

  @Update({"<script> "
          + "update customer "
          + "set children_summary = #{children_summary, jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.ChildrenTypeHandler} "
          + "where customer_id = #{principalCustomerId, jdbcType=VARCHAR} "
          + "</script>"
  })
  int updateNumberOfChildren(Children children_summary, String principalCustomerId);
  @Update({
      "update customer ",
      "set entity_id = #{entityId,jdbcType=INTEGER}",
      "where customer_id = #{customerId,jdbcType=VARCHAR}"
  })
  int appendEntityIdToCustomer(String customerId, Long entityId);

  @Select({
      "select",
      "customer_id, parent_id, agent_id, entity_id, quote_id, super_customer_id, first_name, ",
      "last_name, date_of_birth, title, gender, phone_number, email, start_date, relationship_desc, ",
      "spouse_summary, children_summary, benefit,id_no,kra_pin,create_time,update_time",
      "from customer",
      "where customer_id = #{customerId,jdbcType=VARCHAR}",
      "union",
      "select",
      "customer_id, parent_id, agent_id, entity_id, quote_id, super_customer_id, first_name, ",
      "last_name, date_of_birth, title, gender, phone_number, email, start_date, relationship_desc, ",
      "spouse_summary, children_summary, benefit,id_no,kra_pin,create_time,update_time",
      "from customer",
      "where parent_id = #{customerId,jdbcType=VARCHAR} and quote_id = #{quoteId,jdbcType=VARCHAR}"
  })
  @ResultMap("customerDomainResult")
  List<Customer> getCustomerAndDependants(String customerId, String quoteId);

  @Select({"<script> ",
      "select",
      "customer_id, parent_id, agent_id, entity_id, quote_id, super_customer_id, first_name, ",
      "last_name, date_of_birth, title, gender, phone_number, email, start_date, relationship_desc, ",
      "spouse_summary, children_summary, benefit,id_no,kra_pin,create_time,update_time",
      "from customer",
      "where 1=1 ",
      "<if test='phoneNumber != null and phoneNumber != \"\"'> and phone_number = #{phoneNumber,jdbcType=VARCHAR} </if> ",
      "<if test='agentId != null and agentId != \"\"'> and agent_id = #{agentId,jdbcType=VARCHAR} </if> ",
      "<if test='entityId != null '> and entity_id = #{entityId,jdbcType=INTEGER} </if> ",
      " </script>"
  })
  @ResultMap("customerDomainResult")
  List<Customer> getCustomerList(Customer record);


  @Insert({
      "insert into customer (customer_id,agent_id, ",
      "first_name, last_name, ",
      "date_of_birth, title, relationship_desc, ",
      "gender, spouse_summary,",
      "children_summary,benefit)",
      "values (#{customerId,jdbcType=VARCHAR},#{agentId,jdbcType=VARCHAR}, ",
      "#{firstName,jdbcType=VARCHAR}, #{lastName,jdbcType=VARCHAR}, ",
      "#{dateOfBirth,jdbcType=TIMESTAMP}, #{title,jdbcType=VARCHAR}, #{relationshipDesc,jdbcType=VARCHAR}, ",
      "#{gender,jdbcType=VARCHAR}, #{spouseSummary,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.DependantTypeHandler},",
      "#{childrenSummary,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.ChildrenTypeHandler}, ",
      "#{benefit,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.DependantBenefitTypeHandler})"
  })
  @SelectKey(keyProperty = "customerId", resultType = String.class, before = true,
      statement = "select MD5(uuid());")
  @Options(useGeneratedKeys = true, keyProperty = "customerId")
  int createCustomer(Customer record);


  @Update({"<script> ",
          "update customer ",
          "set ",
          "<if test='phoneNumber != null and phoneNumber != \"\"'> phone_number = #{phoneNumber,jdbcType=VARCHAR} </if>",
          "<if test='superCustomerId != null and superCustomerId != \"\"'> super_customer_id = #{superCustomerId,jdbcType=VARCHAR} </if>",
          "where customer_id = #{customerId,jdbcType=VARCHAR}",
          " </script>"
  })
  int upgradeCustomer(Customer record);

  @Update("<script> "
          +"update customer "
          +"set "
          +"first_name = #{firstName,jdbcType=VARCHAR},"
          +"last_name = #{lastName,jdbcType=VARCHAR},"
          +"date_of_birth = #{dateOfBirth,jdbcType=VARCHAR},"
          +"title = #{title,jdbcType=VARCHAR},"
          +"gender = #{gender,jdbcType=VARCHAR},"
          +"spouse_summary = #{spouseSummary,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.DependantTypeHandler},"
          +"children_summary = #{childrenSummary,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.ChildrenTypeHandler},"
          +"benefit = #{benefit,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.DependantBenefitTypeHandler},"
          +"relationship_desc = #{relationshipDesc,jdbcType=VARCHAR},"
          +"phone_number = #{phoneNumber,jdbcType=VARCHAR},"
          +"super_customer_id = #{superCustomerId,jdbcType=VARCHAR} "
          +"where customer_id = #{customerId,jdbcType=VARCHAR}"
          +" </script>")
  int updateCustomer(Customer record);


  @Update({
          "update customer " +
          " set spouse_summary = #{spouseSummary,jdbcType=VARCHAR, typeHandler=ke.co.apollo.health.mapper.health.typehandler.DependantTypeHandler} " +
          " where customer_id = #{customerId,jdbcType=VARCHAR}"
  })
  int updateSpouseSummary(Dependant spouseSummary, String customerId);
}
