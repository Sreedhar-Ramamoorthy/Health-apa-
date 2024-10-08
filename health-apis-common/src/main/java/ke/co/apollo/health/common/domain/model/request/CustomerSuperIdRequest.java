package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSuperIdRequest {

  @NotNull(message = "super customerId is mandatory")
  private String superCustomerId;

}
