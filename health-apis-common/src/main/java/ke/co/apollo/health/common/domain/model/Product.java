package ke.co.apollo.health.common.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {

  String productName;
  String productCode;
  BigDecimal premiumMonthly;
  BigDecimal premiumAnnually;
  BigDecimal sumAssured;
  Integer maximumBeneficiary;

}
