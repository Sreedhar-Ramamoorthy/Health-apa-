package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerByEntityIdRequest {

  @NotNull
  private Long entityId;

  private String agentId;

}
