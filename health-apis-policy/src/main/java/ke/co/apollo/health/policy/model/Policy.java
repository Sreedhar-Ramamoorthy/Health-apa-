package ke.co.apollo.health.policy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Policy {

  private Integer policyId;
  private String policyNumber;
  private Integer productId;
  private Integer agentId;
  private String agentName;
  private Date startDate;
  private Date effectiveDate;
  private Integer policyHolderId;
  private String policyHolderName;
  private Date renewalDate;
  private BigDecimal policyAmount;
  private String paymentMethod;
  private String policyStatus;
  private String regNo;
  private String coverType;
  private Long coverTypeId;
  private String currentStatus;
  private String proDesc;
  private String toRenew;
  private String installment;
  private BigDecimal amountToPay;
  private Date installmentDate;
  private Integer installmentNo;
  private String phoneNumber;

}
