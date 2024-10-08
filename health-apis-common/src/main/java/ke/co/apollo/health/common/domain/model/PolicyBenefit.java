package ke.co.apollo.health.common.domain.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyBenefit {

  private Integer policyId;
  private Date effectiveDate;
  private Integer benefitId;
  private String description;
  private Integer productId;
  private String benefit;
  private Integer limit;

}
