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
public class PolicyAdjustment {

  private Integer policyId;
  private Date effectiveDate;
  private BigDecimal policyAmount;
  private BigDecimal adjustment;

}
