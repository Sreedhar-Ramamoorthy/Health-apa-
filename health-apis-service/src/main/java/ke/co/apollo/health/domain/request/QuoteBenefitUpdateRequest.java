package ke.co.apollo.health.domain.request;

import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import ke.co.apollo.health.common.domain.model.Benefit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QuoteBenefitUpdateRequest extends QuoteBaseRequest {

  @NotNull
  @Valid
  private Benefit benefit;

  @NotNull
  @PositiveOrZero
  private BigDecimal premium;
}
