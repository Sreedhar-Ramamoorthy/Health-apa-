package ke.co.apollo.health.common.domain.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateRequest {

  private BigDecimal amount;
  private String applicationCustomerEmail;
  private String applicationCustomerId;
  private String applicationScene;
  private String applicationType;
  private String applicationPolicyNumber;

}
