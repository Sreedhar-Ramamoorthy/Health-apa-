package ke.co.apollo.health.common.domain.model.request;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequest {

  @NotBlank
  @Length(min = 1, max = 50)
  private String customerId;
  @NotBlank
  @Positive
  private String amount;
  @Length(max = 50)
  private String quoteId;
  @NotNull
  private boolean renewal;
  @Length(max = 50)
  private String policyNumber;
  private Date effectiveDate;

}
