package ke.co.apollo.health.common.domain.model.request;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteBenefitRequest {

  @NotNull
  private Integer policyId;

  @NotNull
  private Date effectiveDate;

}
