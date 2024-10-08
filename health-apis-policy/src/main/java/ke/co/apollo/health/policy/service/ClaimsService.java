package ke.co.apollo.health.policy.service;


import ke.co.apollo.health.common.domain.model.request.GetBenefitBreakDownRequest;
import ke.co.apollo.health.common.domain.model.response.BenefitBeanResponse;
import ke.co.apollo.health.common.domain.model.response.GetBenefitBreakDownResponse;

public interface ClaimsService {

  GetBenefitBreakDownResponse getBenefit(GetBenefitBreakDownRequest request);

  BenefitBeanResponse getBenefitBreakDown(GetBenefitBreakDownRequest request);

}
