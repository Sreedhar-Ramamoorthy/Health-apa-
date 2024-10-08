package ke.co.apollo.health.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ke.co.apollo.health.common.domain.model.HealthPolicy;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;
import ke.co.apollo.health.domain.BenefitPremium;
import ke.co.apollo.health.domain.PolicyBeneficiary;
import ke.co.apollo.health.domain.PolicyBeneficiary.Beneficiary;
import ke.co.apollo.health.domain.PolicyBenefitPremium;
import ke.co.apollo.health.domain.PolicyBenefitPremium.MemberPremium;
import ke.co.apollo.health.domain.entity.PolicyPremiumEntity;
import ke.co.apollo.health.domain.request.QuoteBaseRequest;
import ke.co.apollo.health.repository.PolicyPremiumRepository;
import ke.co.apollo.health.service.PremiumService;
import ke.co.apollo.health.service.QuoteService;

@Service
@Slf4j
public class PremiumServiceImpl implements PremiumService {

  @Autowired
  QuoteService quoteService;

  @Autowired
  PolicyPremiumRepository policyPremiumRepository;

  @Override
  public boolean recordQuoteBeneficiaryPremium(Quote quote, PolicyBeneficiary policyBeneficiary) {

    Optional<List<PolicyPremiumEntity>> policyPremiumEntityList = policyPremiumRepository.findAllByQuoteId(quote.getId());
    log.debug("Find PolicyPremiumEntity by quote id: {}", quote.getId());

    policyPremiumEntityList.ifPresent(policyPremiumEntities -> log.debug("Find PolicyPremiumEntity size: {}, will del them all",
                                                                         policyPremiumEntities.size()));

    policyPremiumEntityList.ifPresent(policyPremiumEntities -> policyPremiumRepository.deleteAll(policyPremiumEntities));

    log.debug("Still have PolicyPremiumEntity : {}", policyPremiumRepository.findAllByQuoteId(quote.getId()).isPresent());

    Beneficiary principal = policyBeneficiary.getPrincipal();
    if (principal != null && CollectionUtils.isNotEmpty(principal.getBenefitPremiums())) {
      this.saveBeneficiaryPremium(quote, principal);
    }

    Beneficiary spouse = policyBeneficiary.getSpouse();
    if (spouse != null && CollectionUtils.isNotEmpty(spouse.getBenefitPremiums())) {
      this.saveBeneficiaryPremium(quote, spouse);
    }

    for (Beneficiary beneficiary : policyBeneficiary.getChildren()) {
      if (beneficiary != null && CollectionUtils.isNotEmpty(beneficiary.getBenefitPremiums())) {
        this.saveBeneficiaryPremium(quote, beneficiary);
      }
    }
    return true;
  }

  private void saveBeneficiaryPremium(Quote quote, Beneficiary beneficiary) {
    for (BenefitPremium benefitPremium : beneficiary.getBenefitPremiums()) {
      PolicyPremiumEntity entity = PolicyPremiumEntity.builder()
          .customerId(beneficiary.getCustomerId()).entityId(beneficiary.getEntityId())
          .name(beneficiary.getName()).quoteId(quote.getId())
          .policyId(quote.getExtPolicyId()).policyNumber(quote.getExtPolicyNumber())
          .effectiveDate(quote.getEffectiveDate()).age(beneficiary.getAge())
          .relationship(beneficiary.getRelationship())
          .createTime(new Date())
          .build();
      entity.setBenefitLimit(BigDecimal.valueOf(benefitPremium.getBenefitLimit()));
      entity.setBenefitType(benefitPremium.getBenefitType().getValue());
      entity.setPremium(benefitPremium.getPremium());
      policyPremiumRepository.save(entity);
    }
  }

  @Override
  public boolean recordPolicyBeneficiaryPremium(HealthPolicy policy,
      PolicyBeneficiary policyBeneficiary) {
    policyPremiumRepository.deleteByPolicyIdAndEffectiveDate(policy.getPolicyId(), policy.getEffectiveDate());
    Beneficiary principal = policyBeneficiary.getPrincipal();
    if (principal != null) {
      this.saveBeneficiaryPremium(policy, principal);
    }
    Beneficiary spouse = policyBeneficiary.getSpouse();
    if (spouse != null) {
      this.saveBeneficiaryPremium(policy, spouse);
    }

    for (Beneficiary beneficiary : policyBeneficiary.getChildren()) {
      if (beneficiary != null) {
        this.saveBeneficiaryPremium(policy, beneficiary);
      }
    }
    return true;
  }

  private void saveBeneficiaryPremium(HealthPolicy policy, Beneficiary beneficiary) {
    for (BenefitPremium benefitPremium : beneficiary.getBenefitPremiums()) {
      PolicyPremiumEntity entity = PolicyPremiumEntity.builder()
                                                      .customerId(beneficiary.getCustomerId())
                                                      .entityId(beneficiary.getEntityId())
                                                      .name(beneficiary.getName())
                                                      .quoteId(policy.getQuoteId())
                                                      .policyId(policy.getPolicyId())
                                                      .policyNumber(policy.getPolicyNumber())
                                                      .effectiveDate(policy.getEffectiveDate())
                                                      .age(beneficiary.getAge())
                                                      .relationship(beneficiary.getRelationship())
                                                      .createTime(new Date())
                                                      .build();
      entity.setBenefitLimit(BigDecimal.valueOf(benefitPremium.getBenefitLimit()));
      entity.setBenefitType(benefitPremium.getBenefitType().getValue());
      entity.setPremium(benefitPremium.getPremium());
      policyPremiumRepository.save(entity);
    }
  }

  private Optional<List<PolicyPremiumEntity>> getQuotePremiumDetail(QuoteBaseRequest request) {
    quoteService.getQuote(request.getQuoteId(), request.getCustomerId(), request.getAgentId());
    return policyPremiumRepository.findAllByQuoteId(request.getQuoteId());
  }

  @Override
  public List<PolicyPremiumEntity> getQuotePremium(QuoteBaseRequest request) {
    List<PolicyPremiumEntity> list = new ArrayList<>();
    Optional<List<PolicyPremiumEntity>> result = this.getQuotePremiumDetail(request);
    if (result.isPresent()) {
      list = result.get();
    }
    return list;
  }

  @Override
  public List<PolicyBenefitPremium> getQuoteBenefitPremium(QuoteBaseRequest request) {
    Optional<List<PolicyPremiumEntity>> result = this.getQuotePremiumDetail(request);
    return this.convertPolicyBenefitPremium(result);
  }

  private List<PolicyBenefitPremium> convertPolicyBenefitPremium(
      Optional<List<PolicyPremiumEntity>> premiumEntities) {
    List<PolicyBenefitPremium> premiums = new ArrayList<>();
    if (premiumEntities.isPresent()) {
      Map<String, List<PolicyPremiumEntity>> map = premiumEntities.get().stream()
          .collect(Collectors.groupingBy(PolicyPremiumEntity::getBenefitType));
      map.forEach((key, value) -> {
        PolicyBenefitPremium premium = PolicyBenefitPremium.builder().benefitType(key)
            .benefitLimit(value.stream().findFirst().get().getBenefitLimit()).build();
        List<MemberPremium> memberPremiums = value.stream()
            .map(v -> MemberPremium.builder().age(v.getAge()).customerId(v.getCustomerId())
                .entityId(v.getEntityId()).name(v.getName()).relationship(v.getRelationship())
                .premium(v.getPremium()).build()).collect(Collectors.toList());
        premium.setList(memberPremiums);
        premiums.add(premium);
      });
    }
    return premiums;
  }

  @Override
  public List<PolicyPremiumEntity> getPolicyPremium(PolicyIdRequest request) {
    List<PolicyPremiumEntity> list = new ArrayList<>();
    Optional<List<PolicyPremiumEntity>> result = policyPremiumRepository
        .findAllByPolicyIdAndEffectiveDate(request.getPolicyId(), request.getEffectiveDate());
    if (result.isPresent()) {
      list = result.get();
    }
    return list;
  }

  @Override
  public List<PolicyBenefitPremium> getPolicyBenefitPremium(PolicyIdRequest request) {
    Optional<List<PolicyPremiumEntity>> result = policyPremiumRepository
        .findAllByPolicyIdAndEffectiveDate(request.getPolicyId(), request.getEffectiveDate());
    return this.convertPolicyBenefitPremium(result);
  }
}
