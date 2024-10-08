package ke.co.apollo.health.common.domain.model.response;

import java.math.BigDecimal;
import ke.co.apollo.health.common.domain.model.RenewalPremium;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRenewalResponse {

  private RenewalPremium premium;

  private BigDecimal balance;

}
