package ke.co.apollo.health.common.domain.model.response;

import java.util.List;
import ke.co.apollo.health.common.domain.model.ApplicationRenewalPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRenewalPolicyListResponse {

  private List<ApplicationRenewalPolicy> list;
  private int total;

}
