package ke.co.apollo.health.domain.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MpesaExpressRequest {
    private String kraPin = "A123456789O";
    @NotBlank
    private String customerPhoneNumber;
    private double payableAmount = 0;
    @NotBlank
    private String accountReference;
    private String description = "HappinessHealth";
    private String serviceType = "HEALTH";
    private String transactionType = "PAYMENT";
}