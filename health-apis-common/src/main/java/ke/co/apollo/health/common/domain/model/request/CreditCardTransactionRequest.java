package ke.co.apollo.health.common.domain.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardTransactionRequest {
    @NotBlank
    private String referenceNumber;
    @NotBlank
    private String amount;
    @NotBlank
    private String billToForename;
    @NotBlank
    private String billToSurname;
    @NotBlank
    private String billToEmail;
    @NotBlank
    private String orderId;

}
