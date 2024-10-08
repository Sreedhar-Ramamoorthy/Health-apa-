package ke.co.apollo.health.policy.service.impl;

import ke.co.apollo.health.common.domain.model.BenefitBreakDownCategory;
import ke.co.apollo.health.common.domain.model.request.GetBenefitBreakDownRequest;
import ke.co.apollo.health.common.domain.model.response.BenefitBeanResponse;
import ke.co.apollo.health.common.domain.model.response.BenefitBeanResponse.BenefitBean;
import ke.co.apollo.health.common.domain.model.response.BenefitBeanResponse.BenefitBean.BenefitLimit;
import ke.co.apollo.health.common.domain.model.response.GetBenefitBreakDownResponse;
import ke.co.apollo.health.common.domain.model.response.GetBenefitBreakDownResponse.BenefitItems;
import ke.co.apollo.health.common.enums.BenefitEnum;
import ke.co.apollo.health.policy.remote.ClaimsRemote;
import ke.co.apollo.health.policy.service.ClaimsService;
import ke.co.apollo.health.policy.service.QuoteService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClaimsServiceImpl implements ClaimsService {

  @Autowired
  private ClaimsRemote claimsRemote;

  @Autowired
  private QuoteService quoteService;

  @Autowired
  private BenefitBreakDownCategory benefitCategory;

  @Override
  public GetBenefitBreakDownResponse getBenefit(GetBenefitBreakDownRequest request) {

    GetBenefitBreakDownResponse response = claimsRemote.getBenefit(request);
    if (response != null) {
      int exists = quoteService.getTravelBenefitLimit(request.getPolicyId(), null);
      if (exists == 1) {
        response.getItems().add(BenefitItems.builder().description("TravelBenefit")
            .benefitLimit(1).build());
      }
    }

    return response;
  }

  @Override
  public BenefitBeanResponse getBenefitBreakDown(GetBenefitBreakDownRequest request) {
    GetBenefitBreakDownResponse response = this.getBenefit(request);
    BenefitBeanResponse result = null;
    if (response != null) {
      result = BenefitBeanResponse.builder().entityId(String.valueOf(request.getBeneficiaryId()))
          .benefit(fulfillBenefitBean(response)).build();
    }
    return result;
  }


  private BenefitBean fulfillBenefitBean(GetBenefitBreakDownResponse benefitBreakDownResponse) {
    BenefitBean benefitBean = new BenefitBean();
    if (benefitBreakDownResponse.isSuccess() && CollectionUtils
        .isNotEmpty(benefitBreakDownResponse.getItems())) {
      for (BenefitItems item : benefitBreakDownResponse.getItems()) {
        BenefitLimit benefitLimit = BenefitLimit.builder().limit(item.getBenefitLimit())
            .used(item.getBenefitLimit() - item.getBenefitRemaining()).build();
        switch (BenefitEnum
            .getByValue(benefitCategory.getCategories().get(item.getDescription()))) {
          case DENTAL:
            benefitBean.setDentalLimit(benefitLimit);
            break;
          case OPTICAL:
            benefitBean.setOpticalLimit(benefitLimit);
            break;
          case MATERNITY:
            benefitBean.setMaternityLimit(benefitLimit);
            break;
          case INPATIENT:
            benefitBean.setInpatientLimit(benefitLimit);
            break;
          case OUTPATIENT:
            benefitBean.setOutpatientLimit(benefitLimit);
            break;
          case TRAVEL:
            benefitBean.setTravelInsurance(benefitLimit);
            break;
          default:
            break;
        }
      }
    }
    return benefitBean;
  }

}
