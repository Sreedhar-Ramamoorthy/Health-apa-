package ke.co.apollo.health.common.domain.model;

import java.io.Serializable;
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
public class Claim implements Serializable {

  private Long assessmentId;
  private Long policyId;
  private Date effectiveDate;
  private Long beneficiaryId;
  private Date treatmentDate;
  private String invoiceReference;
  private String invoiceStatus;
  private String admissionStatus;
  private String invoiceBenefit;
  private BigDecimal settledAmount;

}
