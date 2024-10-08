package ke.co.apollo.health.common.domain.model.remote;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityDetailRequest {

  @NotNull
  private Integer entityId;

}
