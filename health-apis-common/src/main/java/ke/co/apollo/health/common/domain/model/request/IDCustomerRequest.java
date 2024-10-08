package ke.co.apollo.health.common.domain.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IDCustomerRequest {

  private String idNumber;
  private String serialNumber;

}
