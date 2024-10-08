package ke.co.apollo.health.policy.service.impl;

import java.util.Date;
import java.util.Optional;
import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.policy.mapper.health.HealthMapper;
import ke.co.apollo.health.policy.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuoteServiceImpl implements QuoteService {

  @Autowired
  HealthMapper healthMapper;

  @Override
  public int getTravelBenefitLimit(Integer policyId, Date effectiveDate) {
    Quote quote = healthMapper.getQuoteByPolicyId(policyId, effectiveDate);
    return Optional.ofNullable(quote).map(Quote::getBenefit)
        .map(Benefit::getTravelInsurance).orElse(0);
  }
}
