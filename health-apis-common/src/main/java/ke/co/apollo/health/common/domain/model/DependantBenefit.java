package ke.co.apollo.health.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependantBenefit {

  boolean outpatient;
  boolean maternity;
  boolean dental;
  boolean optical;
  boolean travelInsurance;

}
