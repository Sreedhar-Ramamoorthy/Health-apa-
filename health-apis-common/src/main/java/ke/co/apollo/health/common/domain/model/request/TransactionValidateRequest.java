package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionValidateRequest {

  @NotBlank
  private String applicationType;
  @NotBlank
  private String orderId;
  @NotBlank
  private String mpesaRefOrCheckoutId;
  private String transactionRef;
  @Length(max = 3000)
  private String paymentResponse;
  @NotBlank
  @Length(min = 1, max = 50)
  private String paymentMethod;

  private String resultExternalReference;

  private String mpesaAccountReference;
  @NotNull
  private boolean success;
  private String message;

}
