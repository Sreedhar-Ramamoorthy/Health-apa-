package ke.co.apollo.health.common.domain.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComingPolicyListRequest {

  @Default
  private Integer index = 0;
  @Default
  private Integer limit = 10;
  private Date startDate;
  private Date endDate;
}
