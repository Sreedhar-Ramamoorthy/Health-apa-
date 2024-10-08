package ke.co.apollo.health.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyClaim implements Serializable {

  private BigDecimal claimsPaid;
  private BigDecimal earnedPremium;
  private int noClaimYear;
}
