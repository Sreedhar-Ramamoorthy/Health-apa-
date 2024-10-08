package ke.co.apollo.health.common.domain.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyOverComing {

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
  private Date CaptureDate;

}
