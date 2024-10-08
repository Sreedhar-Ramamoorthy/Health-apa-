package ke.co.apollo.health.domain;

import java.math.BigDecimal;
import ke.co.apollo.health.common.enums.BenefitEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BenefitPremium {

  private BenefitEnum benefitType;
  private Integer benefitLimit;
  private BigDecimal premium;

}
