package ke.co.apollo.health.service;

import java.math.BigDecimal;

import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.PolicyDetail;
import ke.co.apollo.health.common.domain.model.Premium;
import ke.co.apollo.health.common.domain.model.ProductPremium;
import ke.co.apollo.health.common.domain.model.RenewalPremium;
import ke.co.apollo.health.common.enums.ProductEnum;
import ke.co.apollo.health.domain.PolicyBeneficiary;
import ke.co.apollo.health.domain.PolicyClaim;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;

public interface ProductService {

  ProductPremium getProductPremium();

  BigDecimal calculatePremium(PolicyBeneficiary policyBeneficiary, Integer productId,
      Benefit benefit, boolean renewal);

  Premium calculateTotalPremium(BigDecimal premium, boolean isNewPolicy);

  Benefit createDefaultBenefit(CustomerDetailResponse customerDetailResponse,
      ProductEnum productEnum);

  RenewalPremium calculateRenewalPremium(PolicyBeneficiary policyBeneficiary,
      Integer productId, Benefit benefit, PolicyClaim policyClaim, BigDecimal policyAdjustment);

  RenewalPremium calcRenewPremiumByTotalPremium(PolicyBeneficiary policyBeneficiary,
                                                PolicyDetail policyDetail, Benefit benefit, PolicyClaim policyClaim, BigDecimal adjustment);

  RenewalPremium calcRenewPremiumByTotalPremiumForComingWorker(PolicyBeneficiary policyBeneficiary,
                                                PolicyDetail policyDetail, Benefit benefit, PolicyClaim policyClaim, BigDecimal adjustment);

  BigDecimal getTravelInsurancePremium(PolicyBeneficiary policyBeneficiary,
                            Benefit benefit);
}
