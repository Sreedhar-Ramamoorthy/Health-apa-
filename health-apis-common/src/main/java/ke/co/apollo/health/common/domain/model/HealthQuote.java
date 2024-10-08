package ke.co.apollo.health.common.domain.model;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthQuote extends Common {

  private String id;
  private String code;
  private Long entityId;
  private Integer productId;
  private String customerId;
  private String agentId;
  private Date startDate;
  private Date effectiveDate;
  private Date renewalDate;
  private Benefit benefit;
  private String status;
  private String paymentStyle;
  private Integer extPolicyId;
  private String extPolicyNumber;
  private Premium premium;
  private BigDecimal balance;
  private String firstName;
  private String lastName;
  private String phoneNumber;

  private String quoteStatus;

}
