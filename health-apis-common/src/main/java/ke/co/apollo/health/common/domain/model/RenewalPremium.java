package ke.co.apollo.health.common.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RenewalPremium extends Premium {

  private BigDecimal loading;
  private BigDecimal loadingPercentage;
  private BigDecimal discount;
  private BigDecimal lossRatio;
  private BigDecimal earnedPremium;
  private BigDecimal claimsPaid;
  private BigDecimal manualAdjustment;
  private BigDecimal changeInAgePremium;

}
