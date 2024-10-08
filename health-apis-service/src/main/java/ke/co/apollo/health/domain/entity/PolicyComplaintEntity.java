package ke.co.apollo.health.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "policy_complaint")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class PolicyComplaintEntity {

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

  @Column(name = "agent_id", length = 50)
  private String agentId;

  @Column(name = "customer_id", length = 50)
  private String customerId;

  @Column(name = "title", length = 100)
  private String title;

  @Column(name = "content", length = 1000)
  private String content;

  @CreatedDate
  @JsonIgnore
  @Column(name = "create_time", updatable = false, nullable = false)
  private Date createTime;

  @LastModifiedDate
  @JsonIgnore
  @Column(name = "update_time", nullable = false)
  private Date updateTime;
}
