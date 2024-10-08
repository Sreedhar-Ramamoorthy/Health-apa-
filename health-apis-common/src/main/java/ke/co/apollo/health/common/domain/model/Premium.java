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
public class Premium {

  private BigDecimal premium;
  private BigDecimal itl;
  private BigDecimal phcf;
  private BigDecimal stampDuty;
  private BigDecimal totalPremium;

}
