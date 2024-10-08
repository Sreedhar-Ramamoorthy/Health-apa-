package ke.co.apollo.health.common.domain.model;

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
public class PaymentTransaction {

  private String id;

  private boolean renewal;

  private String customerId;

  private String orderId;

  private String transactionRef;

  private BigDecimal amount;

  private String merchantId;

  private String domain;

  private String preauth;

  private String terminalId;

  private String currency;

  private String paymentCustomerId;

  private String paymentMethod;

  private String quoteId;

  private String quoteNumber;

  private String policyId;

  private String policyNumber;

  private Date effectiveDate;

  private String status;

  private boolean clientResult;

  private String clientMessage;

  private String paymentMessage;

  private boolean balanceResult;

  private String balanceMessage;

  private Date createTime;

  private Date updateTime;

}
