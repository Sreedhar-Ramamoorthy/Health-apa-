package ke.co.apollo.health.common.domain.model.request;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyRenewalBalanceRequest extends PolicyRenewalRequest {

  @NotNull
  @Positive
  private BigDecimal paidAmount;

}
