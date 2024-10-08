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
public class CustomerAddPhoneRequest {

  @NotBlank(message = "customerId is mandatory")
  private String customerId;
  private String agentId;
  private String quoteId;
  @NotBlank(message = "phone number is mandatory")
  private String phoneNumber;

}
