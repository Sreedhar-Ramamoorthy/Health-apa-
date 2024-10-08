package ke.co.apollo.health.domain.request;

import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.domain.PolicyClaim;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPolicyRenewalRequest {

  private PolicyClaim policyPremium;

  private CustomerDetailResponse customerDetail;

  private Benefit benefit;

}
