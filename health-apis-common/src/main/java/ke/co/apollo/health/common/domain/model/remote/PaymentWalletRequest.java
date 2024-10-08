package ke.co.apollo.health.common.domain.model.remote;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentWalletRequest {

  @Size(min = 1, max = 100)
  private String kraPin;
  @Size(min = 1, max = 100)
  private String customerPhoneNumber;
  @Size(min = 1, max = 100)
  private String description;
  @Size(min = 1, max = 100)
  private String payableAmount;
  @Size(min = 1, max = 100)
  private String serviceType;
  @Size(min = 1, max = 100)
  private String accountReference;
  @Size(min = 1, max = 100)
  private String transactionType;

}
