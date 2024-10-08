package ke.co.apollo.health.policy.service;

import java.util.Date;

public interface QuoteService {

  int getTravelBenefitLimit(Integer policyId, Date effectiveDate);

}
