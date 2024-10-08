package ke.co.apollo.health.common.domain.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRequest {

  private String idNumber;
  private Integer idType;

}
