package ke.co.apollo.health.mapping;

import java.util.Date;
import ke.co.apollo.health.common.domain.model.Policy;
import ke.co.apollo.health.common.domain.model.Premium;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.domain.request.QuoteBenefitUpdateRequest;
import ke.co.apollo.health.domain.response.CustomerQuoteResponse;
import ke.co.apollo.health.domain.response.QuoteListResponse;

public class QuoteMapping {

  private QuoteMapping() {

  }

  public static Quote quoteUpdateRequest2Entity(QuoteBenefitUpdateRequest dto) {
    return Quote.builder()
        .agentId(dto.getAgentId())
        .customerId(dto.getCustomerId())
        .benefit(dto.getBenefit())
        .updateTime(new Date())
        .build();
  }

  public static QuoteListResponse quote2QuoteListResponse(Quote quote) {
    return QuoteListResponse.builder()
        .agentId(quote.getAgentId())
        .quoteId(quote.getId())
        .quoteNumber(quote.getCode())
        .productId(String.valueOf(quote.getProductId()))
        .benefit(quote.getBenefit())
        .startDate(quote.getStartDate())
        .status(quote.getStatus())
        .premium(quote.getPremium())
        .balance(quote.getBalance())
        .onlyChild(quote.isChildrenOnly())
        .quoteStatus(quote.getQuoteStatus())
        .build();
  }

  public static CustomerQuoteResponse quote2CustomerQuoteResponse(Quote quote) {
    return CustomerQuoteResponse.builder()
        .quoteId(quote.getId())
        .quoteNumber(quote.getExtPolicyNumber())
        .productId(String.valueOf(quote.getProductId()))
        .startDate(quote.getStartDate())
        .premium(quote.getPremium())
        .onlyChild(quote.isChildrenOnly())
        .build();
  }

  public static Quote policy2Quote(Policy policy) {
    return Quote.builder()
        .entityId(policy.getPolicyHolderEntityId())
        .productId(policy.getProductId())
        .startDate(policy.getPolicyStartDate())
        .effectiveDate(policy.getPolicyEffectiveDate())
        .renewalDate(policy.getPolicyRenewalDate())
        .paymentStyle(policy.getPaymentStyle())
        .extPolicyId(policy.getPolicyId())
        .extPolicyNumber(policy.getPolicyNumber())
        .status(policy.getPolicyStatus())
        .premium(Premium.builder().totalPremium(policy.getPolicyAmount()).build())
        .build();
  }

  public static QuoteListResponse policy2QuoteListResponse(Policy policy) {
    return QuoteListResponse.builder()
        .quoteId(String.valueOf(policy.getPolicyId()))
        .productId(String.valueOf(policy.getProductId()))
        .startDate(policy.getPolicyStartDate())
        .status(policy.getPolicyStatus())
        .premium(Premium.builder().totalPremium(policy.getPolicyAmount()).build())
        .build();
  }

  public static CustomerQuoteResponse policy2CustomerQuoteResponse(Policy policy) {
    return CustomerQuoteResponse.builder()
        .quoteId(String.valueOf(policy.getPolicyId()))
        .productId(String.valueOf(policy.getProductId()))
        .startDate(policy.getPolicyStartDate())
        .quoteNumber(policy.getPolicyNumber())
        .premium(Premium.builder().totalPremium(policy.getPolicyAmount()).build())
        .build();
  }

}
