package ke.co.apollo.health.common.domain.model.remote;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailsResponse implements java.io.Serializable {

  private static final long serialVersionUID = 3583322161285990943L;
  private String accountReference;
  private BigDecimal amount;
  private String checkoutRequestId;
  private String createdAt;
  private String customerMessage;
  private Long id;
  private String merchantRequestId;
  private String mpesaReceiptNumber;
  private String msisdn;
  private String paymentStatus;//"enum": ["PENDING","SUCCESSFUL","FAILED"]
  private String requestType; //"enum": ["DEPOSIT","PAYMENT"]
  private Long responseCode;
  private String responseDescription;
  private Long resultCode; // For successful payment, the resultCode will always be 0
  private String resultDescription;
  private String serviceRequestStatus; // "enum": ["PENDING","COMPLETED","FAILED"]
  private String serviceType; //"enum": ["MOTOR","LIFE"]
  private Long shortCode;
  private String transactionDate;
  private String transactionDescription;
  private String transactionType;
  private String updatedAt;

}
