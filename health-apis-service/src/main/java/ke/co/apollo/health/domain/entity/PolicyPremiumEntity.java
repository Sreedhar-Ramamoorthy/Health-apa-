package ke.co.apollo.health.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "policy_premium")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class PolicyPremiumEntity {

  @Id
  @Column(name = "id", length = 50)
  @GeneratedValue(generator = "jpa-uuid")
  private String id;

  @Column(name = "policy_id", length = 11)
  private Integer policyId;

  @Column(name = "policy_number", length = 50)
  private String policyNumber;

  @Column(name = "effective_date")
  private Date effectiveDate;

  @Column(name = "quote_id", length = 50)
  private String quoteId;

  @Column(name = "customer_id", length = 50)
  private String customerId;

  @Column(name = "entity_id", length = 30)
  private Long entityId;

  @Column(name = "name", length = 100)
  private String name;

  @Column(name = "relationship", length = 50)
  private String relationship;

  @Column(name = "age", length = 10)
  private Integer age;

  @Column(name = "benefit_type", length = 30)
  private String benefitType;

  @Column(name = "benefit_limit", columnDefinition = "decimal(20,2)")
  private BigDecimal benefitLimit;

  @Column(name = "premium", columnDefinition = "decimal(20,2)")
  private BigDecimal premium;

  @CreatedDate
  @JsonIgnore
  @Column(name = "create_time", updatable = false, nullable = false)
  private Date createTime;

  @LastModifiedDate
  @JsonIgnore
  @Column(name = "update_time", nullable = false)
  private Date updateTime;
}
