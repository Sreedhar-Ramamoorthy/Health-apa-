package ke.co.apollo.health.domain.response;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryResponse {

  private Date date;
  private BigDecimal premium;
  private String transactionRef;
  private String paymentMethod;

}
