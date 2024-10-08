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
public class PaymentValidateRequest {

  @NotBlank
  @Length(min = 1, max = 50)
  private String customerId;
  @NotBlank
  @Length(min = 1, max = 50)
  private String orderId;
  @NotBlank
  @Length(min = 1, max = 50)
  private String transactionRef;
  @NotBlank
  @Length(min = 1, max = 50)
  private String mpesaRefOrCheckoutId;

  private String mpesaAccountReference;
  @NotBlank
  @Length(min = 1, max = 1000)
  private String paymentResponse;
  @NotBlank
  @Length(min = 1, max = 50)
  private String paymentMethod;
  @Length(max = 50)
  private String externalReference;
  @NotNull
  private boolean success;
  @Length(max = 1000)
  private String message;

}
