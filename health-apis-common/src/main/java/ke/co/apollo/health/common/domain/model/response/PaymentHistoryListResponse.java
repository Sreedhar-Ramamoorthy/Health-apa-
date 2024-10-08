package ke.co.apollo.health.common.domain.model.response;

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
public class PaymentHistoryListResponse {

  private String amount;

  private String paymentType;

  private BigDecimal premium;

  private String responseCode;

  private BigDecimal balance;

  private String status;

  private String paymentDate;

  private Date createTime;

  private Date updateTime;

}
