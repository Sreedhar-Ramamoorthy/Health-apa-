package ke.co.apollo.health.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthPolicyListResponse {

  String productId;
  String productName;
  String policyNumber;
  @JsonFormat(pattern = "yyyy-MM-dd")
  Date startDate;
  @JsonFormat(pattern = "yyyy-MM-dd")
  Date effectiveDate;
  @JsonFormat(pattern = "yyyy-MM-dd")
  Date renewalDate;
  String status;
  BigDecimal premium;
  boolean renewed;
}
