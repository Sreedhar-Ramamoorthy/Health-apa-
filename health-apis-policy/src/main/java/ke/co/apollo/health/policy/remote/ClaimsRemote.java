package ke.co.apollo.health.policy.remote;

import ke.co.apollo.health.common.domain.model.request.GetBenefitBreakDownRequest;
import ke.co.apollo.health.common.domain.model.response.GetBenefitBreakDownResponse;

public interface ClaimsRemote {

  GetBenefitBreakDownResponse getBenefit(GetBenefitBreakDownRequest request);

}
