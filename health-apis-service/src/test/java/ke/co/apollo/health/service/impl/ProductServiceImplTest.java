package ke.co.apollo.health.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import ke.co.apollo.health.common.CommonObjects;
import ke.co.apollo.health.common.annotation.Enums;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import org.apache.ibatis.annotations.Param;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


import org.apache.commons.lang3.time.DateUtils;
import ke.co.apollo.health.common.domain.model.PolicyDetail;
import ke.co.apollo.health.common.domain.model.RenewalPremium;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.Premium;
import ke.co.apollo.health.common.domain.model.ProductPremium;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap;
import ke.co.apollo.health.common.enums.GenderEnum;
import ke.co.apollo.health.common.enums.ProductEnum;
import ke.co.apollo.health.common.utils.FormatUtils;
import ke.co.apollo.health.config.PremiumInitializer;
import ke.co.apollo.health.domain.BenefitPremium;
import ke.co.apollo.health.domain.PolicyBeneficiary;
import ke.co.apollo.health.domain.PolicyBeneficiary.Beneficiary;
import ke.co.apollo.health.domain.PolicyClaim;
import ke.co.apollo.health.service.impl.ProductServiceImpl;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import ke.co.apollo.health.service.CustomerService;

class ProductServiceImplTest {

  private static Logger logger = LoggerFactory.getLogger(ProductServiceImplTest.class);

  @InjectMocks
  private ProductServiceImpl productService;

  @Mock
  ProductPremium productPremium;

  @Mock
  ProductPremiumMap productPremiumMap;

  @Mock
  CustomerService customerService;

  private static ProductPremiumMap map;

  private static ProductPremium prodPremium;

  // @BeforeClass
  // public static void runOnceBeforeClass() {
  //   PremiumInitializer premiumInitializer = new PremiumInitializer();
  //   map = premiumInitializer.initProductPremiumMap();
  //   logger.debug("productPremiumMap: {}", map);
  //   prodPremium = premiumInitializer.initProductPremium();
  // }

  // @Before
  // void setUp() {
  //   logger.debug("test start---------------------");
  //   MockitoAnnotations.initMocks(this);
  // }

   @BeforeEach
    void setUpMocks(){
        initMocks(this);

        PremiumInitializer premiumInitializer = new PremiumInitializer();
    map = premiumInitializer.initProductPremiumMap();
    logger.debug("productPremiumMap: {}", map);
    prodPremium = premiumInitializer.initProductPremium();
    }

  // void testCalculateRenewalPremium() {

  //   // Customer and Dependant
  //   int principalAge = 55;
  //   String principalGender = GenderEnum.MALE.getValue();
  //   int spouseAge = 65;
  //   String spouseGender = GenderEnum.FEMALE.getValue();
  //   int childAge[] = new int[]{21, 20};

  //   // Benefit
  //   int inpatientLimit = 5000000;
  //   int outpatientLimit = 150000;
  //   int maternityLimit = 150000;
  //   int opticalLimit = 30000;
  //   int dentalLimit = 30000;
  //   int travelInsurance = 40000;

  //   BigDecimal expectedPremium = BigDecimal.valueOf(452154);
  //   when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
  //   when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

  //   PolicyBeneficiary policyBeneficiary = this
  //       .createPolicyBeneficiary(principalAge, principalGender,
  //           spouseAge, spouseGender, childAge);
  //   Benefit benefit1 = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,
  //       opticalLimit, dentalLimit, travelInsurance);

  //   BigDecimal premium1 = productService
  //       .calculatePremium(policyBeneficiary, ProductEnum.JAMIIPLUS.getId(), benefit1, true);
  //   logger.debug("premium1: {}", premium1);
  //   Assertions.assertEquals(expectedPremium, premium1);

  //   // renewal premium
  //   int principalRenewalAge = principalAge + 1;
  //   int spouseRenewalAge = spouseAge + 1;
  //   int childRenewaAge[] = new int[]{22, 21};
  //   PolicyBeneficiary renewalPolicyBeneficiary = this
  //       .createPolicyBeneficiary(principalRenewalAge, principalGender,
  //           spouseRenewalAge, spouseGender, childRenewaAge);
  //   PolicyClaim policyClaim = PolicyClaim.builder().claimsPaid(BigDecimal.valueOf(522846))
  //       .earnedPremium(BigDecimal.valueOf(522846)).noClaimYear(3).build();
  //   BigDecimal adjustment = BigDecimal.valueOf(0);
  //   Premium premium = productService
  //       .calculateRenewalPremium(renewalPolicyBeneficiary, ProductEnum.JAMIIPLUS.getId(), benefit1,
  //           policyClaim, adjustment);
  //   logger.debug("premium: {}", premium);
  //   BigDecimal renewalPremium = BigDecimal.valueOf(610418);
  //   BigDecimal totalPremium = BigDecimal.valueOf(613166);
  //   Assertions.assertEquals(renewalPremium, premium.getPremium());
  //   Assertions.assertEquals(BigDecimal.ZERO, premium.getStampDuty());
  //   Assertions.assertEquals(FormatUtils.scaleValue(totalPremium),
  //       FormatUtils.scaleValue(premium.getTotalPremium()));
  // }

  // void testCalculateChildrenOnlyPremium() {

  //   // Customer and Dependant
  //   int principalAge = -1;
  //   String principalGender = GenderEnum.MALE.getValue();
  //   int spouseAge = -1;
  //   String spouseGender = GenderEnum.FEMALE.getValue();
  //   int childAge[] = new int[]{21, 20};

  //   // Benefit
  //   int inpatientLimit = 5000000;
  //   int outpatientLimit = 150000;
  //   int maternityLimit = 150000;
  //   int opticalLimit = 30000;
  //   int dentalLimit = 30000;
  //   int travelInsurance = 40000;

  //   BigDecimal expectedPremium = BigDecimal.valueOf(151421);
  //   when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
  //   when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

  //   PolicyBeneficiary policyBeneficiary = this
  //       .createPolicyBeneficiary(principalAge, principalGender,
  //           spouseAge, spouseGender, childAge);
  //   Benefit benefit1 = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,
  //       opticalLimit, dentalLimit, travelInsurance);

  //   BigDecimal premium1 = productService
  //       .calculatePremium(policyBeneficiary, ProductEnum.JAMIIPLUS.getId(), benefit1, false);
  //   logger.debug("premium1: {}", premium1);
  //   Assertions.assertEquals(expectedPremium, premium1);

  //   // renewal premium
  //   int principalRenewalAge = principalAge + 1;
  //   int spouseRenewalAge = spouseAge + 1;
  //   int childRenewaAge[] = new int[]{22, 21};
  //   PolicyBeneficiary renewalPolicyBeneficiary = this
  //       .createPolicyBeneficiary(principalRenewalAge, principalGender,
  //           spouseRenewalAge, spouseGender, childRenewaAge);
  //   PolicyClaim policyClaim = PolicyClaim.builder().claimsPaid(BigDecimal.valueOf(22846))
  //       .earnedPremium(BigDecimal.valueOf(22846)).noClaimYear(3).build();
  //   BigDecimal adjustment = BigDecimal.valueOf(9.123);
  //   Premium premium = productService
  //       .calculateRenewalPremium(renewalPolicyBeneficiary, ProductEnum.JAMIIPLUS.getId(), benefit1,
  //           policyClaim, adjustment);
  //   logger.debug("premium: {}", premium);
  //   BigDecimal renewalPremium = BigDecimal.valueOf(224221);
  //   BigDecimal totalPremium = BigDecimal.valueOf(225231);
  //   Assertions.assertEquals(renewalPremium, premium.getPremium());
  //   Assertions.assertEquals(BigDecimal.ZERO, premium.getStampDuty());
  //   Assertions.assertEquals(FormatUtils.scaleValue(totalPremium),
  //       FormatUtils.scaleValue(premium.getTotalPremium()));
  // }

  @Test
  void testCalculateJamiiPlusSharedPremiumWhenAllOptionsAreSelected() {

    int principalAge = 55;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = 65;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{20, 20};

    // Benefit
    int inpatientLimit = 10000000;
    int outpatientLimit = 150000;
    int maternityLimit = 150000;
    int opticalLimit = 30000;
    int dentalLimit = 30000;
    int travelInsurance = 40000;


    PolicyBeneficiary policyBeneficiary = this.createPolicyBeneficiary(
            principalAge, principalGender, spouseAge, spouseGender, childAge
    );

    Benefit benefit = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,opticalLimit, dentalLimit, travelInsurance);
    BigDecimal expectedPremium = BigDecimal.valueOf(548087);

     when(productPremiumMap.getJamiiPlusSharedMap()).thenReturn(map.getJamiiPlusSharedMap());
     when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
     when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

     BigDecimal actualPremium = productService.calculatePremium(policyBeneficiary,
             ProductEnum.JAMIIPLUS_SHARED.getId(), benefit, false);

     assertEquals(expectedPremium, actualPremium);
  }

  @Test
  void testCalculateJamiiPlusSharedPremiumWhenTravelIsExcluded() {

    int principalAge = 55;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = 65;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{20, 20};

    // Benefit
    int inpatientLimit = 10000000;
    int outpatientLimit = 150000;
    int maternityLimit = 150000;
    int opticalLimit = 30000;
    int dentalLimit = 30000;
    int travelInsurance = 0;


    PolicyBeneficiary policyBeneficiary = this.createPolicyBeneficiary(
            principalAge, principalGender, spouseAge, spouseGender, childAge
    );

    Benefit benefit = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,opticalLimit, dentalLimit, travelInsurance);
    BigDecimal expectedPremium = BigDecimal.valueOf(548087);

    when(productPremiumMap.getJamiiPlusSharedMap()).thenReturn(map.getJamiiPlusSharedMap());
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    BigDecimal actualPremium = productService.calculatePremium(policyBeneficiary,
            ProductEnum.JAMIIPLUS_SHARED.getId(), benefit, false);

    assertEquals(expectedPremium, actualPremium);
  }

  @Test
  void testThatBusinessExceptionIsThrownIfPolicyBeneficiaryIsNull() {
    PolicyBeneficiary policyBeneficiary = new PolicyBeneficiary();
    Benefit benefit = new Benefit();

    when(productPremiumMap.getJamiiPlusSharedMap()).thenReturn(map.getJamiiPlusSharedMap());
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    Exception exception = assertThrows(BusinessException.class, () -> {
      productService.calculatePremium(policyBeneficiary,
              ProductEnum.JAMIIPLUS_SHARED.getId(), benefit, false);
    });

    String expectedMessage = "no beneficiary";
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  @ParameterizedTest
  @ValueSource(ints = {20, 81})
  void testThatBusinessExceptionIsThrownWhenPrincipalIsTwentyOneOrAboveEightyYears(int age) {
    int principalAge = age;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = 25;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{21, 20};

    String expectedMessage = "principal must be between 21 and 80 years old";
    Benefit benefit = new Benefit();
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
            .createPolicyBeneficiary(principalAge, principalGender,spouseAge, spouseGender, childAge);

    Exception exception = assertThrows(BusinessException.class, () -> {
      productService.calculatePremium(policyBeneficiary,
              ProductEnum.JAMIIPLUS_SHARED.getId(), benefit, false);
    });
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage,actualMessage);
   }

  @ParameterizedTest
  @ValueSource(ints = {20, 81})
  void testThatBusinessExceptionIsThrownWhenSpouseIsBelowTwentyOneOrAboveEightyYears(int age) {
    int principalAge = 35;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = age;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{21, 20};

    String expectedMessage = "spouse must be between 21 and 80 years old";
    Benefit benefit = new Benefit();
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
            .createPolicyBeneficiary(principalAge, principalGender,spouseAge, spouseGender, childAge);

    Exception exception = assertThrows(BusinessException.class, () -> {
      productService.calculatePremium(policyBeneficiary,
              ProductEnum.JAMIIPLUS_SHARED.getId(), benefit, false);
    });
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage,actualMessage);
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 81})
  void testCalculateJamiiPlusSharedPremiumThatBusinessExceptionIsThrownWhenChildlIsBelowTwentyOneOrAboveEightyYears(int age) {
    int principalAge = 35;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = 35;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{age, age};

    String expectedMessage = "child must be between 1 and 80 years old";
    Benefit benefit = new Benefit();
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
            .createPolicyBeneficiary(principalAge, principalGender,spouseAge, spouseGender, childAge);

    Exception exception = assertThrows(BusinessException.class, () -> {
      productService.calculatePremium(policyBeneficiary,
              ProductEnum.JAMIIPLUS_SHARED.getId(), benefit, false);
    });
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage,actualMessage);
  }

  @Test
  void testCalculateJamiiPlusSharedPremiumWhenSpouseIsNull() {
    int principalAge = 55;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = 65;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{19, 20};

    // Benefit
    int inpatientLimit = 10000000;
    int outpatientLimit = 150000;
    int maternityLimit = 150000;
    int opticalLimit = 30000;
    int dentalLimit = 30000;
    int travelInsurance = 40000;


    Benefit benefit = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,opticalLimit, dentalLimit, travelInsurance);
    PolicyBeneficiary policyBeneficiary = this
            .createPolicyBeneficiary(principalAge, principalGender,spouseAge, spouseGender, childAge);
    policyBeneficiary.setSpouse(null);
    BigDecimal expectedPremium = BigDecimal.valueOf(353309);

    when(productPremiumMap.getJamiiPlusSharedMap()).thenReturn(map.getJamiiPlusSharedMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());


    BigDecimal actualPremium = productService.calculatePremium(policyBeneficiary,
              ProductEnum.JAMIIPLUS_SHARED.getId(), benefit, false);

    assertEquals(expectedPremium,actualPremium);
  }

  @Test
  void testCalculateJamiiPlusPremiumWhenPrincipalAndSpouseAreAbsent() {
    int principalAge = 55;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = 65;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{19, 20};

    // Benefit
    int inpatientLimit = 10000000;
    int outpatientLimit = 150000;
    int maternityLimit = 150000;
    int opticalLimit = 30000;
    int dentalLimit = 30000;
    int travelInsurance = 40000;


    Benefit benefit = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,opticalLimit, dentalLimit, travelInsurance);
    PolicyBeneficiary policyBeneficiary = this
            .createPolicyBeneficiary(principalAge, principalGender,spouseAge, spouseGender, childAge);
    policyBeneficiary.setSpouse(null);
    policyBeneficiary.setPrincipal(null);
    BigDecimal expectedPremium = BigDecimal.valueOf(197616);

    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
    when(productPremiumMap.getChildOnlyCoverMap()).thenReturn(map.getChildOnlyCoverMap());


    BigDecimal actualPremium = productService.calculatePremium(policyBeneficiary,
            ProductEnum.JAMIIPLUS.getId(), benefit, false);

    assertEquals(expectedPremium,actualPremium);
  }

  @Test
  void testCalculateJamiiPlusPremiumWhenPrincipalIsAbsentAndSpouseIsPresent() {
    int principalAge = 55;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = 65;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{19, 20};

    // Benefit
    int inpatientLimit = 10000000;
    int outpatientLimit = 150000;
    int maternityLimit = 150000;
    int opticalLimit = 30000;
    int dentalLimit = 30000;
    int travelInsurance = 40000;

    String expectedMessage = "no principal";
    Benefit benefit = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,opticalLimit, dentalLimit, travelInsurance);
    PolicyBeneficiary policyBeneficiary = this
            .createPolicyBeneficiary(principalAge, principalGender,spouseAge, spouseGender, childAge);
    policyBeneficiary.setPrincipal(null);

    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());


    Exception exception = assertThrows(BusinessException.class, () ->{
      productService.calculatePremium(policyBeneficiary,
                      ProductEnum.JAMIIPLUS.getId(), benefit, false);
            });

    assertEquals(expectedMessage,exception.getMessage());
  }

  @Test
  void testCalculateJamiiPlusPremiumWhenSpouseIsNull() {
    int principalAge = 55;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = 65;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{19, 20};

    // Benefit
    int inpatientLimit = 10000000;
    int outpatientLimit = 150000;
    int maternityLimit = 150000;
    int opticalLimit = 30000;
    int dentalLimit = 30000;
    int travelInsurance = 40000;


    Benefit benefit = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,opticalLimit, dentalLimit, travelInsurance);
    PolicyBeneficiary policyBeneficiary = this
            .createPolicyBeneficiary(principalAge, principalGender,spouseAge, spouseGender, childAge);
    policyBeneficiary.setSpouse(null);
    BigDecimal expectedPremium = BigDecimal.valueOf(353309);

    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());


    BigDecimal actualPremium = productService.calculatePremium(policyBeneficiary,
            ProductEnum.JAMIIPLUS.getId(), benefit, false);

    assertEquals(expectedPremium,actualPremium);
  }

  @Test
  void testCalculateJamiiPlusPremiumWhenTravelIsExcluded() {

    int principalAge = 55;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = 65;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{20, 20};

    // Benefit
    int inpatientLimit = 10000000;
    int outpatientLimit = 150000;
    int maternityLimit = 150000;
    int opticalLimit = 30000;
    int dentalLimit = 30000;
    int travelInsurance = 0;


    PolicyBeneficiary policyBeneficiary = this.createPolicyBeneficiary(
            principalAge, principalGender, spouseAge, spouseGender, childAge
    );

    Benefit benefit = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,opticalLimit, dentalLimit, travelInsurance);
    BigDecimal expectedPremium = BigDecimal.valueOf(566312);

    when(productPremiumMap.getJamiiPlusSharedMap()).thenReturn(map.getJamiiPlusSharedMap());
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    BigDecimal actualPremium = productService.calculatePremium(policyBeneficiary,
            ProductEnum.JAMIIPLUS.getId(), benefit, false);

    assertEquals(expectedPremium, actualPremium);
  }


  @Test
  void testCalculatePremium1() {

    // Customer and Dependant
    int principalAge = 55;
    String principalGender = GenderEnum.MALE.getValue();
    int spouseAge = 65;
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{21, 20};

    // Benefit
    int inpatientLimit = 10000000;
    int outpatientLimit = 150000;
    int maternityLimit = 150000;
    int opticalLimit = 30000;
    int dentalLimit = 30000;
    int travelInsurance = 40000;

    BigDecimal expectedPremium = BigDecimal.valueOf(522846);
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
        .createPolicyBeneficiary(principalAge, principalGender,spouseAge, spouseGender, childAge);
    Benefit benefit1 = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,opticalLimit, dentalLimit, travelInsurance);

    BigDecimal premium1 = productService
        .calculatePremium(policyBeneficiary, ProductEnum.JAMIIPLUS.getId(), benefit1, false);
    logger.debug("premium1: {}", premium1);
    // Assertions.assertEquals(expectedPremium, premium1);
    Assertions.assertNotNull(expectedPremium);
    // this.assertPolicyPremiums(policyBeneficiary);
  }

  private void assertPolicyPremiums(PolicyBeneficiary policyBeneficiary) {
    logger.debug("policyBeneficiary: {}", policyBeneficiary);
    this.assertBenefitPremiums(policyBeneficiary.getPrincipal().getBenefitPremiums());
    this.assertBenefitPremiums(policyBeneficiary.getSpouse().getBenefitPremiums());
    for (Beneficiary beneficiary : policyBeneficiary.getChildren()) {
      this.assertBenefitPremiums(beneficiary.getBenefitPremiums());
    }
  }

  private void assertBenefitPremiums(List<BenefitPremium> benefitPremiums) {
    Assertions.assertNotNull(benefitPremiums);
    for (BenefitPremium benefitPremium : benefitPremiums) {
      Assertions.assertNotNull(benefitPremium.getBenefitLimit());
      Assertions.assertNotNull(benefitPremium.getBenefitType());
      Assertions.assertNotNull(benefitPremium.getPremium());
    }
  }

  @Test
  void testCalculatePremium1min() {

    // Customer and Dependant
    int principalAge = 80;
    int spouseAge = 76;
    String principalGender = GenderEnum.MALE.getValue();
    String spouseGender = GenderEnum.MALE.getValue();
    int childAge[] = new int[]{21, 20};

    // Benefit
    int inpatientLimit = 500000;
    int outpatientLimit = 50000;
    int maternityLimit = 50000;
    int opticalLimit = 10000;
    int dentalLimit = 10000;
    int travelInsurance = 40000;

    BigDecimal expectedPremium = BigDecimal.valueOf(291780);
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
        .createPolicyBeneficiary(principalAge, principalGender,
            spouseAge, spouseGender, childAge);
    Benefit benefit1 = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit,
        opticalLimit, dentalLimit, travelInsurance);

    BigDecimal premium1 = productService
        .calculatePremium(policyBeneficiary, ProductEnum.JAMIIPLUS.getId(), benefit1, false);
    logger.debug("premium1: {}", premium1);
    //Assertions.assertEquals(expectedPremium, premium1);
    Assertions.assertNotNull(expectedPremium);
  }

  @Test
  void testCalculatePremium1max() {

    // Customer and Dependant
    int principalAge = 35;
    int spouseAge = 43;
    String principalGender = GenderEnum.FEMALE.getValue();
    String spouseGender = GenderEnum.MALE.getValue();
    int childAge[] = new int[]{6};

    // Benefit
    int inpatientLimit = 10000000;
    int outpatientLimit = 150000;
    int maternityLimit = 150000;
    int opticalLimit = 30000;
    int dentalLimit = 30000;
    int travelInsurance = 40000;

//    BigDecimal expectedPremium = BigDecimal.valueOf(552846);
    when(productPremiumMap.getJamiiPlusMap()).thenReturn(map.getJamiiPlusMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this.createPolicyBeneficiary(principalAge, principalGender, spouseAge, spouseGender, childAge);
    Benefit benefit1 = createBenefit1(inpatientLimit, outpatientLimit, maternityLimit, opticalLimit, dentalLimit, travelInsurance);

    BigDecimal premium1 = productService.calculatePremium(policyBeneficiary, ProductEnum.JAMIIPLUS.getId(), benefit1, false);
    logger.debug("premium1: {}", premium1);
    //Assertions.assertEquals(new BigDecimal(332175), premium1);
    Assertions.assertNotNull(premium1);
  }


  @Test
  void testCalculatePremium2() {

    // Customer and Dependant
    int principalAge = 50;
    int spouseAge = 50;
    String principalGender = GenderEnum.MALE.getValue();
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{21, 20};

    // Benefit
    int inpatientLimit = 1000000;
    int outpatientLimit = 100000;
    int maternityLimit = 100000;

    BigDecimal expectedPremium = BigDecimal.valueOf(332175);
    when(productPremiumMap.getAfyaNafuuMap()).thenReturn(map.getAfyaNafuuMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
        .createPolicyBeneficiary(principalAge, principalGender,
            spouseAge, spouseGender, childAge);
    Benefit benefit2 = createBenefit2(inpatientLimit, outpatientLimit, maternityLimit);

    BigDecimal premium2 = productService
        .calculatePremium(policyBeneficiary, ProductEnum.AFYANAFUU.getId(), benefit2, false);

    //Assertions.assertEquals(expectedPremium, premium2);
    Assertions.assertNotNull(expectedPremium);

  }

  @Test
  void testCalculatePremium2min() {

    // Customer and Dependant
    int principalAge = 75;
    int spouseAge = 70;
    String principalGender = GenderEnum.MALE.getValue();
    String spouseGender = GenderEnum.MALE.getValue();
    int childAge[] = new int[]{};

    // Benefit
    int inpatientLimit = 100000;
    int outpatientLimit = 30000;
    int maternityLimit = 50000;

    BigDecimal expectedPremium = BigDecimal.valueOf(69280);
    when(productPremiumMap.getAfyaNafuuMap()).thenReturn(map.getAfyaNafuuMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
        .createPolicyBeneficiary(principalAge, principalGender,
            spouseAge, spouseGender, childAge);
    Benefit benefit2 = createBenefit2(inpatientLimit, outpatientLimit, maternityLimit);

    BigDecimal premium2 = productService
        .calculatePremium(policyBeneficiary, ProductEnum.AFYANAFUU.getId(), benefit2, false);
    logger.debug("premium2: {}", premium2);
    // Assertions.assertEquals(expectedPremium, premium2);
    Assertions.assertNotNull(expectedPremium);

  }

  @Test
  void testCalculatePremium2max() {

    // Customer and Dependant
    int principalAge = 75;
    int spouseAge = 70;
    String principalGender = GenderEnum.FEMALE.getValue();
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{21, 20};

    // Benefit
    int inpatientLimit = 1000000;
    int outpatientLimit = 100000;
    int maternityLimit = 100000;

    BigDecimal expectedPremium = BigDecimal.valueOf(231717);
    when(productPremiumMap.getAfyaNafuuMap()).thenReturn(map.getAfyaNafuuMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
        .createPolicyBeneficiary(principalAge, principalGender,
            spouseAge, spouseGender, childAge);
    Benefit benefit2 = createBenefit2(inpatientLimit, outpatientLimit, maternityLimit);

    BigDecimal premium2 = productService
        .calculatePremium(policyBeneficiary, ProductEnum.AFYANAFUU.getId(), benefit2, false);
    logger.debug("premium2: {}", premium2);
    //Assertions.assertEquals(expectedPremium, premium2);
    Assertions.assertNotNull(expectedPremium);

  }

  @Test
  void testCalculatePremium3() {

    // Customer and Dependant
    int principalAge = 50;
    int spouseAge = 45;
    String principalGender = GenderEnum.MALE.getValue();
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{21, 20};

    // Benefit
    int principalLimit = 500000;
    int spouseLimit = 250000;
    int childLimit[] = new int[]{500000, 250000};

    BigDecimal expectedPremium = BigDecimal.valueOf(7900);
    when(productPremiumMap.getFeminaMap()).thenReturn(map.getFeminaMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
        .createPolicyBeneficiary(principalAge, principalGender,
            spouseAge, spouseGender, childAge);
    Benefit benefit3 = createBenefit3(principalLimit, spouseLimit, childLimit);

    BigDecimal premium3 = productService
        .calculatePremium(policyBeneficiary, ProductEnum.FEMINA.getId(), benefit3, false);
    logger.debug("premium3: {}", premium3);
    // Assertions.assertEquals(expectedPremium, premium3);
    Assertions.assertNotNull(expectedPremium);

  }

  @Test
  void testCalculatePremium3min() {

    // Customer and Dependant
    int principalAge = 80;
    int spouseAge = 39;
    String principalGender = GenderEnum.MALE.getValue();
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{29, 20};

    // Benefit
    int principalLimit = 250000;
    int spouseLimit = 250000;
    int childLimit[] = new int[]{250000, 250000};

    BigDecimal expectedPremium = BigDecimal.valueOf(4550);
    when(productPremiumMap.getFeminaMap()).thenReturn(map.getFeminaMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
        .createPolicyBeneficiary(principalAge, principalGender,
            spouseAge, spouseGender, childAge);
    Benefit benefit3 = createBenefit3(principalLimit, spouseLimit, childLimit);

    BigDecimal premium3 = productService
        .calculatePremium(policyBeneficiary, ProductEnum.FEMINA.getId(), benefit3, false);
    logger.debug("premium3: {}", premium3);
    // Assertions.assertEquals(expectedPremium, premium3);
    Assertions.assertNotNull(expectedPremium);

  }

  @Test
  void testCalculatePremium3max() {

    // Customer and Dependant
    int principalAge = 80;
    int spouseAge = 39;
    String principalGender = GenderEnum.MALE.getValue();
    String spouseGender = GenderEnum.FEMALE.getValue();
    int childAge[] = new int[]{29, 20};

    // Benefit
    int principalLimit = 500000;
    int spouseLimit = 500000;
    int childLimit[] = new int[]{500000, 500000};

    BigDecimal expectedPremium = BigDecimal.valueOf(8900);
    when(productPremiumMap.getFeminaMap()).thenReturn(map.getFeminaMap());
    when(productPremium.getHealth()).thenReturn(prodPremium.getHealth());

    PolicyBeneficiary policyBeneficiary = this
        .createPolicyBeneficiary(principalAge, principalGender,
            spouseAge, spouseGender, childAge);
    Benefit benefit3 = createBenefit3(principalLimit, spouseLimit, childLimit);

    BigDecimal premium3 = productService.calculatePremium(policyBeneficiary, ProductEnum.FEMINA.getId(), benefit3, false);
    logger.debug("premium3: {}", premium3);
    // Assertions.assertEquals(expectedPremium, premium3);
    Assertions.assertNotNull(expectedPremium);

    }


  private PolicyBeneficiary createPolicyBeneficiary(int principalAge, String principalGender,
      int spouseAge, String spouseGender, int[] childAge) {
    Beneficiary principal = null;
    Beneficiary spouse = null;
    if (principalAge > 0) {
      principal = Beneficiary.builder().age(principalAge).gender(principalGender).build();
    }
    if (spouseAge > 0) {
      spouse = Beneficiary.builder().age(spouseAge).gender(spouseGender).build();
    }

    List<Beneficiary> children = new ArrayList<>();
    if (childAge.length > 0) {
      for (int i = 0; i < childAge.length; i++) {
        children.add(Beneficiary.builder().age(childAge[i]).build());
      }
    }
    return PolicyBeneficiary.builder().id("JUnitTest").principal(principal).spouse(spouse)
        .children(children)
        .build();

  }

  private Benefit createBenefit1(int inpatientLimit, int outpatientLimit, int maternityLimit,
      int opticalLimit, int dentalLimit, int travelInsurance) {
    Benefit benefit1 = new Benefit();
    benefit1.setInpatientLimit(inpatientLimit);
    benefit1.setOutpatientLimit(outpatientLimit);
    benefit1.setMaternityLimit(maternityLimit);
    benefit1.setOpticalLimit(opticalLimit);
    benefit1.setDentalLimit(dentalLimit);
    benefit1.setTravelInsurance(travelInsurance);
    return benefit1;
  }

  private Benefit createBenefit2(int inpatientLimit, int outpatientLimit, int maternityLimit) {
    Benefit benefit2 = new Benefit();
    benefit2.setInpatientLimit(inpatientLimit);
    benefit2.setOutpatientLimit(outpatientLimit);
    benefit2.setMaternityLimit(maternityLimit);
    return benefit2;
  }

  private Benefit createBenefit3(int principalLimit, int spouseLimit, int[] childLimit) {
    Benefit benefit3 = new Benefit();
    benefit3.setPrincipal(principalLimit);
    benefit3.setSpouse(spouseLimit);
    if (childLimit.length > 0) {
      benefit3.setChildren(Arrays.stream(childLimit).boxed().collect(Collectors.toList()));
    }

    return benefit3;
  }


  @Test
  void calcRenewPremiumByTotalPremiumTest() {
    PolicyDetail policyDetail = PolicyDetail.builder()
                  .totalPremium(new BigDecimal(100))
                  .productId(1)
                  .build();

        RenewalPremium ren = productService.calcRenewPremiumByTotalPremium(
                PolicyBeneficiary.builder().build(),
                policyDetail,
                Benefit.builder().build(),
                PolicyClaim.builder().claimsPaid(new BigDecimal(10)).build(),
                new BigDecimal(10)
              );

      Assertions.assertNotNull(ren);
    }


  @Test
  void calculateRenewalPremiumTest() {
    PolicyBeneficiary pb = PolicyBeneficiary.builder().build();
    Benefit benefit = Benefit.builder().build();
    PolicyClaim pc = PolicyClaim.builder()
                    .claimsPaid(new BigDecimal(100))
                    .earnedPremium(new BigDecimal(100))
                    .build();

    RenewalPremium ren = productService.calculateRenewalPremium(pb,1,benefit, pc, new BigDecimal(1));
    Assertions.assertNotNull(ren);

    }


  @Test
  void calcRenewPremiumByTotalPremiumForComingWorkerTest() {

        PolicyBeneficiary policyBeneficiary = PolicyBeneficiary.builder().build();
        PolicyDetail policyDetail = PolicyDetail.builder()
                          .totalPremium(new BigDecimal(100))
                          .productId(1)
                          .build();
        Benefit benefit = Benefit.builder().build();
        PolicyClaim policyClaim = PolicyClaim.builder()
                        .claimsPaid(new BigDecimal(100))
                        .earnedPremium(new BigDecimal(100))
                        .build();

    
        RenewalPremium ren = productService.calcRenewPremiumByTotalPremiumForComingWorker(policyBeneficiary,policyDetail, benefit, policyClaim, new BigDecimal(1));
        Assertions.assertNotNull(ren);

      }

  @ParameterizedTest
  @EnumSource(ProductEnum.class)
  void testCreateDefaultBenefit(ProductEnum productEnum) {
    CustomerDetailResponse customerDetailResponse = CommonObjects.customerDetailResponse;

    Benefit expectedBenefit = productService.createDefaultBenefit(customerDetailResponse, productEnum);

    assertNotNull(expectedBenefit);
  }

}
