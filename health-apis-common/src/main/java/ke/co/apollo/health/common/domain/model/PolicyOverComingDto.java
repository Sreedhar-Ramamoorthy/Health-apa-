package ke.co.apollo.health.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyOverComingDto {

  private String policyNumber;
  private Date effectiveDate;
  private String principalName;
  private String email;
  private String agentName;
  private String asagentId;
  private String plan;
  private String mobile;
  private String policyAmount;
  private BigDecimal claims;
  private BigDecimal earnedPremium;
  private BigDecimal lossRatio;
  private BigDecimal loadingPercentage;
  private BigDecimal loading;
  private BigDecimal changeInAgePremium;
  private BigDecimal discount;
  private BigDecimal premium;
  private BigDecimal totalPremium;
  private Date renewalDate;

}
