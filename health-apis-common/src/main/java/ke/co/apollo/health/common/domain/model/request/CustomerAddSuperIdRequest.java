package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAddSuperIdRequest {

  @NotBlank(message = "customerId is mandatory")
  private String customerId;
  @NotBlank(message = "super customerId is mandatory")
  private String superCustomerId;

}
