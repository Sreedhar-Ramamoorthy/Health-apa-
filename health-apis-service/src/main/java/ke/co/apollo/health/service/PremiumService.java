package ke.co.apollo.health.service;

import java.util.List;
import ke.co.apollo.health.common.domain.model.HealthPolicy;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;
import ke.co.apollo.health.domain.PolicyBeneficiary;
import ke.co.apollo.health.domain.PolicyBenefitPremium;
import ke.co.apollo.health.domain.entity.PolicyPremiumEntity;
import ke.co.apollo.health.domain.request.QuoteBaseRequest;


public interface PremiumService {

  boolean recordQuoteBeneficiaryPremium(Quote quote, PolicyBeneficiary policyBeneficiary);

  boolean recordPolicyBeneficiaryPremium(HealthPolicy policy, PolicyBeneficiary policyBeneficiary);

  List<PolicyPremiumEntity> getQuotePremium(QuoteBaseRequest request);

  List<PolicyPremiumEntity> getPolicyPremium(PolicyIdRequest request);

  List<PolicyBenefitPremium> getQuoteBenefitPremium(QuoteBaseRequest request);

  List<PolicyBenefitPremium> getPolicyBenefitPremium(PolicyIdRequest request);
}

