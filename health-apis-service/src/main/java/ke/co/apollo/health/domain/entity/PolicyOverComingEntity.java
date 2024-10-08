package ke.co.apollo.health.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "policy_over_coming")
@EntityListeners(AuditingEntityListener.class)
public class PolicyOverComingEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String policyNumber;
  private String principalName;
  private Date renewalDate;
  private Date effectiveDate;
  private String plan;
  private String agentName;
  private String asagentId;
  private String policyAmount;
  private BigDecimal claims;
  private String email;
  private String mobile;
  private BigDecimal loading;
  private BigDecimal discount;
  private BigDecimal premium;
  private BigDecimal totalPremium;
  private boolean needToUpdate;

  private BigDecimal loadingPercentage;
  private BigDecimal earnedPremium;

  @Column(name = "change_in_age_premium",nullable = true)
  private BigDecimal changeInAgePremium;

  @CreatedDate
  @Column(name = "create_time",updatable = false,nullable = false)
  private Date createTime;

  @LastModifiedDate
  @Column(name = "update_time",nullable = false)
  private Date updateTime;

  @Column(name = "loss_ratio",nullable = true)
  private BigDecimal lossRatio;

}
