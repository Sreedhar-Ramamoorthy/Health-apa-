package ke.co.apollo.health.common.domain.model.response;

import java.math.BigDecimal;
import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.Premium;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreateResponse {

  private String customerId;
  private QuoteBean quote;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class QuoteBean {

    private String quoteId;
    private String quoteNumber;
    private String productId;
    private String startDate;
    private String status;
    private Benefit benefit;
    private Premium premium;
    private BigDecimal balance;

  }
}
