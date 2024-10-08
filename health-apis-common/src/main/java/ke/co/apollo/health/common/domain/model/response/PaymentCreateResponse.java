package ke.co.apollo.health.common.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateResponse {

  private String merchantId;
  private String domain;
  private String preauth;
  private String transactionRef;
  private String terminalId;
  private String currency;
  private String orderId;
  private String customerId;

}
