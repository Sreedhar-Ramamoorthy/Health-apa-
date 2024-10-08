package ke.co.apollo.health.common.domain.model.request;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {


  private String customerId;
  private String paymentPhoneNumber;
  @NotBlank
  private String amount;
  private String paymentMethods;
  private String premium;
  private String quoteId;
  @NotNull
  private Boolean renewal;
  private String policyNumber;
  private Date effectiveDate;

}
