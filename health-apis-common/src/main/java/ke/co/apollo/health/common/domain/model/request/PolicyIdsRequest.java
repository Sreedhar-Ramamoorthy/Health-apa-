package ke.co.apollo.health.common.domain.model.request;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyIdsRequest {

  @NotNull
  @Size(min = 1, max = 1000)
  private List<Integer> ids;

}
