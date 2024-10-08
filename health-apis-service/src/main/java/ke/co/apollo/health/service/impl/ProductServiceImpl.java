package ke.co.apollo.health.service.impl;

import static java.lang.Double.MAX_VALUE;
import static ke.co.apollo.health.common.constants.GlobalConstant.STAMP_DUTY;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import ke.co.apollo.health.domain.request.JamiiPlusSetupRequest;
import ke.co.apollo.health.domain.response.JamiiPlusSetupResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.DefaultBenefit;
import ke.co.apollo.health.common.domain.model.DefaultBenefit.AfyaNafuuBenefit;
import ke.co.apollo.health.common.domain.model.DefaultBenefit.*;
import ke.co.apollo.health.common.domain.model.DependantBenefit;
import ke.co.apollo.health.common.domain.model.PolicyDetail;
import ke.co.apollo.health.common.domain.model.Premium;
import ke.co.apollo.health.common.domain.model.ProductPremium;
import ke.co.apollo.health.common.domain.model.ProductPremium.Health;
import ke.co.apollo.health.common.domain.model.ProductPremium.Health.AfyaNafuu;
import ke.co.apollo.health.common.domain.model.ProductPremium.Health.Femina;
import ke.co.apollo.health.common.domain.model.ProductPremium.Health.JamiiPlus;
import ke.co.apollo.health.common.domain.model.ProductPremium.Health.JamiiPlus.Permium;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap.JamiiPlusSharedMap;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap.AfyaNafuuMap;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap.FeminaMap;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap.InpatientMap;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap.JamiiPlusMap;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap.JamiiPlusChildOnlyCoverMap;

import ke.co.apollo.health.common.domain.model.RenewalPremium;
import ke.co.apollo.health.common.enums.BenefitEnum;
import ke.co.apollo.health.common.enums.GenderEnum;
import ke.co.apollo.health.common.enums.ProductEnum;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.common.utils.FormatUtils;
import ke.co.apollo.health.common.utils.ProductUtils;
import ke.co.apollo.health.domain.BenefitPremium;
import ke.co.apollo.health.domain.PolicyBeneficiary;
import ke.co.apollo.health.domain.PolicyBeneficiary.Beneficiary;
import ke.co.apollo.health.domain.PolicyClaim;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.service.CustomerService;
import ke.co.apollo.health.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

  public static final String LOADING_PERCENT = "loadingPercent: {}";
  private Logger logger = LoggerFactory.getLogger(getClass());

  Gson g = new Gson();

  @Autowired
  ProductPremium productPremium;

  @Autowired
  ProductPremiumMap productPremiumMap;

  @Autowired
  CustomerService customerService;

  @Override
  public ProductPremium getProductPremium() {
    return productPremium;
  }

  @Override
  public Premium calculateTotalPremium(BigDecimal premium, boolean isNewPolicy) {
    BigDecimal itl = ProductUtils.calculateITL(premium);
    logger.info("itl : {}", itl);
    BigDecimal phcf = ProductUtils.calculatePHCF(premium);
    logger.info("phcf : {}", phcf);
    BigDecimal stampDuty = BigDecimal.ZERO;
    if (isNewPolicy) {
      stampDuty = STAMP_DUTY;
    }
    BigDecimal totalPremium = FormatUtils.scaleValue(premium.add(itl).add(phcf).add(stampDuty));
    logger.info("totalPremium without travel: {}", totalPremium);
    return Premium.builder().premium(premium).itl(itl).phcf(phcf).stampDuty(stampDuty)
        .totalPremium(totalPremium)
        .build();
  }

  @Override
  public BigDecimal calculatePremium(PolicyBeneficiary policyBeneficiary, Integer productId,
      Benefit benefit, boolean renewal ) {
    BigDecimal totalPremium = BigDecimal.ZERO;

    if (this.validateBenefit(productPremium.getHealth(), productId, benefit)) {
      throw new BusinessException("invalid benefit");
    }

    if (ProductEnum.JAMIIPLUS.getId() == productId) {
      totalPremium = this.calculateJamiiPlusPremium(policyBeneficiary, benefit, renewal, ProductEnum.JAMIIPLUS.getValue());
    } else if (ProductEnum.AFYANAFUU.getId() == productId) {
      totalPremium = this.calculateAfyaNafuuPremium(policyBeneficiary, benefit);
    } else if (ProductEnum.FEMINA.getId() == productId) {
      totalPremium = this.calculateFeminaPremium(policyBeneficiary, benefit);
    } else if (ProductEnum.JAMIIPLUS_SHARED.getId() == productId) {
      totalPremium = this.calculateJamiiPlusSharedPremium(policyBeneficiary, benefit, renewal, ProductEnum.JAMIIPLUS_SHARED.getValue());
    }

    return totalPremium;
  }

  private boolean validateBenefit(Health health, Integer productId, Benefit benefit) {
    if (ProductEnum.JAMIIPLUS.getId() == productId) {
      JamiiPlus jamiiPlus = health.getJamiiPlus();
      return (jamiiPlus.getInpatient().stream()
          .noneMatch(t -> t.getBenefit() == benefit.getInpatientLimit()))
          || (benefit.getOutpatientLimit() > 0 && jamiiPlus.getOutpatient().stream()
          .noneMatch(t -> t.getBenefit() == benefit.getOutpatientLimit()))
          || this.checkBenefit(benefit.getDentalLimit(), jamiiPlus.getDental())
          || this.checkBenefit(benefit.getMaternityLimit(), jamiiPlus.getMaternity())
          || this.checkBenefit(benefit.getOpticalLimit(), jamiiPlus.getOptical())
          || (benefit.getTravelInsurance() > 0
          && jamiiPlus.getTravel().getBenefit() != benefit
          .getTravelInsurance());
    } else if (ProductEnum.AFYANAFUU.getId() == productId) {
      AfyaNafuu afyaNafuu = health.getAfyaNafuu();
      return (afyaNafuu.getInpatient().stream()
          .noneMatch(t -> t.getBenefit() == benefit.getInpatientLimit()
          )) || (benefit.getOutpatientLimit() > 0 && afyaNafuu.getOutpatient().stream()
          .noneMatch(t -> t.getBenefit() == benefit.getOutpatientLimit()
          )) || (benefit.getMaternityLimit() > 0 && afyaNafuu.getMaternity().stream()
          .noneMatch(t -> t.getBenefit() == benefit.getMaternityLimit()
          ));
    } else if (ProductEnum.FEMINA.getId() == productId) {
      List<Femina> feminas = health.getFemina();
      List<Integer> benefitList = feminas.stream().map(Femina::getBenefit)
          .collect(Collectors.toList());
      return this.checkPSBenefit(benefit.getPrincipal(), benefitList)
          || this.checkPSBenefit(benefit.getSpouse(), benefitList)
          || (CollectionUtils.size(benefit.getChildren()) > 0 && benefit.getChildren().stream()
          .noneMatch(p -> checkBenefitList(benefitList, p)));
    }
    return false;
  }

  private boolean checkBenefit(int benefit, List<Permium> permiumList) {
    return benefit > 0 && permiumList.stream().noneMatch(t -> t.getBenefit() == benefit);
  }

  private boolean checkPSBenefit(int benefit, List<Integer> benefitList) {
    return benefit > 0 && !benefitList.contains(benefit);
  }

  private boolean checkBenefitList(List<Integer> benefitList, int p) {
    return benefitList.contains(p);
  }

  private int getTotalBeneficiary(PolicyBeneficiary policyBeneficiary) {
    int total = 0;
    if (policyBeneficiary.getPrincipal() != null) {
      total++;
    }
    if (policyBeneficiary.getSpouse() != null) {
      total++;
    }
    total += CollectionUtils.size(policyBeneficiary.getChildren());
    if (total == 0) {
      throw new BusinessException("no beneficiary");
    }
    return total;
  }

  private void validateBeneficiary(PolicyBeneficiary policyBeneficiary) {
    Beneficiary principalBeneficiary = policyBeneficiary.getPrincipal();
    Beneficiary spouseBeneficiary = policyBeneficiary.getSpouse();
    if (principalBeneficiary != null && (principalBeneficiary.getAge() < 21
        || principalBeneficiary.getAge() > 80)) {
      throw new BusinessException("principal must be between 21 and 80 years old");
    }
    if (spouseBeneficiary != null && (spouseBeneficiary.getAge() < 21
        || spouseBeneficiary.getAge() > 80)) {
      throw new BusinessException("spouse must be between 21 and 80 years old");
    }
    List<Beneficiary> childrenBeneficiary = policyBeneficiary.getChildren();
    if (CollectionUtils.isNotEmpty(childrenBeneficiary)) {
      for (Beneficiary child : childrenBeneficiary) {
        if (child.getAge() < 0 || child.getAge() > 80) {
          throw new BusinessException("child must be between 1 and 80 years old");
        }
      }
    }
  }

  private JamiiPlusSetupResponse setUpJamiiPlus(JamiiPlusSetupRequest jamiiPlusSetupRequest, boolean renewal) {
    PolicyBeneficiary policyBeneficiary = jamiiPlusSetupRequest.getPolicyBeneficiary();
    String product = jamiiPlusSetupRequest.getProduct();
    Benefit benefit = jamiiPlusSetupRequest.getBenefit();

    String track = "renewal-" + renewal;
    logger.info("{} ***Product: {}***, policyBeneficiary: {}, benefit: {}, renewal: {}", product,
            track,                    policyBeneficiary,    benefit,     renewal);
    String benefitJson =  g.toJson(benefit);
    String policyBeneficiaryJson =  g.toJson(policyBeneficiary);

    logger.info("{}, benefitJson: \t{}", track, benefitJson);
    logger.info("{}, policyBeneficiaryJson: \t{}", track, policyBeneficiaryJson);

    // Inpatient
    this.validateBeneficiary(policyBeneficiary);
    int totalBeneficiary = this.getTotalBeneficiary(policyBeneficiary);
    logger.info("{}, JP totalBeneficiary: {}", track, totalBeneficiary);
    Beneficiary principalBeneficiary = policyBeneficiary.getPrincipal();
    Beneficiary spouseBeneficiary = policyBeneficiary.getSpouse();
    List<Beneficiary> childrenBeneficiary = policyBeneficiary.getChildren();
    boolean hasSpouse = spouseBeneficiary != null;
    boolean hasPrincipal = principalBeneficiary != null;


    return JamiiPlusSetupResponse.builder()
            .hasPrincipal(hasPrincipal)
            .hasSpouse(hasSpouse)
            .principalBeneficiary(principalBeneficiary)
            .totalBeneficiary(totalBeneficiary)
            .spouseBeneficiary(spouseBeneficiary)
            .childrenBeneficiary(childrenBeneficiary)
            .track(track)
            .build();
  }

  private void logMessage(String track, String nameOfObject, Object object) {
      logger.info("{}, {}, {}", track, nameOfObject, object);
  }

  private BigDecimal calculateJamiiPlusSharedPremium(PolicyBeneficiary policyBeneficiary,
                                                     Benefit benefit, boolean renewal, String product) {

    int totalPremium = 0;
    int inpatientPremium = 0;

    int inpatientPrincipalPremium = 0;
    int inpatientSpousePremium = 0;
    int inpatientChildrenPremium = 0;

    JamiiPlusSetupRequest jamiiPlusSetupRequest = JamiiPlusSetupRequest.builder()
           .product(product)
           .policyBeneficiary(policyBeneficiary)
           .benefit(benefit)
           .build();

    JamiiPlusSetupResponse jamiiPlusSetupResponse = setUpJamiiPlus(jamiiPlusSetupRequest, renewal);
    String track = jamiiPlusSetupResponse.getTrack();
    boolean hasPrincipal = jamiiPlusSetupResponse.getHasPrincipal();
    boolean hasSpouse = jamiiPlusSetupResponse.getHasSpouse();
    Beneficiary principalBeneficiary = jamiiPlusSetupResponse.getPrincipalBeneficiary();
    Beneficiary spouseBeneficiary = jamiiPlusSetupResponse.getSpouseBeneficiary();
    List<Beneficiary> childrenBeneficiary = jamiiPlusSetupResponse.getChildrenBeneficiary();
    int totalBeneficiary = jamiiPlusSetupResponse.getTotalBeneficiary();

      // Use Jamii Plus Map to calculate OP, Dental, Optical, Maternity and travel
      // because they have the same price.
    JamiiPlusMap jamiiPlusMapForOptionalRiders = productPremiumMap.getJamiiPlusMap();
    JamiiPlusSharedMap jamiiPlusSharedMap = productPremiumMap.getJamiiPlusSharedMap();


    logger.debug("{}  {}Map {}", track, product, jamiiPlusSharedMap);
    InpatientMap inpatientMap = jamiiPlusSharedMap.getInpatient();
    logMessage(track, "inpatientMap", inpatientMap);
    List<Integer> inpatientAgeList = jamiiPlusSharedMap.getInpatientAge();
    logMessage(track, "inpatientAgeList", inpatientAgeList);
    Integer inpatientLimit = benefit.getInpatientLimit();
    logMessage(track, "hasPrincipal", hasPrincipal);
    logMessage(track, "hasSpouse", hasSpouse);
    logMessage(track, "Children", policyBeneficiary.getChildren());

    List<Integer> inpatientAgeIndexList = this.getInpatientAgeIndexList(policyBeneficiary, inpatientAgeList, track);
    String inpatientAgeIndexListJson  = g.toJson(inpatientAgeIndexList);
    logger.info("{}  inpatientAgeIndexList json {}", track, inpatientAgeIndexListJson);
        // Inpatient Principal and Spouse
    int principalAgeIndex = inpatientAgeIndexList.get(0);

    if (hasSpouse) {
        logger.debug("{}, {} cover principal and spouse", track, product);
        // calculate inpatient premium
        inpatientPrincipalPremium = inpatientMap.getPrincipal().get(inpatientLimit, principalAgeIndex);
        logMessage(track, "inpatientLimit", inpatientLimit);
        logMessage(track, "principalAgeIndex", principalAgeIndex);
        logMessage(track, "inpatientMap", inpatientMap);
        logMessage(track, "inpatientPrincipalPremium", inpatientPrincipalPremium);

        int spouseAgeIndex = inpatientAgeIndexList.get(1);
        inpatientSpousePremium = inpatientMap.getSpouse().get(inpatientLimit, spouseAgeIndex);

        // record premium
        BenefitPremium principalInpatientPremium = BenefitPremium.builder()
                .benefitType(BenefitEnum.INPATIENT)
                .benefitLimit(inpatientLimit)
                .premium(BigDecimal.valueOf(inpatientPrincipalPremium))
                .build();
        logger.info("==== principle beneficiary {}", principalBeneficiary);
        logger.info("==== benefit premiums {}", principalBeneficiary.getBenefitPremiums());

        addPrincipalInpatientPremium(inpatientSpousePremium, track, principalBeneficiary, spouseBeneficiary, inpatientLimit, principalInpatientPremium);
      }
    else {
        // Inpatient Principal
        logger.info("{}, JP not cover spouse", track);
        // calculate inpatient premium
        inpatientPrincipalPremium = inpatientMap.getPrincipal().get(inpatientLimit, principalAgeIndex);
        logger.info("{} inpatientPrincipalPremium: {}", track, inpatientPrincipalPremium);
        // record premium
        BenefitPremium principalInpatientPremium = BenefitPremium.builder()
                .benefitType(BenefitEnum.INPATIENT)
                .benefitLimit(inpatientLimit)
                .premium(BigDecimal.valueOf(inpatientPrincipalPremium))
                .build();
        logger.info("{} principalInpatientPremium: {}", track, principalInpatientPremium);

        principalBeneficiary.getBenefitPremiums().add(principalInpatientPremium);

        logger.info("{} principalBeneficiary: {}", track, principalBeneficiary);
      }


    inpatientChildrenPremium = calcInpatientChildren(childrenBeneficiary,
            track,
            inpatientMap,
            inpatientLimit,
            inpatientAgeList,
            inpatientChildrenPremium);
    // total inpatient premium
    inpatientPremium += inpatientPrincipalPremium + inpatientSpousePremium + inpatientChildrenPremium;

    logger.info("{}, #inpatientLimit: {}", track, inpatientLimit);
    logger.info("{}, *inpatientPrincipalPremium: {}, *inpatientSpousePremium: {} ,*inpatientChildrenPremium: {}",
            track, inpatientPrincipalPremium, inpatientSpousePremium, inpatientChildrenPremium);

    logger.info("{}, *inpatientPremium: {}", track, inpatientPremium);
    logger.info("{}, *checkPremium hasPrincipal: {}, inpatientPrincipalPremium: {}", track, hasPrincipal, inpatientPrincipalPremium);
    logger.info("{}, *checkPremium hasSpouse: {},inpatientSpousePremium: {}", track, hasSpouse,inpatientSpousePremium);

    this.checkPremium(inpatientPrincipalPremium, inpatientSpousePremium, hasPrincipal, hasSpouse, renewal);

    // Optional

    int optionalPremium = this.calculateOptionalPremium(benefit, jamiiPlusMapForOptionalRiders, totalBeneficiary, track, policyBeneficiary);
    logger.info("{}, *optionalPremium: {}", track, optionalPremium);

    //Maternity
    int maternityPremium = this.calculateMaternityPremium(benefit, jamiiPlusMapForOptionalRiders, policyBeneficiary, track);
    logger.info("{}, *maternityPremium: {}", track, maternityPremium);
    // Travel Insurance
    logger.info("{}, *benefit.getTravelInsurance() > 0: {}", track, benefit.getTravelInsurance() > 0);


    if (benefit.getTravelInsurance() > 0) {
      int premium = jamiiPlusSharedMap.getTravel().get(benefit.getTravelInsurance());
      this.recordBenefitPremium(BenefitEnum.TRAVEL, benefit.getTravelInsurance(), premium, policyBeneficiary);
    }

    totalPremium = inpatientPremium + optionalPremium + maternityPremium ;

    logger.info("{} *inpatientPremium: {}, *optionalPremium: {}, *maternityPremium: {}",
            track, inpatientPremium, optionalPremium, maternityPremium );

    logger.info("{} ***JamiiPlus end***, *totalPremium: {}", track, totalPremium);

    return BigDecimal.valueOf(totalPremium);
  }

    private void addPrincipalInpatientPremium(int inpatientSpousePremium, String track, Beneficiary principalBeneficiary, Beneficiary spouseBeneficiary, Integer inpatientLimit, BenefitPremium principalInpatientPremium) {
        principalBeneficiary.getBenefitPremiums().add(principalInpatientPremium);
        logger.info("{}  principalBeneficiary {}", track, principalBeneficiary);

        BenefitPremium spouseInpatientPremium = BenefitPremium.builder()
                .benefitType(BenefitEnum.INPATIENT)
                .benefitLimit(inpatientLimit)
                .premium(BigDecimal.valueOf(inpatientSpousePremium))
                .build();

        spouseBeneficiary.getBenefitPremiums().add(spouseInpatientPremium);
        logger.info("{}  spouseBeneficiary {}", track, spouseBeneficiary);
    }

    private BigDecimal calculateJamiiPlusPremium(PolicyBeneficiary policyBeneficiary,
                                               Benefit benefit, boolean renewal, String product) {
      int totalPremium = 0;
      int inpatientPremium = 0;

      int inpatientPrincipalPremium = 0;
      int inpatientSpousePremium = 0;
      int inpatientChildrenPremium = 0;

      JamiiPlusSetupRequest jamiiPlusSetupRequest = JamiiPlusSetupRequest.builder()
              .product(product)
              .policyBeneficiary(policyBeneficiary)
              .benefit(benefit)
              .build();

      JamiiPlusSetupResponse jamiiPlusSetupResponse = setUpJamiiPlus(jamiiPlusSetupRequest, renewal);
      String track = jamiiPlusSetupResponse.getTrack();
      boolean hasPrincipal = jamiiPlusSetupResponse.getHasPrincipal();
      boolean hasSpouse = jamiiPlusSetupResponse.getHasSpouse();
      Beneficiary principalBeneficiary = jamiiPlusSetupResponse.getPrincipalBeneficiary();
      Beneficiary spouseBeneficiary = jamiiPlusSetupResponse.getSpouseBeneficiary();
      List<Beneficiary> childrenBeneficiary = jamiiPlusSetupResponse.getChildrenBeneficiary();
      int totalBeneficiary = jamiiPlusSetupResponse.getTotalBeneficiary();

      JamiiPlusMap jamiiPlusMap = productPremiumMap.getJamiiPlusMap();
      logger.debug("{}  {}Map {}", track, product, jamiiPlusMap);
      InpatientMap inpatientMap = jamiiPlusMap.getInpatient();
      logger.debug("{}  inpatientMap {}", track, inpatientMap);
      List<Integer> inpatientAgeList = jamiiPlusMap.getInpatientAge();
      logger.debug("{}  inpatientAgeList {}", track, inpatientAgeList);
      Integer inpatientLimit = benefit.getInpatientLimit();
      logger.info("{}  hasPrincipal {}", track, hasPrincipal);
      logger.info("{}  hasSpouse {}", track, hasSpouse);
      logger.info("{}  Children {}", track, policyBeneficiary.getChildren());

      if (hasPrincipal || hasSpouse) {
        List<Integer> inpatientAgeIndexList = this.getInpatientAgeIndexList(policyBeneficiary, inpatientAgeList, track);
        String inpatientAgeIndexListJson = g.toJson(inpatientAgeIndexList);
        logger.info("{}  inpatientAgeIndexList json {}", track, inpatientAgeIndexListJson);
        // Inpatient Principal and Spouse
        int principalAgeIndex = inpatientAgeIndexList.get(0);

        if (hasSpouse) {
          logger.debug("{}, {} cover principal and spouse", track, product);
          // calculate inpatient premium
          inpatientPrincipalPremium = inpatientMap.getPrincipal().get(inpatientLimit, principalAgeIndex);
          logger.info("{}  inpatientLimit {}", track, inpatientLimit);
          logger.info("{}  principalAgeIndex {}", track, principalAgeIndex);
          logger.info("{}  inpatientMap {}", track, inpatientMap);
          logger.info("{}  inpatientPrincipalPremium {}", track, inpatientPrincipalPremium);

          int spouseAgeIndex = inpatientAgeIndexList.get(1);
          inpatientSpousePremium = inpatientMap.getSpouse().get(inpatientLimit, spouseAgeIndex);

          // record premium
          BenefitPremium principalInpatientPremium = BenefitPremium.builder()
                  .benefitType(BenefitEnum.INPATIENT)
                  .benefitLimit(inpatientLimit)
                  .premium(BigDecimal.valueOf(inpatientPrincipalPremium))
                  .build();
          addPrincipalInpatientPremium(inpatientSpousePremium, track, principalBeneficiary, spouseBeneficiary, inpatientLimit, principalInpatientPremium);
        }
        else {
          // Inpatient Principal
          logger.info("{}, JP not cover spouse", track);
          // calculate inpatient premium
          inpatientPrincipalPremium = inpatientMap.getPrincipal().get(inpatientLimit, principalAgeIndex);
          logger.info("{} inpatientPrincipalPremium: {}", track, inpatientPrincipalPremium);
          // record premium
          BenefitPremium principalInpatientPremium = BenefitPremium.builder()
                  .benefitType(BenefitEnum.INPATIENT)
                  .benefitLimit(inpatientLimit)
                  .premium(BigDecimal.valueOf(inpatientPrincipalPremium))
                  .build();
          logger.info("{} principalInpatientPremium: {}", track, principalInpatientPremium);

          principalBeneficiary.getBenefitPremiums().add(principalInpatientPremium);

          logger.info("{} principalBeneficiary: {}", track, principalBeneficiary);
        }
      }
      if (hasPrincipal == false && hasSpouse == false && !childrenBeneficiary.isEmpty()) {
        logger.info("Computing premium for children only cover");
        JamiiPlusChildOnlyCoverMap jpChildOnlyCoverMap = productPremiumMap.getChildOnlyCoverMap();
        Map<Integer, Integer> inpatientChildOnly = jpChildOnlyCoverMap.getInpatient();
        inpatientPremium = calcInpatientForChildOnlyCover(childrenBeneficiary,
                track,
                inpatientChildOnly,
                inpatientLimit,
                inpatientChildrenPremium
                );

        logger.info("{}, #inpatientLimit: {}", track, inpatientLimit);
        logger.info("{}, *inpatientPrincipalPremium: {}, *inpatientSpousePremium: {} ,*inpatientChildrenPremium: {}",
                track, inpatientPrincipalPremium, inpatientSpousePremium, inpatientChildrenPremium);

        logger.info("{}, *inpatientPremium: {}", track, inpatientPremium);
        this.checkPremium(inpatientPrincipalPremium, inpatientSpousePremium, false, false, renewal);

        int optionalChildOnlyPremium = this.calculateOptionalPremium(benefit, jamiiPlusMap, totalBeneficiary, track, policyBeneficiary);
        totalPremium = inpatientPremium + optionalChildOnlyPremium;

        return BigDecimal.valueOf(totalPremium);
      }
      else {
        inpatientChildrenPremium = calcInpatientChildren(childrenBeneficiary,
                track,
                inpatientMap,
                inpatientLimit,
                inpatientAgeList,
                inpatientChildrenPremium);

        // total inpatient premium
        inpatientPremium += inpatientPrincipalPremium + inpatientSpousePremium + inpatientChildrenPremium;

        logger.info("{}, #inpatientLimit: {}", track, inpatientLimit);
        logger.info("{}, *inpatientPrincipalPremium: {}, *inpatientSpousePremium: {} ,*inpatientChildrenPremium: {}",
                track, inpatientPrincipalPremium, inpatientSpousePremium, inpatientChildrenPremium);

        logger.info("{}, *inpatientPremium: {}", track, inpatientPremium);
        logger.info("{}, *checkPremium hasPrincipal: {}, inpatientPrincipalPremium: {}", track, hasPrincipal, inpatientPrincipalPremium);
        logger.info("{}, *checkPremium hasSpouse: {},inpatientSpousePremium: {}", track, hasSpouse, inpatientSpousePremium);

        this.checkPremium(inpatientPrincipalPremium, inpatientSpousePremium, hasPrincipal, hasSpouse, renewal);

        // Optional
        int optionalPremium = this.calculateOptionalPremium(benefit, jamiiPlusMap, totalBeneficiary, track, policyBeneficiary);
        logger.info("{}, *optionalPremium: {}", track, optionalPremium);

        //Maternity
        int maternityPremium = this.calculateMaternityPremium(benefit, jamiiPlusMap, policyBeneficiary, track);
        logger.info("{}, *maternityPremium: {}", track, maternityPremium);
        // Travel Insurance
        logger.info("{}, *benefit.getTravelInsurance() > 0: {}", track, benefit.getTravelInsurance() > 0);


        if (benefit.getTravelInsurance() > 0) {
          int premium = jamiiPlusMap.getTravel().get(benefit.getTravelInsurance());
          this.recordBenefitPremium(BenefitEnum.TRAVEL, benefit.getTravelInsurance(), premium, policyBeneficiary);
        }

        totalPremium = inpatientPremium + optionalPremium + maternityPremium;

        logger.info("{} *inpatientPremium: {}, *optionalPremium: {}, *maternityPremium: {}",
                track, inpatientPremium, optionalPremium, maternityPremium);

        logger.info("{} ***JamiiPlus end***, *totalPremium: {}", track, totalPremium);

        return BigDecimal.valueOf(totalPremium);
      }
    }

    private int calcInpatientForChildOnlyCover(List<Beneficiary> childrenBeneficiary,
                                               String track,
                                               Map<Integer, Integer> inpatientChildOnly,
                                               Integer inpatientLimit,
                                               int inpatientChildrenPremium) {
    for (Beneficiary beneficiary: childrenBeneficiary) {
      int childPremium = inpatientChildOnly.get(inpatientLimit);
      inpatientChildrenPremium = inpatientChildrenPremium + childPremium;
      logger.info("{}, *inpatientChildrenPremium: {}", track, inpatientChildrenPremium);

      // record premium
      BenefitPremium childrenPremium = BenefitPremium.builder()
              .benefitLimit(inpatientLimit)
              .benefitType(BenefitEnum.INPATIENT)
              .premium(BigDecimal.valueOf(childPremium))
              .build();
      beneficiary.getBenefitPremiums().add(childrenPremium);
      logger.info("{}, beneficiary: {}", track, beneficiary);
      logger.info("{}, *childPremium: {}", track, childPremium);
    }

    return inpatientChildrenPremium;
    }

    private int calcInpatientChildren(List<Beneficiary> childrenBeneficiary,
                                      String track,
                                      InpatientMap inpatientMap,
                                      Integer inpatientLimit,
                                      List<Integer> inpatientAgeList,
                                      int inpatientChildrenPremium) {
        // Inpatient Children
        if (CollectionUtils.isNotEmpty(childrenBeneficiary)) {
            for (Beneficiary beneficiary : childrenBeneficiary) {
                int childAge = beneficiary.getAge();
                logger.debug("{}, childAge: {}", track, childAge);

                int inpatientChildAgeIndex = 0;
                int childPremium = 0;
                if (childAge <= 20) {
                    childPremium = inpatientMap.getChild().get(inpatientLimit);
                    logger.debug("{}, childPremium: {}", track, childPremium);
                } else {
                    // >20, same as the spouse premium
                    inpatientChildAgeIndex = this.calculateAgeIndex(childAge, inpatientAgeList);
                    logger.debug("{}, inpatientChildAgeIndex: {}", track, inpatientChildAgeIndex);

                    childPremium = inpatientMap.getPrincipal()
                                               .get(inpatientLimit, inpatientChildAgeIndex);
                    logger.debug("{}, childPremium: {}", track, childPremium);
                }

                // record premium
                BenefitPremium childrenPremium = BenefitPremium.builder()
                                                               .benefitLimit(inpatientLimit)
                                                               .benefitType(BenefitEnum.INPATIENT)
                                                               .premium(BigDecimal.valueOf(childPremium))
                                                               .build();
                beneficiary.getBenefitPremiums().add(childrenPremium);
                logger.debug("{}, beneficiary: {}", track, beneficiary);
                logger.debug("{}, *childPremium: {}", track, childPremium);
                inpatientChildrenPremium += childPremium;
                logger.debug("{}, *inpatientChildrenPremium: {}", track, inpatientChildrenPremium);
            }
        }
        return inpatientChildrenPremium;
    }

  private void recordBenefitPremium(BenefitEnum benefitEnum, Integer benefitLimit, int premium,
                                    PolicyBeneficiary policyBeneficiary) {
    if (policyBeneficiary.getPrincipal() != null) {
      BenefitPremium benefitPremium = BenefitPremium.builder()
                                                    .benefitType(benefitEnum)
                                                    .benefitLimit(benefitLimit)
                                                    .premium(BigDecimal.valueOf(premium))
                                                    .build();
      policyBeneficiary.getPrincipal()
                       .getBenefitPremiums()
                       .add(benefitPremium);
    }
    if (policyBeneficiary.getSpouse() != null) {
      BenefitPremium benefitPremium = BenefitPremium.builder()
                                                    .benefitType(benefitEnum)
                                                    .benefitLimit(benefitLimit)
                                                    .premium(BigDecimal.valueOf(premium))
                                                    .build();
      policyBeneficiary.getSpouse()
                       .getBenefitPremiums()
                       .add(benefitPremium);
    }
    int total = CollectionUtils.size(policyBeneficiary.getChildren());
    if (total > 0) {
      for (Beneficiary beneficiary : policyBeneficiary.getChildren()) {
        BenefitPremium benefitPremium = BenefitPremium.builder()
                                                      .benefitType(benefitEnum)
                                                      .benefitLimit(benefitLimit)
                                                      .premium(BigDecimal.valueOf(premium))
                                                      .build();
        beneficiary.getBenefitPremiums()
                   .add(benefitPremium);
      }
    }
  }

  private void recordMaternityBenefitPremium(BenefitEnum benefitEnum, Integer benefitLimit,
      int premium, PolicyBeneficiary policyBeneficiary) {
    if (policyBeneficiary.getPrincipal() != null && GenderEnum.FEMALE.getValue()
        .equals(policyBeneficiary.getPrincipal().getGender())) {
      BenefitPremium benefitPremium = BenefitPremium.builder().benefitType(benefitEnum)
          .benefitLimit(benefitLimit).premium(BigDecimal.valueOf(premium)).build();
      policyBeneficiary.getPrincipal().getBenefitPremiums().add(benefitPremium);
    }
    if (policyBeneficiary.getSpouse() != null && GenderEnum.FEMALE.getValue()
        .equals(policyBeneficiary.getSpouse().getGender())) {
      BenefitPremium benefitPremium = BenefitPremium.builder().benefitType(benefitEnum)
          .benefitLimit(benefitLimit).premium(BigDecimal.valueOf(premium)).build();
      policyBeneficiary.getSpouse().getBenefitPremiums().add(benefitPremium);
    }
  }

  private List<Integer> getInpatientAgeIndexList(PolicyBeneficiary policyBeneficiary,
      List<Integer> inpatientAgeList,String track) {
    List<Integer> list = new ArrayList<>();
    int principalAge = Optional.ofNullable(policyBeneficiary)
                               .map(PolicyBeneficiary::getPrincipal)
                               .map(Beneficiary::getAge)
                               .orElse(-1);
    logger.debug("{}  principalAge {}", track, principalAge);
    int spouseAge = Optional.ofNullable(policyBeneficiary)
                            .map(PolicyBeneficiary::getSpouse)
                            .map(Beneficiary::getAge)
                            .orElse(-1);
    logger.debug("{}  spouseAge {}", track, spouseAge);
    if (principalAge < 0 && spouseAge >= 0) {
      throw new BusinessException("no principal");
    }
    if (principalAge >= 0 && spouseAge >= 0) {
      int calcPrincipalAge = 0;
      int calcSpouseAge = 0;
      calcPrincipalAge = principalAge;
      calcSpouseAge = spouseAge;
      int principalAgeIndex = this.calculateAgeIndex(calcPrincipalAge, inpatientAgeList);
      logger.debug("{}  principalAgeIndex {}", track, principalAgeIndex);
      int spouseAgeIndex = this.calculateAgeIndex(calcSpouseAge, inpatientAgeList);
      logger.debug("{}  spouseAgeIndex {}", track, spouseAgeIndex);
      list.add(principalAgeIndex);
      list.add(spouseAgeIndex);
    } else if (principalAge >= 0) {
      list.add(this.calculateAgeIndex(principalAge, inpatientAgeList));
    }
    logger.debug("{}  getInpatientAgeIndexList {}", track, list);
    return list;
  }

  private int calculateOptionalPremium(Benefit benefit, JamiiPlusMap jamiiPlusMap,
      int totalBeneficiary, String track, PolicyBeneficiary policyBeneficiary) {
    int opticalPremium = 0;
    int dentalPremium = 0;

    //Outpatient
    int outpatientPremium = this.calculateOutpatientPremium(benefit.getOutpatientLimit(),
        jamiiPlusMap, policyBeneficiary);
    logger.debug("{}, #outpatientLimit: {}", track, benefit.getOutpatientLimit());
    logger.debug("{}, *outpatientPremium: {}", track, outpatientPremium);

    // calculate Dental/Optical/Optical Premium only when outpatient premium is available
    logger.debug("{}, *outpatientPremium > 0: {}", track, outpatientPremium > 0);
    if (outpatientPremium > 0) {
      // Dental
      if (benefit.getDentalLimit() > 0) {
        int premium = jamiiPlusMap.getDental().get(benefit.getDentalLimit());
        dentalPremium = premium * totalBeneficiary;
        this.recordBenefitPremium(BenefitEnum.DENTAL, benefit.getDentalLimit(),
            premium, policyBeneficiary);
      }
      logger.debug("{}, #dentalLimit: {}", track, benefit.getDentalLimit());
      logger.debug("{}, *dentalPremium: {}", track, dentalPremium);

      // Optical
      if (benefit.getOpticalLimit() > 0) {
        int premium = jamiiPlusMap.getOptical().get(benefit.getOpticalLimit());
        opticalPremium = premium * totalBeneficiary;
        this.recordBenefitPremium(BenefitEnum.OPTICAL, benefit.getOpticalLimit(),
            premium, policyBeneficiary);
      }
      logger.debug("{}, #opticalLimit: {}", track, benefit.getOpticalLimit());
      logger.debug("{}, *opticalPremium: {}", track, opticalPremium);
    }

    return outpatientPremium + opticalPremium + dentalPremium;
  }

  private int calculateMaternityPremium(Benefit benefit, JamiiPlusMap jamiiPlusMap,
      PolicyBeneficiary policyBeneficiary, String track) {
    int maternityPremium = 0;
    int maternityCoverNumber = this.getMaternityCoverNumber(policyBeneficiary);
    if (maternityCoverNumber > 0 && benefit.getMaternityLimit() > 0) {
      int premium = jamiiPlusMap.getMaternity().get(benefit.getMaternityLimit());
      maternityPremium = premium * maternityCoverNumber;
      this.recordMaternityBenefitPremium(BenefitEnum.MATERNITY, benefit.getMaternityLimit(),
          premium, policyBeneficiary);
    }
    logger.debug("{}, #maternityLimit: {}", track, benefit.getMaternityLimit());
    logger.debug("{}, maternityCoverNumber: {}, maternityPremium: {}", track, maternityCoverNumber, maternityPremium);
    return maternityPremium;
  }

  private void checkPremium(int inpatientPrincipalPremium, int inpatientSpousePremium,
      boolean principal, boolean spouse,boolean renewal) {
    if (renewal) {
      return;
    }
    if (principal && inpatientPrincipalPremium == 0 || (spouse && inpatientSpousePremium == 0)) {
      throw new BusinessException(
          "Sorry, we are unable to provide quotation at this stage, please contact our customer service at (+254) 0709 912 777 for more information.");
    }
  }

  private int getMaternityCoverNumber(PolicyBeneficiary policyBeneficiary) {
    int maternityCoverNumber = 0;
    if (GenderEnum.FEMALE.getValue()
        .equals(Optional.ofNullable(policyBeneficiary).map(PolicyBeneficiary::getPrincipal)
            .map(Beneficiary::getGender).orElse(null))) {
      maternityCoverNumber++;
    }
    if (GenderEnum.FEMALE.getValue()
        .equals(Optional.ofNullable(policyBeneficiary).map(PolicyBeneficiary::getSpouse)
            .map(Beneficiary::getGender).orElse(null))) {
      maternityCoverNumber++;
    }
    return maternityCoverNumber;
  }

  private int calculateOptionalPremium(int limit, MultiKeyMap<Integer, Integer> premiumMap,
      List<Integer> ageIndexList) {
    int optionalPremium = 0;
    if (limit > 0) {
      for (Integer ageIndex : ageIndexList) {
        optionalPremium += premiumMap.get(limit, ageIndex);
      }
    }
    return optionalPremium;
  }

  private int calculateOutpatientPremium(int limit, JamiiPlusMap jamiiPlusMap,
      PolicyBeneficiary policyBeneficiary) {
    int optionalPremium = 0;
    MultiKeyMap<Integer, Integer> premiumMap = jamiiPlusMap.getOutpatient();
    List<Integer> outpatientAgeList = jamiiPlusMap.getOutpatientAge();
    if (limit > 0) {
      int principalAge = Optional.ofNullable(policyBeneficiary).map(PolicyBeneficiary::getPrincipal)
          .map(Beneficiary::getAge).orElse(-1);
      if (principalAge >= 0) {
        int ageIndex = this.calculateAgeIndex(principalAge, outpatientAgeList);
        int outpatientPremium = premiumMap.get(limit, ageIndex);
        BenefitPremium benefitPremium = BenefitPremium.builder().benefitType(BenefitEnum.OUTPATIENT)
            .benefitLimit(limit).premium(BigDecimal.valueOf(outpatientPremium)).build();
        policyBeneficiary.getPrincipal().getBenefitPremiums().add(benefitPremium);
        optionalPremium += outpatientPremium;
      }
      int spouseAge = Optional.ofNullable(policyBeneficiary).map(PolicyBeneficiary::getSpouse)
          .map(Beneficiary::getAge).orElse(-1);
      if (spouseAge >= 0) {
        int ageIndex = this.calculateAgeIndex(spouseAge, outpatientAgeList);
        int outpatientPremium = premiumMap.get(limit, ageIndex);
        BenefitPremium benefitPremium = BenefitPremium.builder().benefitType(BenefitEnum.OUTPATIENT)
            .benefitLimit(limit).premium(BigDecimal.valueOf(outpatientPremium)).build();
        policyBeneficiary.getSpouse().getBenefitPremiums().add(benefitPremium);
        optionalPremium += outpatientPremium;
      }
      List<Beneficiary> childrenBeneficiary = policyBeneficiary.getChildren();
      if (CollectionUtils.isNotEmpty(childrenBeneficiary)) {
        for (Beneficiary child : childrenBeneficiary) {
          if (principalAge < 0 && spouseAge < 0) {
            logger.info("Computing outpatient for children only cover");
            JamiiPlusChildOnlyCoverMap jamiiPlusChildOnlyCoverMap = productPremiumMap.getChildOnlyCoverMap();
            int outpatientChildOnlyPremium = jamiiPlusChildOnlyCoverMap.getOutpatient().get(limit);
            BenefitPremium benefitPremium = BenefitPremium.builder()
                    .benefitType(BenefitEnum.OUTPATIENT)
                    .benefitLimit(limit).premium(BigDecimal.valueOf(outpatientChildOnlyPremium)).build();
            child.getBenefitPremiums().add(benefitPremium);
            optionalPremium += outpatientChildOnlyPremium;
          }
          else {
            int ageIndex = this.calculateAgeIndex(child.getAge(), outpatientAgeList);
            int outpatientPremium = premiumMap.get(limit, ageIndex);
            BenefitPremium benefitPremium = BenefitPremium.builder()
                    .benefitType(BenefitEnum.OUTPATIENT)
                    .benefitLimit(limit).premium(BigDecimal.valueOf(outpatientPremium)).build();
            child.getBenefitPremiums().add(benefitPremium);
            optionalPremium += outpatientPremium;
          }
        }
      }
    }

    return optionalPremium;
  }

  private BigDecimal calculateAfyaNafuuPremium(PolicyBeneficiary policyBeneficiary,
      Benefit benefit) {
    int totalPremium = 0;
    int inpatientPremium = 0;
    int outpatientPremium = 0;
    int maternityPremium = 0;

    logger
        .info("{} ***AfyaNafuu start***, policyBeneficiary: {}, benefit: {}",
            GlobalConstant.CALCULATE_PREMIUM, policyBeneficiary, benefit);

    AfyaNafuuMap afyaNafuuMap = productPremiumMap.getAfyaNafuuMap();
    List<Integer> inpatientAgeList = afyaNafuuMap.getInpatientAge();
    List<Integer> outpatientAgeList = afyaNafuuMap.getOutpatientAge();

    // Inpatient
    this.validateBeneficiary(policyBeneficiary);
    int totalBeneficiary = this.getTotalBeneficiary(policyBeneficiary);
    logger.info("{}, AF totalBeneficiary: {}", GlobalConstant.CALCULATE_PREMIUM, totalBeneficiary);
    Beneficiary principalBeneficiary = policyBeneficiary.getPrincipal();
    Beneficiary spouseBeneficiary = policyBeneficiary.getSpouse();
    List<Beneficiary> childrenBeneficiary = policyBeneficiary.getChildren();
    List<Integer> inpatientAgeIndexList = new ArrayList<>();
    List<Integer> outpatientAgeIndexList = new ArrayList<>();
    if (principalBeneficiary != null || spouseBeneficiary != null) {

      int principalAge = policyBeneficiary.getPrincipal().getAge();
      logger.debug("{}, principalAge: {}", GlobalConstant.CALCULATE_PREMIUM, principalAge);
      if (spouseBeneficiary != null) {
        int spouseAge = spouseBeneficiary.getAge();
        logger.debug("{}, spouseAge: {}", GlobalConstant.CALCULATE_PREMIUM, spouseAge);
        inpatientAgeIndexList.add(this.calculateAgeIndex(principalAge, inpatientAgeList));
        inpatientAgeIndexList.add(this.calculateAgeIndex(spouseAge, inpatientAgeList));
        outpatientAgeIndexList.add(this.calculateAgeIndex(principalAge, outpatientAgeList));
        outpatientAgeIndexList.add(this.calculateAgeIndex(spouseAge, outpatientAgeList));
      } else {
        logger.debug("{}, AF not cover spouse", GlobalConstant.CALCULATE_PREMIUM);
        inpatientAgeIndexList.add(this.calculateAgeIndex(principalAge, inpatientAgeList));
        outpatientAgeIndexList.add(this.calculateAgeIndex(principalAge, outpatientAgeList));
      }
    }

    if (CollectionUtils.isEmpty(childrenBeneficiary)) {
      logger.debug("{}, AF not cover children", GlobalConstant.CALCULATE_PREMIUM);
    }
    for (Beneficiary beneficiary : childrenBeneficiary) {
      int childAge = beneficiary.getAge();
      logger.debug("{}, childAge: {}", GlobalConstant.CALCULATE_PREMIUM, childAge);
      inpatientAgeIndexList.add(calculateAgeIndex(childAge, inpatientAgeList));
      outpatientAgeIndexList.add(calculateAgeIndex(childAge, outpatientAgeList));
    }
    logger.debug("{}, ageIndexList: {}", GlobalConstant.CALCULATE_PREMIUM, inpatientAgeIndexList);

    inpatientPremium = this.calculateOptionalPremium(benefit.getInpatientLimit(),
        afyaNafuuMap.getInpatient(), inpatientAgeIndexList);
    logger.debug("{}, inpatientLimit: {}", GlobalConstant.CALCULATE_PREMIUM,
        benefit.getInpatientLimit());
    logger.debug("{}, inpatientPremium: {}", GlobalConstant.CALCULATE_PREMIUM, inpatientPremium);

    //Outpatient
    outpatientPremium = this.calculateOptionalPremium(benefit.getOutpatientLimit(),
        afyaNafuuMap.getOutpatient(), outpatientAgeIndexList);
    logger.debug("{}, outpatientLimit: {}", GlobalConstant.CALCULATE_PREMIUM,
        benefit.getOutpatientLimit());
    logger
        .debug("{}, outpatientPremium: {}", GlobalConstant.CALCULATE_PREMIUM, outpatientPremium);

    //Maternity
    int maternityCoverNumber = this.getMaternityCoverNumber(policyBeneficiary);
    if (maternityCoverNumber > 0 && benefit.getMaternityLimit() > 0) {
      maternityPremium = afyaNafuuMap.getMaternity().get(benefit.getMaternityLimit()) * maternityCoverNumber;
    }
    logger.debug("{}, maternityLimit: {}", GlobalConstant.CALCULATE_PREMIUM,
        benefit.getMaternityLimit());
    logger.debug("{}, maternityCoverNumber: {}, maternityPremium: {}",
        GlobalConstant.CALCULATE_PREMIUM, maternityCoverNumber, maternityPremium);

    totalPremium = inpatientPremium + outpatientPremium + maternityPremium;
    logger.debug("{} ***AfyaNafuu end***, totalPremium: {}", GlobalConstant.CALCULATE_PREMIUM,
        totalPremium);

    return BigDecimal.valueOf(totalPremium);
  }

  private BigDecimal calculateFeminaPremium(PolicyBeneficiary policyBeneficiary,
      Benefit benefit) {
    int totalPremium = 0;
    int principalPremium = 0;
    int spousePremium = 0;
    int childrenPremium = 0;

    logger.debug("{} ***Femina start***, policyBeneficiary: {}, benefit: {}",
        GlobalConstant.CALCULATE_PREMIUM, policyBeneficiary, benefit);
    FeminaMap feminaMap = productPremiumMap.getFeminaMap();
    List<Integer> ageList = feminaMap.getAge();

    this.validateBeneficiary(policyBeneficiary);
    int totalBeneficiary = this.getTotalBeneficiary(policyBeneficiary);
    logger.info("{}, FA totalBeneficiary: {}", GlobalConstant.CALCULATE_PREMIUM, totalBeneficiary);
    Beneficiary principalBeneficiary = policyBeneficiary.getPrincipal();
    Beneficiary spouseBeneficiary = policyBeneficiary.getSpouse();
    List<Beneficiary> childrenBeneficiary = policyBeneficiary.getChildren();

    if (principalBeneficiary != null) {
      int principalAge = principalBeneficiary.getAge();
      int principalAgeIndex = this.calculateAgeIndex(principalAge, ageList);
      principalPremium = feminaMap.getFemina().get(benefit.getPrincipal(), principalAgeIndex);
      logger.debug("{}, principalAge: {}, principalAgeIndex: {}, limit: {}",
          GlobalConstant.CALCULATE_PREMIUM, principalAge, principalAgeIndex,
          benefit.getPrincipal());
      logger
          .debug("{}, principalPremium: {}", GlobalConstant.CALCULATE_PREMIUM, principalPremium);
    } else {
      logger.debug("{}, FA not cover spouse", GlobalConstant.CALCULATE_PREMIUM);
    }

    if (spouseBeneficiary != null) {
      int spouseAge = spouseBeneficiary.getAge();
      int spouseAgeIndex = calculateAgeIndex(spouseAge, ageList);
      spousePremium = feminaMap.getFemina().get(benefit.getSpouse(), spouseAgeIndex);
      logger.debug("{}, spouseAge: {}, spouseAgeIndex: {}, limit: {}",
          GlobalConstant.CALCULATE_PREMIUM, spouseAge, spouseAgeIndex, benefit.getSpouse());
      logger.debug("{}, spousePremium: {}", GlobalConstant.CALCULATE_PREMIUM, spousePremium);
    } else {
      logger.debug("{}, FA not cover spouse", GlobalConstant.CALCULATE_PREMIUM);
    }

    if (CollectionUtils.isEmpty(childrenBeneficiary)) {
      logger.debug("{}, FA not cover children", GlobalConstant.CALCULATE_PREMIUM);
    }
    for (int i = 0; i < childrenBeneficiary.size(); i++) {
      int childAge = childrenBeneficiary.get(i).getAge();
      int childAgeIndex = calculateAgeIndex(childAge, ageList);
      int premium = feminaMap.getFemina().get(benefit.getChildren().get(i), childAgeIndex);
      childrenPremium += premium;
      logger.debug("{}, childAge: {}, childAgeIndex: {}, limit: {}",
          GlobalConstant.CALCULATE_PREMIUM, childAge, childAgeIndex,
          benefit.getChildren().get(i));
      logger.debug("{}, premium: {}", GlobalConstant.CALCULATE_PREMIUM, premium);
    }
    logger.debug("{}, childrenPremium: {}", GlobalConstant.CALCULATE_PREMIUM, childrenPremium);

    totalPremium = principalPremium + spousePremium + childrenPremium;
    logger.debug("{} ***Femina end***, totalPremium: {}", GlobalConstant.CALCULATE_PREMIUM, totalPremium);

    return BigDecimal.valueOf(totalPremium);
  }


  private int calculateAgeIndex(int age, List<Integer> ageList) {
    int index = 0;
    for (int i = 0; i < ageList.size(); i++) {
      if (age <= ageList.get(i)) {
        index = i;
        break;
      }
    }
    return index;
  }

  @Override
  public Benefit createDefaultBenefit(CustomerDetailResponse customerDetailResponse,
      ProductEnum productEnum) {
    DefaultBenefit defaultBenefit = DefaultBenefit.builder().build();
    Benefit benefit = Benefit.builder().build();
    DependantBenefit dependantBenefit = customerDetailResponse.getBenefit();
    if (ProductEnum.JAMIIPLUS == productEnum) {
      benefit = this.createJPBenefit(defaultBenefit, dependantBenefit);
    } else if (ProductEnum.JAMIIPLUS_SHARED == productEnum) {
      benefit = this.createJPSBenefit(defaultBenefit, dependantBenefit);
    } else if (ProductEnum.AFYANAFUU == productEnum) {
      AfyaNafuuBenefit afyaNafuuBenefit = defaultBenefit.getAfyaNafuuBenefit();
      benefit.setInpatientLimit(afyaNafuuBenefit.getInpatientLimit());
      int outpatientLimit =
          dependantBenefit.isOutpatient() ? afyaNafuuBenefit.getOutpatientLimit() : 0;
      benefit.setOutpatientLimit(outpatientLimit);
      int maternityLimit =
          dependantBenefit.isMaternity() ? afyaNafuuBenefit.getMaternityLimit() : 0;
      benefit.setMaternityLimit(maternityLimit);
    } else if (ProductEnum.FEMINA == productEnum) {
      benefit.setPrincipal(defaultBenefit.getFeminaBenefit().getPrincipal());
      benefit.setSpouse(defaultBenefit.getFeminaBenefit().getSpouse());

      if (customerDetailResponse.getChildren() != null) {
        List<Integer> children = new ArrayList<>();
        for (int i = 0; i < customerDetailResponse.getChildren().getCount(); i++) {
          children.add(defaultBenefit.getFeminaBenefit().getChildren());
        }
        benefit.setChildren(children);
      }
    }

    logger.debug("product: {}, default benefit: {}", productEnum.getValue(), benefit);
    return benefit;
  }

    private Benefit createJPSBenefit(DefaultBenefit defaultBenefit, DependantBenefit dependantBenefit) {
      Benefit benefit = Benefit.builder().build();
      JamiiPlusSharedBenefit jamiiPlusSharedBenefit = defaultBenefit.getJamiiPlusSharedBenefit();
      benefit.setInpatientLimit(jamiiPlusSharedBenefit.getInpatientLimit());
      int outpatientLimit =
              dependantBenefit.isOutpatient() ? jamiiPlusSharedBenefit.getOutpatientLimit() : 0;
      benefit.setOutpatientLimit(outpatientLimit);
      int dentalLimit = dependantBenefit.isDental() ? jamiiPlusSharedBenefit.getDentalLimit() : 0;
      benefit.setDentalLimit(dentalLimit);
      int opticalLimit = dependantBenefit.isOptical() ? jamiiPlusSharedBenefit.getOpticalLimit() : 0;
      benefit.setOpticalLimit(opticalLimit);
      int maternityLimit = dependantBenefit.isMaternity() ?
              jamiiPlusSharedBenefit.getMaternityLimit() : 0;
      benefit.setMaternityLimit(maternityLimit);
      int travelInsuranceLimit =
              dependantBenefit.isTravelInsurance() ? jamiiPlusSharedBenefit.getTravelInsurance() : 0;
      benefit.setTravelInsurance(travelInsuranceLimit);
      return benefit;
    }
    private Benefit createJPBenefit(DefaultBenefit defaultBenefit, DependantBenefit dependantBenefit) {
    Benefit benefit = Benefit.builder().build();
    JamiiPlusBenefit jamiiPlusBenefit = defaultBenefit.getJamiiPlusBenefit();
    benefit.setInpatientLimit(jamiiPlusBenefit.getInpatientLimit());
    int outpatientLimit =
        dependantBenefit.isOutpatient() ? jamiiPlusBenefit.getOutpatientLimit() : 0;
    benefit.setOutpatientLimit(outpatientLimit);
    int dentalLimit = dependantBenefit.isDental() ? jamiiPlusBenefit.getDentalLimit() : 0;
    benefit.setDentalLimit(dentalLimit);
    int opticalLimit = dependantBenefit.isOptical() ? jamiiPlusBenefit.getOpticalLimit() : 0;
    benefit.setOpticalLimit(opticalLimit);
    int maternityLimit = dependantBenefit.isMaternity() ?
        jamiiPlusBenefit.getMaternityLimit() : 0;
    benefit.setMaternityLimit(maternityLimit);
    int travelInsuranceLimit =
        dependantBenefit.isTravelInsurance() ? jamiiPlusBenefit.getTravelInsurance() : 0;
    benefit.setTravelInsurance(travelInsuranceLimit);
    return benefit;
  }

  @Override
  //Deprecated
  public RenewalPremium calculateRenewalPremium(PolicyBeneficiary policyBeneficiary,
      Integer productId, Benefit benefit, PolicyClaim policyClaim, BigDecimal adjustment) {
    logger.debug("{} ***start***, policyBeneficiary: {}, benefit: {}, policyClaim: {}",
        GlobalConstant.CALCULATE_RENEWAL_PREMIUM, policyBeneficiary, benefit, policyClaim);
    String policyClaimJson =  g.toJson(policyClaim);
    logger.debug("policyClaimJson {}", policyClaimJson);
    logger.debug("adjustment {}", adjustment);
    BigDecimal updatePremium = this.calculatePremium(policyBeneficiary, productId, benefit, true);

    double adjustmentPercent = adjustment.doubleValue() / 100;
    logger.debug("adjustmentPercent {}", adjustmentPercent);

    BigDecimal newUpdatePremium = updatePremium.multiply(BigDecimal.valueOf(1).add(BigDecimal.valueOf(adjustmentPercent)));
    logger.debug("newUpdatePremium: {}", newUpdatePremium);

    double lossRatio = 0.0;
    double loadingPercent = 1;
    double claimsPaid = policyClaim.getClaimsPaid().doubleValue();
    double earnedPremium = policyClaim.getEarnedPremium().doubleValue();

    logger.debug("claimsPaid: {}, earnedPremium: {}", claimsPaid, earnedPremium);
    if (earnedPremium > 0) {

      lossRatio = (claimsPaid / ((earnedPremium * 10 / 12) * GlobalConstant.LOSSRATIO_FACTOR));
      logger.debug("lossRatio: {}", lossRatio);
      loadingPercent = this.calculateLoadingPercent(lossRatio);
      logger.debug(LOADING_PERCENT, loadingPercent);
    }
    logger.debug("lossRatio: {}, loadingPercent: {}", lossRatio, loadingPercent);

    BigDecimal loading = FormatUtils.scaleValue(newUpdatePremium.multiply(BigDecimal.valueOf(loadingPercent)));
    logger.debug("loading: {}", loading);

    double discountPercent = this.calculateDiscountPercent(policyClaim.getNoClaimYear());
    logger.debug("discountPercent: {}", discountPercent);

    BigDecimal discount = FormatUtils.scaleValue(newUpdatePremium.multiply(BigDecimal.valueOf(discountPercent)));
    logger.debug("discount: {}", discount);

    BigDecimal renewalPremium = FormatUtils.scaleValue(newUpdatePremium.add(loading).subtract(discount));
    logger.debug("renewalPremium: {}", renewalPremium);
    logger.debug(
        "{} ***end***, loadingPercent: {}, loading: {}, discountPercent: {}, discount: {}",
        GlobalConstant.CALCULATE_RENEWAL_PREMIUM, loadingPercent, loading, discountPercent, discount);
    logger.debug("{} ***end***, updatePremium: {}, newUpdatePremium: {}, renewalPremium : {}", GlobalConstant.CALCULATE_RENEWAL_PREMIUM, updatePremium, newUpdatePremium, renewalPremium);
    logger.debug("{} ***end***, adjustment: {}, renewalPremium: {}", GlobalConstant.CALCULATE_RENEWAL_PREMIUM, adjustment, renewalPremium);

    Premium premium = this.calculateTotalPremium(renewalPremium, false);
    logger.debug("calculateTotalPremium: {}", premium);

    BigDecimal itl = premium.getItl();
    BigDecimal phcf = premium.getPhcf();
    BigDecimal stampDuty = premium.getStampDuty();
    // calculate total renewal premium
    BigDecimal totalPremium = premium.getTotalPremium();

    logger.debug("{} ***end***, itl: {}, phcf: {}, stampDuty: {}, totalPremium: {}",
        GlobalConstant.CALCULATE_RENEWAL_PREMIUM, itl, phcf, stampDuty, totalPremium);
    return RenewalPremium.builder().claimsPaid(policyClaim.getClaimsPaid())
        .earnedPremium(policyClaim.getEarnedPremium()).lossRatio(BigDecimal.valueOf(lossRatio))
        .manualAdjustment(adjustment)
        .loading(loading).discount(discount).premium(renewalPremium)
        .itl(itl).phcf(phcf).stampDuty(stampDuty).totalPremium(totalPremium).build();
  }

  @Override
  public RenewalPremium calcRenewPremiumByTotalPremiumForComingWorker(PolicyBeneficiary policyBeneficiary,
                                                       PolicyDetail policyDetail,
                                                       Benefit benefit,
                                                       PolicyClaim policyClaim,
                                                       BigDecimal adjustment) {
    logger.debug("6. start  calcRenewPremiumByTotalPremiumForComingWorker: {}", policyDetail.getPolicyNumber());
    PolicyBeneficiary policyBeneficiaryCopy = new PolicyBeneficiary();
    Benefit benefitCopy = new Benefit();
    BeanUtils.copyProperties(policyBeneficiary, policyBeneficiaryCopy);
    BeanUtils.copyProperties(benefit, benefitCopy);

    // 1.Get premiumPaid from endpoint(SQL script)
    BigDecimal totalPremium = policyDetail.getTotalPremium();
    //2.Remove tax. premiumPaid - (0.45% * premiumPaid)
    logger.debug("6.2 totalPremium: {}", totalPremium);
    BigDecimal totalPremiumWithoutTax = totalPremium.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100.45), 2, RoundingMode.HALF_UP);

    //2.lossRatio = claimsPaid / (premiumPaid * 10 / 12) (lossRatio = Claims Ratio)
    double lossRatio;
    double loadingPercent = 1;

    double claimsPaid = policyClaim.getClaimsPaid().doubleValue();

    lossRatio = (claimsPaid / ((totalPremiumWithoutTax.doubleValue() * 10 / 12) * GlobalConstant.LOSSRATIO_FACTOR));
    logger.debug("6.3 lossRatio: {}", lossRatio);

    BigDecimal earnedPremium = BigDecimal.valueOf(totalPremiumWithoutTax.doubleValue() * 10 / 12);
    //3.loading percentage = refer to  lossRatio VS Premium Loadings(loading percentage) table
    loadingPercent = this.calculateLoadingPercent(lossRatio);

    //4.loading amount  = premiumPaid * loading percentage
    BigDecimal loadingAmount = totalPremiumWithoutTax.multiply(BigDecimal.valueOf(loadingPercent))
            .setScale(2, RoundingMode.HALF_UP);

    //5.discount percentage = noClaimYear (0-3 years no discount,3 years - 0.025, 4 years - 0.05, 5 years - 0.075,6 year or more - 0.1
    double discountPercent = this.calculateDiscountPercent(policyClaim.getNoClaimYear());
    logger.debug("6.5 discountPercent: {}", discountPercent);
    //6.discount amount = premiumPaid * discount percentage
    BigDecimal discountAmount = totalPremiumWithoutTax.multiply(BigDecimal.valueOf(discountPercent))
            .setScale(2, RoundingMode.HALF_UP);
//  7.renewal Premium = premiumPaid + loading amount - discount amount + add premium change due to change in age.
    BigDecimal normalAgeRenewPremium = this.calculatePremium(policyBeneficiary, policyDetail.getProductId(), benefit, true);


    //8.loading amountChangeInAge  = premiumPaid * loading percentage
    BigDecimal loadingAmountCurrentAge = normalAgeRenewPremium.multiply(BigDecimal.valueOf(loadingPercent))
            .setScale(2, RoundingMode.HALF_UP);
    logger.info("loadingAmountCurrentAge: {}", loadingAmountCurrentAge);

    PolicyBeneficiary policyBeneficiaryUpdate = reduceAgeForAllPolicyBeneficiary(policyBeneficiaryCopy);
    BigDecimal reduceAgeRenewPremium = this.calculatePremium(policyBeneficiaryUpdate, policyDetail.getProductId(), benefitCopy, true);

    BigDecimal premiumChangeInAge = normalAgeRenewPremium.subtract(reduceAgeRenewPremium);
    logger.debug("Change in Age ==>  : {}", premiumChangeInAge);

    BigDecimal renewalPremium =  totalPremiumWithoutTax.add(loadingAmount).subtract(discountAmount).add(premiumChangeInAge);

    BigDecimal renewalPremiumCurrentAge =  normalAgeRenewPremium.add(loadingAmountCurrentAge).subtract(discountAmount);
    logger.debug("normalAgeRenewPremium + loadingAmount - discountAmount :{}", renewalPremium);

    if (renewalPremiumCurrentAge.compareTo(renewalPremium) > 0){
      renewalPremium = renewalPremiumCurrentAge;
    }

    if (loadingAmountCurrentAge.compareTo(loadingAmount) > 0){
      loadingAmount = loadingAmountCurrentAge;
    }

    BigDecimal itl = ProductUtils.calculateITL(renewalPremium);

    BigDecimal phcf = ProductUtils.calculatePHCF(renewalPremium);

    BigDecimal premiumWithIncludeTax =  renewalPremium.add(itl).add(phcf).setScale(0, RoundingMode.UP);
    BigDecimal stampDuty = BigDecimal.ZERO;

    return RenewalPremium.builder()
            .claimsPaid(policyClaim.getClaimsPaid())
            .earnedPremium(earnedPremium)
            .lossRatio(BigDecimal.valueOf(lossRatio))
            .manualAdjustment(adjustment)
            .loading(loadingAmount)
            .discount(discountAmount)
            .premium(renewalPremium)
            .itl(itl)
            .phcf(phcf)
            .stampDuty(stampDuty)
            .totalPremium(premiumWithIncludeTax)
            .changeInAgePremium(premiumChangeInAge)
            .loadingPercentage(BigDecimal.valueOf(loadingPercent))
            .build();
  }
  @Override
  public RenewalPremium calcRenewPremiumByTotalPremium(PolicyBeneficiary policyBeneficiary,
                                                       PolicyDetail policyDetail,
                                                       Benefit benefit,
                                                       PolicyClaim policyClaim,
                                                       BigDecimal adjustment) {

    PolicyBeneficiary policyBeneficiaryCopy = new PolicyBeneficiary();
    Benefit benefitCopy = new Benefit();
    BeanUtils.copyProperties(policyBeneficiary, policyBeneficiaryCopy);
    BeanUtils.copyProperties(benefit, benefitCopy);


    StringBuilder calcLog = new StringBuilder();
    logger.info("########  calcRenewPremiumByTotalPremium start ########");
    String logPolicyBeneficiary = g.toJson(policyBeneficiary);
    String logPolicyDetail =  g.toJson(policyDetail);
    String logBenefit = g.toJson(benefit);
    String logAdjustment = g.toJson(adjustment);
    logger.info("policyBeneficiary: {}", logPolicyBeneficiary);
    logger.info("policyDetail: {}", logPolicyDetail);
    logger.info("benefit: {}", logBenefit);
    logger.info("adjustment: {}", logAdjustment);

    // 1.Get premiumPaid from endpoint(SQL script)
    BigDecimal totalPremium = policyDetail.getTotalPremium();
    logger.info("totalPremium {}", totalPremium);

    calcLog.append("1.Get premiumPaid from endpoint(SQL script)").append("\t");
    calcLog.append("  premiumPaid = ").append(totalPremium).append("\t");

    //2.Remove tax. premiumPaid - (0.45% * premiumPaid)
    BigDecimal tax = totalPremium.subtract(new BigDecimal(100).multiply(totalPremium)
                                                              .divide(BigDecimal.valueOf(100.45), 2, RoundingMode.HALF_UP));
    logger.info("tax : {}", tax);
    calcLog.append("tax = ").append(tax).append("\t");

    BigDecimal totalPremiumWithoutTax = totalPremium.multiply(BigDecimal.valueOf(100))
                                                    .divide(BigDecimal.valueOf(100.45), 2, RoundingMode.HALF_UP);

    logger.info("totalPremiumWithoutTax remove 0.45% tax: {}", totalPremiumWithoutTax);
    calcLog.append("2.premiumPaid  =  100*premiumPaid/100.45  ").append(" (remove 0.45% tax) ").append("\t");
    calcLog.append("  premiumPaid  = 100*").append(totalPremium).append("/100.45 ").append("=").append(totalPremiumWithoutTax).append("\t");


    //2.lossRatio = claimsPaid / (premiumPaid * 10 / 12) (lossRatio = Claims Ratio)
    double lossRatio;
    double loadingPercent = 1;

    double claimsPaid = policyClaim.getClaimsPaid().doubleValue();

    logger.debug("claimsPaid:  {}", claimsPaid);
    calcLog.append("2.claimsPaid = ").append(claimsPaid).append("\t");

    lossRatio = (claimsPaid / ((totalPremiumWithoutTax.doubleValue() * 10 / 12) * GlobalConstant.LOSSRATIO_FACTOR));
    logger.info("LR lossRatio: {}", lossRatio);
    calcLog.append("lossRatio = claimsPaid/ (premiumPaid * 10/12) = ").append(lossRatio).append("\t");
    calcLog.append("lossRatio = ")
           .append(claimsPaid)
           .append("/(")
           .append(totalPremiumWithoutTax)
           .append(" * 10/12")
           .append(")")
           .append("=")
           .append(lossRatio)
           .append("\t");

    //3.loading percentage = refer to  lossRatio VS Premium Loadings(loading percentage) table
    loadingPercent = this.calculateLoadingPercent(lossRatio);
    logger.info(LOADING_PERCENT, loadingPercent);
    calcLog.append("3.loadingPercent = ").append(loadingPercent).append("\t");


    //4.loading amount  = premiumPaid * loading percentage
    BigDecimal loadingAmount = totalPremiumWithoutTax.multiply(BigDecimal.valueOf(loadingPercent))
                                                     .setScale(2, RoundingMode.HALF_UP);
    logger.info("loadingAmount: {}", loadingAmount);
    calcLog.append("4.loadingAmount = ").append(loadingAmount).append("\t");

    //5.discount percentage = noClaimYear (0-3 years no discount,3 years - 0.025, 4 years - 0.05, 5 years - 0.075,6 year or more - 0.1
    double discountPercent = this.calculateDiscountPercent(policyClaim.getNoClaimYear());
    calcLog.append("noClaimYear = ").append(policyClaim.getNoClaimYear()).append("\t");
    calcLog.append("discount percentage = noClaimYear (0-3 years no discount,3 years - 0.025, 4 years - 0.05, 5 years - 0.075,6 year or more - 0.1 \t");

    logger.info("discountPercent: {}", discountPercent);
    calcLog.append("5.discountPercent = ").append(discountPercent).append("\t");


    //6.discount amount = premiumPaid * discount percentage
    BigDecimal discountAmount = totalPremiumWithoutTax.multiply(BigDecimal.valueOf(discountPercent))
                                                      .setScale(2, RoundingMode.HALF_UP);
    logger.info("discountAmount: {}", discountAmount);
    calcLog.append("6.discountAmount = ").append(discountAmount).append("\t");

//  7.renewal Premium = premiumPaid + loading amount - discount amount + add premium change due to change in age.

    BigDecimal normalAgeRenewPremium = this.calculatePremium(policyBeneficiary, policyDetail.getProductId(), benefit, true);


    //8.loading amountChangeInAge  = premiumPaid * loading percentage
    BigDecimal loadingAmountCurrentAge = normalAgeRenewPremium.multiply(BigDecimal.valueOf(loadingPercent))
            .setScale(2, RoundingMode.HALF_UP);
    logger.info("loadingAmountCurrentAge: {}", loadingAmountCurrentAge);
    calcLog.append("4.loadingAmountCurrentAge = ").append(loadingAmountCurrentAge).append("\t");


    logger.info("***** start reduceAge RenewPremium ******");

    PolicyBeneficiary policyBeneficiaryUpdate = reduceAgeForAllPolicyBeneficiary(policyBeneficiaryCopy);
    BigDecimal reduceAgeRenewPremium = this.calculatePremium(policyBeneficiaryUpdate, policyDetail.getProductId(), benefitCopy, true);
    logger.info("***** end reduceAge RenewPremium ******");

    logger.info("normalAgeRenewPremium: {}", normalAgeRenewPremium);
    logger.info("reduceAgeRenewPremium: {}", reduceAgeRenewPremium);

    BigDecimal premiumChangeInAge = normalAgeRenewPremium.subtract(reduceAgeRenewPremium);



    logger.info("premiumChangeInAge: {}", premiumChangeInAge);
    calcLog.append("8.add premium change due to change in age = ").append(premiumChangeInAge).append("\t");


    BigDecimal renewalPremium =  totalPremiumWithoutTax.add(loadingAmount).subtract(discountAmount).add(premiumChangeInAge);
    logger.debug("renewalPremium + loadingAmount - discountAmount + premiumChangeInAge :{}", renewalPremium);

    BigDecimal renewalPremiumCurrentAge =  normalAgeRenewPremium.add(loadingAmountCurrentAge).subtract(discountAmount);
    logger.debug("normalAgeRenewPremium + loadingAmount - discountAmount :{}", renewalPremium);

    if (renewalPremiumCurrentAge.compareTo(renewalPremium) > 0){
      renewalPremium = renewalPremiumCurrentAge;
    }
    calcLog.append("renewal Premium = premiumPaid + loadingAmount - discountAmount + add premium change due to change in age.").append("\t");
    calcLog.append("renewal Premium = ")
           .append(totalPremiumWithoutTax)
           .append(" + ")
           .append(loadingAmount)
           .append(" - ")
           .append(discountAmount)
           .append(" + ")
           .append(premiumChangeInAge)
           .append("\t");

    BigDecimal itl = ProductUtils.calculateITL(renewalPremium);
    calcLog.append("itl = ").append(itl).append("\t");

    BigDecimal phcf = ProductUtils.calculatePHCF(renewalPremium);
    calcLog.append("phcf = ").append(phcf).append("\t");

    BigDecimal phcfAnditl = itl.add(phcf);

    logger.info("itl + phcf : {}", phcfAnditl);

    calcLog.append("renewalPremium = ").append(renewalPremium).append("\t");

    BigDecimal premiumWithIncludeTax =  renewalPremium.add(itl).add(phcf).setScale(0, RoundingMode.UP);

    logger.info("premiumWithoutTax: {}", renewalPremium);

    logger.info("renewalPremium + itl + phcf : {}", premiumWithIncludeTax);

    calcLog.append("final renewalPremium = renewalPremium + itl + phcf = ").append(premiumWithIncludeTax).append("\t");

    BigDecimal stampDuty = BigDecimal.ZERO;
    logger.info("***** Detailed calculation process start ******");
    String log = calcLog.toString();
    logger.info(log);
    logger.info("***** Detailed calculation process end ******");
    return RenewalPremium.builder()
                         .claimsPaid(policyClaim.getClaimsPaid())
                         .earnedPremium(policyClaim.getEarnedPremium())
                         .lossRatio(BigDecimal.valueOf(lossRatio))
                         .manualAdjustment(adjustment)
                         .loading(loadingAmount)
                         .discount(discountAmount)
                         .premium(renewalPremium)
                         .itl(itl)
                         .phcf(phcf)
                         .stampDuty(stampDuty)
                         .totalPremium(premiumWithIncludeTax)
                         .build();
  }


  public BigDecimal getTravelInsurancePremium(PolicyBeneficiary policyBeneficiary,
                                        Benefit benefit) {
    int travelInsurancePremium = 0;
    if (benefit.getTravelInsurance() > 0) {
      JamiiPlusMap jamiiPlusMap = productPremiumMap.getJamiiPlusMap();
      int premium = jamiiPlusMap.getTravel().get(benefit.getTravelInsurance());
      int totalBeneficiary = this.getTotalBeneficiary(policyBeneficiary);
      logger.info("totalBeneficiary: {}", totalBeneficiary);
      travelInsurancePremium = premium * totalBeneficiary;
    }
    return new BigDecimal(travelInsurancePremium);
  }

  public PolicyBeneficiary reduceAgeForAllPolicyBeneficiary(PolicyBeneficiary policyBeneficiarySource) {

    PolicyBeneficiary policyBeneficiaryCopy = new PolicyBeneficiary();
    BeanUtils.copyProperties(policyBeneficiarySource, policyBeneficiaryCopy);

    if (policyBeneficiaryCopy.getPrincipal() != null) {
      policyBeneficiarySource.getPrincipal().getBenefitPremiums().clear();
      logger.info("policyBeneficiary principal age:{}", policyBeneficiaryCopy.getPrincipal().getAge());
      policyBeneficiaryCopy.getPrincipal().setAge(policyBeneficiaryCopy.getPrincipal().getAge() - 1);
    }
    if (policyBeneficiaryCopy.getSpouse() != null) {
      logger.info("policyBeneficiary spouse age:{}", policyBeneficiaryCopy.getSpouse().getAge());
      policyBeneficiarySource.getSpouse().getBenefitPremiums().clear();
      policyBeneficiaryCopy.getSpouse().setAge(policyBeneficiaryCopy.getSpouse().getAge() - 1);
    }
    if (CollectionUtils.isNotEmpty(policyBeneficiaryCopy.getChildren())) {
      policyBeneficiaryCopy.getChildren().forEach(o -> {
      o.getBenefitPremiums().clear();
                                  logger.info("policyBeneficiary children age:{}", o.getAge());
                                  o.setAge(o.getAge() - 1);
                                });
    }
    return policyBeneficiaryCopy;
  }


  private double calculateLoadingPercent(double lossRatio) {
    List<Double> lossRateList = new ArrayList<>(Arrays.asList(0.62, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0, 1.1, MAX_VALUE));
    List<Double> loadPercentList = new ArrayList<>(Arrays.asList(0.0, 0.06, 0.08, 0.09, 0.10, 0.12, 0.13, 0.15, 0.16, 0.23));
    int index = 0;
    for (int i = 0; i < lossRateList.size(); i++) {
      if (lossRatio <= lossRateList.get(i)) {
        index = i;
        break;
      }
    }
    return loadPercentList.get(index);
  }

  private double calculateDiscountPercent(int year) {
    Map<Integer, Double> discountMap = new HashMap<>();
    discountMap.put(0, 0.0);
    discountMap.put(1, 0.0);
    discountMap.put(2, 0.0);
    discountMap.put(3, 0.025);
    discountMap.put(4, 0.05);
    discountMap.put(5, 0.075);
    discountMap.put(6, 0.1);
    discountMap.put(7, 0.1);
    discountMap.put(8, 0.1);
    discountMap.put(9, 0.1);
    discountMap.put(10, 0.1);
    return discountMap.get(year);
  }
}
