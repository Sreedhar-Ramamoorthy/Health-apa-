package ke.co.apollo.health.common.domain.model.response;

import ke.co.apollo.health.common.domain.model.EntityDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEntityDetailsResponse extends ASAPIResponse {

  private EntityDetails entityDetails;


}
