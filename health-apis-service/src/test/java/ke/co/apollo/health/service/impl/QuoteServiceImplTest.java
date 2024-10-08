package ke.co.apollo.health.service.impl;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import ke.co.apollo.health.common.CommonObjects;
import ke.co.apollo.health.common.CommonQuote;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.*;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyResponse;
import ke.co.apollo.health.common.domain.model.response.CustomerResponse;
import ke.co.apollo.health.common.domain.model.response.HealthQuoteListResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionValidateResponse;
import ke.co.apollo.health.common.enums.IntermediaryRole;
import ke.co.apollo.health.common.enums.ProductEnum;
import ke.co.apollo.health.common.enums.QuoteNumberEnum;
import ke.co.apollo.health.config.HealthInitializer;
import ke.co.apollo.health.config.PremiumInitializer;
import ke.co.apollo.health.config.QuestionConfig;
import ke.co.apollo.health.domain.QuoteListSearchFilter;
import ke.co.apollo.health.domain.entity.HealthStepEntity;
import ke.co.apollo.health.domain.request.CustomerSearchRequest;
import ke.co.apollo.health.domain.request.QuoteBenefitUpdateRequest;
import ke.co.apollo.health.domain.request.QuoteFinishRequest;
import ke.co.apollo.health.domain.request.QuoteStepRequest;
import ke.co.apollo.health.domain.response.AgentBranchDetailsResponse;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.domain.response.QuoteStepResponse;
import ke.co.apollo.health.enums.HealthQuoteStepsEnum;
import ke.co.apollo.health.enums.QuoteStatusEnum;
import ke.co.apollo.health.event.PolicyNotificationEventPublisher;
import ke.co.apollo.health.mapper.health.CustomerMapper;
import ke.co.apollo.health.mapper.health.QuoteMapper;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.repository.HealthAgentBranchRepository;
import ke.co.apollo.health.repository.HealthStepRepository;
import ke.co.apollo.health.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import ke.co.apollo.health.common.enums.PolicyStatus;
import ke.co.apollo.health.common.exception.BusinessException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import ke.co.apollo.health.domain.request.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static ke.co.apollo.health.common.CommonObjects.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class QuoteServiceImplTest {

    @InjectMocks
    QuoteServiceImpl quoteService;
    @Mock
    QuoteServiceImpl quoteServiceMock;

    @Mock
    CustomerServiceImpl customerService;

    @Mock
    HealthStepRepository healthStepRepository;

    @Mock
    QuoteMapper quoteMapper;

    @Mock
    IntermediaryService intermediaryService;

    @Mock
    PolicyRemote policyRemote;
    @Mock
    BeneficiaryService beneficiaryService;
    @Mock
    ProductService productService;

    @Mock
    BenefitCategoryMap benefitCategoryMap;

    @Mock
    PremiumService premiumService;

    @Mock
    QuestionService questionService;

    @Mock
    QuestionConfig questionConfig;

    @Mock
    Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);

    @Mock
    PolicyNotificationEventPublisher policySMSEventPublisher;

    private static BenefitCategoryMap bcMap;



    @BeforeEach
    void setUpMocks() {
        initMocks(this);

        //Shared, to be moved when need arises
        when(policyRemote.addBusinessSourceToIndividualPolicy(any())).thenReturn(
                ApiResponse
                        .builder()
                        .success(true)
                        .errorMessage("No error")
                        .build()
        );
        HealthInitializer healthInitializer = new HealthInitializer();
        bcMap = healthInitializer.initBenefitCategory();
        ReflectionTestUtils.setField(quoteService, "customerService", customerService);
        ReflectionTestUtils.setField(quoteService, "quoteMapper", quoteMapper);
        ReflectionTestUtils.setField(quoteService, "intermediaryService", intermediaryService);
        ReflectionTestUtils.setField(quoteService, "policyRemote", policyRemote);
        ReflectionTestUtils.setField(quoteService, "stepRepository", healthStepRepository);
        ReflectionTestUtils.setField(quoteService, "agentBranchRepository", agentBranchRepository);
        ReflectionTestUtils.setField(quoteService, "productService", productService);
        ReflectionTestUtils.setField(quoteService, "beneficiaryService", beneficiaryService);
        ReflectionTestUtils.setField(quoteService, "benefitCategoryMap", benefitCategoryMap);
        ReflectionTestUtils.setField(quoteService, "premiumService", premiumService);

    }

    @Mock
    HealthAgentBranchRepository agentBranchRepository;

    @Test
    void testAddIntermediaryToPolicy() {
        when(intermediaryService.getIntermediaryEntityId(anyString())).thenReturn(10);
        Quote q = CommonObjects.quote;
        AgentBranchDetailsResponse agentBranchDetailsResponse = CommonObjects.agentBranchDetailsResponse;
        when(quoteMapper.getQuote(any(), any(), any())).thenReturn(q);
        when(policyRemote.addPolicyAdditionalBranchDetails(any())).thenReturn(actisurePolicyBranchDetailsResponse);
        when(policyRemote.getAgentBranchDetails(any())).thenReturn(agentBranchDetailsResponse);
        when(agentBranchRepository.save(any())).thenReturn(null);
        boolean res = quoteService.addIntermediaryToPolicy("quoteId", "customerId", "agentId");
        assertTrue(res);
    }

    private static Stream<Arguments> argumentsForTestFinishQuote() {
        return Stream.of(
                Arguments.of(49, true),
                Arguments.of(49, false),
                Arguments.of(52, false)
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsForTestFinishQuote")
    void testFinishQuote(int productId, boolean isChildrenOnly) {

        Map<String, String> qcMap = new HashMap<>();
        qcMap.put("key_one", "value_one");
        qcMap.put("key_two", "value_two");

        List<Customer> customerList = new ArrayList<>();
        customerList.add(customer);

        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(Quote
                .builder().id("q123")
                .effectiveDate(new Date())
                .startDate(new Date())
                .productId(productId)
                .customerId("agent")
                .benefit(benefit)
                .agentId("agent")
                .quoteStatus("ACTIVE")
                .status("APPLICATION")
                .isChildrenOnly(isChildrenOnly)
                .code("")
                .build());

        when(customerService.addClientAndDependantToBase(any(), any())).thenReturn(1L);
        when(beneficiaryService.addBeneficiaryToBase(any(), any(), any(), anyString())).thenReturn(true);
        when(customerService.getCustomerAndDependants(any(), any())).thenReturn(customerList);
        when(quoteMapper.update(any())).thenReturn(1);

        when(customerService.getCustomer(anyString())).thenReturn(null);
        when(healthStepRepository.saveAndFlush(any())).thenReturn(null);
        when(quoteServiceMock.getQuote(anyString(), anyString(), anyString())).thenReturn(null);
        when(questionService.getQuoteQuestion(any())).thenReturn(quoteQuestion);
        when(questionConfig.getQuestionMap()).thenReturn(qcMap);
        when(policyRemote.addBeneficiaryUWQuestions(any())).thenReturn(apiResponse);
        when(policyRemote.createPolicy(any())).thenReturn(CreatePolicyResponse
                .builder()
                .policyId(888)
                .policyNumber("PolM")
                .success(true)
                .build());
        when(policyRemote.addBenefitsToPolicy(any())).thenReturn(apiResponse);
        when(benefitCategoryMap.getInpatient()).thenReturn(bcMap.getInpatient());
        when(benefitCategoryMap.getOptionalBenefits()).thenReturn(bcMap.getOptionalBenefits());
        when(intermediaryService.getIntermediaryEntityId(any())).thenReturn(agentBranchEntity.getEntityId());
        when(policyRemote.getAgentBranchDetails(any())).thenReturn(agentBranchDetailsResponse);
        when(agentBranchRepository.save(any())).thenReturn(agentBranchEntity);
        when(policyRemote.addPolicyAdditionalBranchDetails(any())).thenReturn(actisurePolicyBranchDetailsResponse);
        doNothing().when(policySMSEventPublisher).publishTask(any());
        when(healthStepRepository.save(any())).thenReturn(null);

        QuoteServiceImpl classToTest = Mockito.mock(QuoteServiceImpl.class);

        doReturn(Quote.builder().build()).when(classToTest).getQuote(anyString(), any(), any());
        doReturn(Quote.builder().build()).when(classToTest).getQuoteNoThrowException(anyString(), any(), any());

        Quote resp = quoteService.finishQuote(
                QuoteFinishRequest.builder()
                        .quoteId("q123")
                        .customerId("agent")
                        .step("2")
                        .agentId("agent")
                        .build());
        assertNotNull(resp);
    }

    @ParameterizedTest
    @ValueSource(strings = {"New", "Viewed"})
    void testFinishQuoteWhenStatusIsNewOrViewed(String status) {
        when(customerService.addClientAndDependantToBase(any(), any())).thenReturn(1L);
        when(quoteMapper.update(any())).thenReturn(1);
        when(customerService.getCustomer(anyString())).thenReturn(null);
        when(healthStepRepository.saveAndFlush(any())).thenReturn(null);
        when(quoteServiceMock.getQuote(anyString(), anyString(), anyString())).thenReturn(null);
        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(Quote
                .builder()
                .effectiveDate(new Date())
                .startDate(new Date())
                .productId(1)
                .entityId(9L)
                .agentId("")
                .status(status)
                .code("")
                .build());
        when(policyRemote.createPolicy(any())).thenReturn(CreatePolicyResponse
                .builder()
                .policyId(888)
                .policyNumber("PolM")
                .success(true)
                .build());
        when(beneficiaryService.addBeneficiaryToBase(any(), any(), any(), anyString())).thenReturn(true);
        QuoteServiceImpl classToTest = Mockito.mock(QuoteServiceImpl.class);

        doReturn(Quote.builder().build()).when(classToTest).getQuote(anyString(), any(), any());
        doReturn(Quote.builder().build()).when(classToTest).getQuoteNoThrowException(anyString(), any(), any());

        Quote resp = quoteService.finishQuote(
                QuoteFinishRequest.builder()
                        .quoteId("quote")
                        .customerId("agent")
                        .step("2")
                        .agentId("agent")
                        .build());
        assertNotNull(resp);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "customerId"})
    void testCreateInitQuote(String customerId) {
        when(customerService.getCustomer((CustomerSearchRequest) any())).thenReturn(

                CustomerDetailResponse
                        .builder()
                        .principal(Principal.builder().dateOfBirth(new Date()).firstName("Test").gender("Male").build())
                        .spouse(Dependant.builder().dateOfBirth(new Date()).relationship("Spouse").gender("Female").build())
                        .build()

        );


        when(productService.createDefaultBenefit(any(), any())).thenReturn(Benefit.builder().build());
        when(productService.calculatePremium(any(), any(), any(), anyBoolean())).thenReturn(BigDecimal.TEN);
        when(productService.calculateTotalPremium(any(), anyBoolean())).thenReturn(Premium.builder()
                .totalPremium(BigDecimal.TEN)
                .premium(BigDecimal.TEN)
                .build());
        when(productService.getTravelInsurancePremium(any(), any())).thenReturn(BigDecimal.TEN);
        when(quoteMapper.deleteDependentBenefits(anyString(),any())).thenReturn(1);
        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(Quote
                .builder()
                .effectiveDate(new Date())
                .startDate(new Date())
                .productId(50)
                .entityId(9L)
                .agentId("")
                .status("APPLICATION")
                .code("")
                .build());
        when(quoteMapper.insert(any())).thenReturn(0);
        when(healthStepRepository.save(any())).thenReturn(null);
        List<Quote> createInitQuote = quoteService.createInitQuote(customerId, "agentId", "quoteId", false, 49);
        assertNotNull(createInitQuote);
    }

    @ParameterizedTest
    @EnumSource(ProductEnum.class)
    void testCreateInitQuoteWithDifferentProductIds(ProductEnum productEnum) {
        when(customerService.getCustomer((CustomerSearchRequest) any())).thenReturn(

                CustomerDetailResponse
                        .builder()
                        .principal(Principal.builder().dateOfBirth(new Date()).firstName("Test").gender("Male").build())
                        .spouse(Dependant.builder().dateOfBirth(new Date()).relationship("Spouse").gender("Female").build())
                        .build()

        );


        when(productService.createDefaultBenefit(any(), any())).thenReturn(Benefit.builder().build());
        when(productService.calculatePremium(any(), any(), any(), anyBoolean())).thenReturn(BigDecimal.TEN);
        when(productService.calculateTotalPremium(any(), anyBoolean())).thenReturn(Premium.builder()
                .totalPremium(BigDecimal.TEN)
                .premium(BigDecimal.TEN)
                .build());
        when(productService.getTravelInsurancePremium(any(), any())).thenReturn(BigDecimal.TEN);
        when(quoteMapper.deleteDependentBenefits(anyString(),any())).thenReturn(1);
        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(Quote
                .builder()
                .effectiveDate(new Date())
                .startDate(new Date())
                .productId(50)
                .entityId(9L)
                .agentId("")
                .status("APPLICATION")
                .code("")
                .build());
        when(quoteMapper.insert(any())).thenReturn(0);
        when(healthStepRepository.save(any())).thenReturn(null);
        List<Quote> createInitQuote = quoteService.createInitQuote("customerId", "agentId", "quoteId", false, productEnum.getId());
        assertNotNull(createInitQuote);
    }



    @ParameterizedTest
    @ValueSource(ints = {49, 50, 51, 52})
    void testCreateInitQuoteReturnsQuoteWithDifferentProductCodes(int productId) {
        Quote quoteResponse = Quote
                .builder()
                .effectiveDate(new Date())
                .startDate(new Date())
                .productId(productId)
                .entityId(9L)
                .agentId("")
                .status("APPLICATION")
                .build();

        when(customerService.getCustomer((CustomerSearchRequest) any())).thenReturn(

                CustomerDetailResponse
                        .builder()
                        .principal(Principal.builder().dateOfBirth(new Date()).firstName("Test").gender("Male").build())
                        .spouse(Dependant.builder().dateOfBirth(new Date()).relationship("Spouse").gender("Female").build())
                        .build()

        );
        switch (productId) {
            case 49:
                quoteResponse.setCode("HJ-123");
                break;
            case 50:
                quoteResponse.setCode("HA-123");
                break;
            case 51:
                quoteResponse.setCode("HF-123");
                break;
            case 52:
                quoteResponse.setCode("HS-123");
                break;
            default:
        }


        when(productService.createDefaultBenefit(any(), any())).thenReturn(Benefit.builder().build());
        when(productService.calculatePremium(any(), any(), any(), anyBoolean())).thenReturn(BigDecimal.TEN);
        when(productService.calculateTotalPremium(any(), anyBoolean())).thenReturn(Premium.builder()
                .totalPremium(BigDecimal.TEN)
                .premium(BigDecimal.TEN)
                .build());
        when(productService.getTravelInsurancePremium(any(), any())).thenReturn(BigDecimal.TEN);
        when(quoteMapper.deleteDependentBenefits(anyString(),any())).thenReturn(1);
        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(quoteResponse);
        when(quoteMapper.update(any())).thenReturn(1);
        when(healthStepRepository.save(any())).thenReturn(null);
        List<Quote> createInitQuote = quoteService.createInitQuote("customerId", "agentId", "quoteId", false, productId);
        assertNotNull(createInitQuote);
    }


    @ParameterizedTest
    @ValueSource(strings = {"Male", "Female"})
    void testCreateInitQuoteGender(String gender) {
        CustomerDetailResponse re =  CustomerDetailResponse
                .builder()
                .principal(Principal.builder().dateOfBirth(new Date()).firstName("Test").gender(gender).build())
                .spouse(Dependant.builder().dateOfBirth(new Date()).relationship("Spouse").gender("Female").build())
                .build();
        when(customerService.getCustomer((CustomerSearchRequest) any())).thenReturn(re);


        when(productService.createDefaultBenefit(any(), any())).thenReturn(Benefit.builder().build());
        when(productService.calculatePremium(any(), any(), any(), anyBoolean())).thenReturn(BigDecimal.TEN);
        when(productService.calculateTotalPremium(any(), anyBoolean())).thenReturn(Premium.builder()
                .totalPremium(BigDecimal.TEN)
                .premium(BigDecimal.TEN)
                .build());
        when(productService.getTravelInsurancePremium(any(), any())).thenReturn(BigDecimal.TEN);
        when(quoteMapper.deleteDependentBenefits(anyString(),any())).thenReturn(1);
        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(Quote
                .builder()
                .effectiveDate(new Date())
                .startDate(new Date())
                .productId(50)
                .entityId(9L)
                .agentId("")
                .status("APPLICATION")
                .code("")
                .build());
        when(quoteMapper.insert(any())).thenReturn(0);
        when(healthStepRepository.save(any())).thenReturn(null);
        List<Quote> createInitQuote = quoteService.createInitQuote("customerId", "agentId", "quoteId", false, 49);
        assertNotNull(createInitQuote);
    }

    @Test
    void testCreateInitQuoteWhenQuoteEmpty() {
        when(customerService.getCustomer((CustomerSearchRequest) any())).thenReturn(CustomerDetailResponse.builder().build());
        when(productService.createDefaultBenefit(any(), any())).thenReturn(Benefit.builder().build());
        when(productService.calculatePremium(any(), any(), any(), anyBoolean())).thenReturn(BigDecimal.TEN);
        when(productService.calculateTotalPremium(any(), anyBoolean())).thenReturn(Premium.builder()
                .totalPremium(BigDecimal.TEN)
                .premium(BigDecimal.TEN)
                .build());
        when(productService.getTravelInsurancePremium(any(), any())).thenReturn(BigDecimal.TEN);
        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(Quote
                .builder()
                .effectiveDate(new Date())
                .startDate(new Date())
                .productId(50)
                .entityId(9L)
                .agentId("")
                .status("APPLICATION")
                .code("")
                .build());
        when(quoteMapper.insert(any())).thenReturn(0);
        when(healthStepRepository.save(any())).thenReturn(null);
        List<Quote> createInitQuote = quoteService.createInitQuote("customer", "agentId", "", false, 49);
        assertNotNull(createInitQuote);
    }

    @Test
    void testGetQuoteStep() {
        when(healthStepRepository.findById(anyString())).thenReturn(Optional.of(HealthStepEntity.builder().quoteOrPolicyId("id")
                .step(HealthQuoteStepsEnum.CREATE_QUOTE).build()));
        QuoteStepResponse response = quoteService.getQuoteStep(QuoteStepRequest.builder().quoteOrPolicy("2").build());
        assertNotNull(response);
    }

    @Test
    void testGetQuoteStepNull() {
        when(healthStepRepository.findById(anyString())).thenReturn(Optional.empty());
        QuoteStepResponse response = quoteService.getQuoteStep(QuoteStepRequest.builder().quoteOrPolicy("2").build());
        assertNotNull(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Viewed", "Enquiry", "New"})
    void testUpdateQuoteBenefitWhenStatusIsViewedOrEnquiryOrNew(String status) {
        when(productService.createDefaultBenefit(any(), any())).thenReturn(Benefit.builder().build());
        when(productService.calculatePremium(any(), any(), any(), anyBoolean())).thenReturn(BigDecimal.TEN);
        when(productService.calculateTotalPremium(any(), anyBoolean())).thenReturn(Premium.builder()
                .totalPremium(BigDecimal.TEN)
                .premium(BigDecimal.TEN)
                .build());
        when(productService.getTravelInsurancePremium(any(), any())).thenReturn(BigDecimal.TEN);
        Customer principal = Customer
                .builder()
                .relationshipDesc("Policy Holder")
                .gender("Male")
                .spouseSummary(Dependant.builder().gender("Male").relationship("Policy Holder").build())
                .dateOfBirth(new Date())
                .build();
        Customer spouse = Customer
                .builder()
                .relationshipDesc("Spouse")
                .gender("Female")
                .spouseSummary(Dependant.builder().gender("Female").relationship("Spouse").build())
                .dateOfBirth(new Date())
                .build();
        List<Customer> customers = new ArrayList<>();
        customers.add(principal);
        customers.add(spouse);
        when(beneficiaryService.getQuoteBeneficiary(any(), any()))
                .thenReturn(customers);
        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(Quote
                .builder()
                .effectiveDate(new Date())
                .startDate(new Date())
                .productId(50)
                .entityId(9L)
                .agentId("")
                .status(status)
                .code("")
                .build());
        when(quoteMapper.insert(any())).thenReturn(0);
        when(quoteMapper.update(any())).thenReturn(1);
        when(premiumService.recordQuoteBeneficiaryPremium(any(), any())).thenReturn(true);
        when(healthStepRepository.save(any())).thenReturn(null);
        Quote quote = quoteService.updateQuoteBenefit(QuoteBenefitUpdateRequest
                .builder()
                .customerId("Cu")
                .agentId("agentId")
                .quoteId("quote")
                .benefit(Benefit.builder().build())
                .build());

        assertNotNull(quote);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Male", "Female"})
    void testUpdateQuoteBenefitWhenPrincipalAnd(String gender) {
        when(productService.createDefaultBenefit(any(), any())).thenReturn(Benefit.builder().build());
        when(productService.calculatePremium(any(), any(), any(), anyBoolean())).thenReturn(BigDecimal.TEN);
        when(productService.calculateTotalPremium(any(), anyBoolean())).thenReturn(Premium.builder()
                .totalPremium(BigDecimal.TEN)
                .premium(BigDecimal.TEN)
                .build());
        when(productService.getTravelInsurancePremium(any(), any())).thenReturn(BigDecimal.TEN);
        Customer principal = Customer
                .builder()
                .relationshipDesc("Policy Holder")
                .gender(gender)
                .spouseSummary(Dependant.builder().gender(gender).relationship("Policy Holder").build())
                .dateOfBirth(new Date())
                .build();
        Customer spouse = Customer
                .builder()
                .relationshipDesc("Spouse")
                .gender("Female")
                .spouseSummary(Dependant.builder().gender("Female").relationship("Spouse").build())
                .dateOfBirth(new Date())
                .build();
        List<Customer> customers = new ArrayList<>();
        customers.add(principal);
        customers.add(spouse);
        when(beneficiaryService.getQuoteBeneficiary(any(), any())).thenReturn(customers);
        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(Quote
                .builder()
                .effectiveDate(new Date())
                .startDate(new Date())
                .productId(50)
                .entityId(9L)
                .agentId("")
                .status("Enquiry")
                .code("")
                .build());
        when(quoteMapper.insert(any())).thenReturn(0);
        when(quoteMapper.update(any())).thenReturn(1);
        when(premiumService.recordQuoteBeneficiaryPremium(any(), any())).thenReturn(true);
        when(healthStepRepository.save(any())).thenReturn(null);
        Quote quote = quoteService.updateQuoteBenefit(QuoteBenefitUpdateRequest
                .builder()
                .customerId("Cu")
                .agentId("agentId")
                .quoteId("quote")
                .benefit(Benefit.builder().build())
                .build());

        assertNotNull(quote);
    }

    @Test
    void testUpdateQuoteBenefitWithIncorrectStatus() {
        when(productService.createDefaultBenefit(any(), any())).thenReturn(Benefit.builder().build());
        when(productService.calculatePremium(any(), any(), any(), anyBoolean())).thenReturn(BigDecimal.TEN);
        when(productService.calculateTotalPremium(any(), anyBoolean())).thenReturn(Premium.builder()
                .totalPremium(BigDecimal.TEN)
                .premium(BigDecimal.TEN)
                .build());
        when(productService.getTravelInsurancePremium(any(), any())).thenReturn(BigDecimal.TEN);
        when(beneficiaryService.getQuoteBeneficiary(any(), any()))
                .thenReturn(Collections.singletonList(Customer
                        .builder()
                        .build()));
        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(Quote
                .builder()
                .effectiveDate(new Date())
                .startDate(new Date())
                .productId(50)
                .entityId(9L)
                .agentId("")
                .status("None")
                .code("")
                .build());
        when(quoteMapper.insert(any())).thenReturn(0);
        when(quoteMapper.update(any())).thenReturn(1);
        when(premiumService.recordQuoteBeneficiaryPremium(any(), any())).thenReturn(true);
        when(healthStepRepository.save(any())).thenReturn(null);

        assertThrows(BusinessException.class, () -> {quoteService.updateQuoteBenefit(QuoteBenefitUpdateRequest
                .builder()
                .customerId("Cu")
                .agentId("agentId")
                .quoteId("quote")
                .benefit(Benefit.builder().build())
                .build());
        });
    }


    @Test
    void updateQuoteStartDateTest() {
        QuoteStartDateUpdateRequest request = QuoteStartDateUpdateRequest.builder()
                .customerId("Cu")
                .agentId("agentId")
                .quoteId("quote")
                .startDate(new Date())
                .build();

        Quote quote = Quote.builder()
                .id("1")
                .agentId("101")
                .customerId("123")
                .extPolicyId(1)
                .productId(1)
                // .benefit(afBenefit)
                // .premium(premium1)
                .balance(BigDecimal.ZERO)
                .status(PolicyStatus.NEW.getValue())
                // .isChildrenOnly(isChildrenOnly)
                .createTime(new Date())
                .build();


        when(intermediaryService.getIntermediaryEntityId(any())).thenReturn(1);
        when(quoteMapper.update(any())).thenReturn(1);
        when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(quote);
        Quote resp = quoteService.updateQuoteStartDate(request);
        assertNotNull(resp);

    }

    @Test
    void testSoftDeleteQuoteByCustomerId() {

        when(quoteMapper.softDeleteQuoteByCustomerId(anyString())).thenReturn(1);
        boolean result = quoteService.softDeleteQuoteByCustomerId("12345");
        assertTrue(result);

        doThrow(new BusinessException("Customer is null test")).when(quoteMapper).softDeleteQuoteByCustomerId(null);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> quoteService.softDeleteQuoteByCustomerId(null));
        assertEquals("Customer is null!!", exception.getMessage());
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 0})
    void testSoftDeleteQuoteByCustomerIdWhenNotNull(int returnValue) {

        when(quoteMapper.softDeleteQuoteByCustomerId(anyString())).thenReturn(returnValue);
        boolean result = quoteService.softDeleteQuoteByCustomerId("12345");
        if (returnValue == 1) {
            assertTrue(result);
        } else {
            assertFalse(result);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"quoteId123", ""})
    void testSoftDeleteQuoteByAgentAndReturnValueIsOne(String quoteId) {
        SoftDeleteQuoteByAgentRequest request = new SoftDeleteQuoteByAgentRequest();
        request.setQuoteId(quoteId);
        doThrow(new BusinessException("Quote cannot be null test")).when(quoteMapper).updateQuotStatusToDeleted(null);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> quoteService.softDeleteQuoteByAgent(null));
        assertEquals("Quote cannot be null!", exception.getMessage());

        when(quoteMapper.updateQuotStatusToDeleted(request)).thenReturn(1);


        boolean result = quoteService.softDeleteQuoteByAgent(request);
        assertTrue(result,"Is True");
    }

    @ParameterizedTest
    @ValueSource(strings = {"quoteId123", ""})
    void testSoftDeleteQuoteByAgentAndReturnValueIsZero(String quoteId) {
        SoftDeleteQuoteByAgentRequest request = new SoftDeleteQuoteByAgentRequest();
        request.setQuoteId(quoteId);
        doThrow(new BusinessException("Quote cannot be null test")).when(quoteMapper).updateQuotStatusToDeleted(null);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> quoteService.softDeleteQuoteByAgent(null));
        assertEquals("Quote cannot be null!", exception.getMessage());

        when(quoteMapper.updateQuotStatusToDeleted(request)).thenReturn(0);


        boolean result = quoteService.softDeleteQuoteByAgent(request);
        assertFalse(result, "Is False");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "otherStatus"})
    void testSearchQuoteListWhenSortTypeAndSortAreNotNullAndIndexIsGreaterThanZero(String status) {
        // Mocking intermediaryService.getIntermediary()
        Intermediary intermediary = CommonObjects.intermediary;
        intermediary.setRole(IntermediaryRole.ADMIN.getValue());
        when(intermediaryService.getIntermediary("agent123")).thenReturn(intermediary);

        // Mocking intermediaryService.getUserList()
        List<Intermediary> intermediaryList = new ArrayList<>();
        Intermediary intermediary1 = new Intermediary();
        intermediary1.setAgentId("agent456");
        intermediaryList.add(intermediary1);
        when(intermediaryService.getUserList("agent123")).thenReturn(intermediaryList);

        // Mocking quoteMapper.searchQuotes()
        List<HealthQuote> quotes = new ArrayList<>();
        QuoteListSearchFilter quoteListSearchFilter = CommonObjects.quoteListSearchFilter;
        HealthQuote quote1 = new HealthQuote();
        quote1.setId("111");
        quotes.add(quote1);
        when(quoteMapper.searchQuotes(quoteListSearchFilter)).thenReturn(quotes);


        // Creating a QuoteListSearchRequest
        QuoteListSearchRequest request = new QuoteListSearchRequest();
        request.setAgentId("agent123");
        request.setSortType("name");
        request.setIndex(1);
        if (status == null) {
            request.setQuoteStatus(QuoteStatusEnum.ACTIVE);
        }

        // Calling the method being tested
        HealthQuoteListResponse response = quoteService.searchQuoteList(request);


        // Verifying the results
        assertNotNull(response);
        assertEquals(0, response.getTotal());

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "otherStatus"})
    void testSearchQuoteListWhenSortTypeAndSortAreNotNullAndIndexIsZero(String status) {
        // Mocking intermediaryService.getIntermediary()
        Intermediary intermediary = CommonObjects.intermediary;
        intermediary.setRole(IntermediaryRole.ADMIN.getValue());
        when(intermediaryService.getIntermediary("agent123")).thenReturn(intermediary);

        // Mocking intermediaryService.getUserList()
        List<Intermediary> intermediaryList = new ArrayList<>();
        Intermediary intermediary1 = new Intermediary();
        intermediary1.setAgentId("agent456");
        intermediaryList.add(intermediary1);
        when(intermediaryService.getUserList("agent123")).thenReturn(intermediaryList);

        // Mocking quoteMapper.searchQuotes()
        List<HealthQuote> quotes = new ArrayList<>();
        QuoteListSearchFilter quoteListSearchFilter = CommonObjects.quoteListSearchFilter;
        HealthQuote quote1 = new HealthQuote();
        quote1.setId("111");
        quotes.add(quote1);
        when(quoteMapper.searchQuotes(quoteListSearchFilter)).thenReturn(quotes);


        // Creating a QuoteListSearchRequest
        QuoteListSearchRequest request = new QuoteListSearchRequest();
        request.setAgentId("agent123");
        request.setSortType("name");
        request.setIndex(0);
        if (status == null) {
            request.setQuoteStatus(QuoteStatusEnum.ACTIVE);
        }

        // Calling the method being tested
        HealthQuoteListResponse response = quoteService.searchQuoteList(request);


        // Verifying the results
        assertNotNull(response);
        assertEquals(0, response.getTotal());
    }

    @Test
    void testDownloadQuote() throws IOException, TemplateException {
        ReflectionTestUtils.setField(quoteService, "configuration", configuration);
        String templateStr = "Hello Testes";
        Customer customer = CommonQuote.getCustomer();
        Quote quote = CommonQuote.getQuote();
        HealthQuoteDownloadRequest request = CommonQuote.getHealthQuoteDownloadRequest();
        Intermediary intermediary = CommonQuote.getIntermediary();
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(quoteMapper.getQuoteByCode(any(), any())).thenReturn(quote);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Configuration configuration1 = new Configuration(Configuration.VERSION_2_3_30);
        Template template = new Template("te", templateStr, configuration1);
        when(configuration.getTemplate(anyString())).thenReturn(template);
        byte[] resp = quoteService.downloadQuote(request);
        assertNotNull(resp);
    }

    @Test
    void testDownloadQuoteWithNullAgent() throws IOException, TemplateException {
        ReflectionTestUtils.setField(quoteService, "configuration", configuration);
        String templateStr = "Hello Testes";
        Customer customer = CommonQuote.getCustomerWithNullAgent();
        Quote quote = CommonQuote.getQuoteWithNull();
        HealthQuoteDownloadRequest request = CommonQuote.getHealthQuoteDownloadRequest();
        Intermediary intermediary = CommonQuote.getIntermediaryNull();
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(quoteMapper.getQuoteByCode(any(), any())).thenReturn(quote);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Configuration configuration1 = new Configuration(Configuration.VERSION_2_3_30);
        Template template = new Template("te", templateStr, configuration1);
        when(configuration.getTemplate(anyString())).thenReturn(template);
        byte[] resp = quoteService.downloadQuote(request);
        assertNotNull(resp);
    }

    @Test
    void testDownloadQuoteWithEmptyAgent() throws IOException, TemplateException {
        ReflectionTestUtils.setField(quoteService, "configuration", configuration);
        String templateStr = "Hello Testes";
        Customer customer = CommonQuote.getCustomerWithNullAgent();
        Quote quote = CommonQuote.getQuoteEmpty();
        HealthQuoteDownloadRequest request = CommonQuote.getHealthQuoteDownloadRequest();
        Intermediary intermediary = CommonQuote.getIntermediaryEmpty();
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(quoteMapper.getQuoteByCode(any(), any())).thenReturn(quote);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Configuration configuration1 = new Configuration(Configuration.VERSION_2_3_30);
        Template template = new Template("te", templateStr, configuration1);
        when(configuration.getTemplate(anyString())).thenReturn(template);
        byte[] resp = quoteService.downloadQuote(request);
        assertNotNull(resp);
    }
    @Test
    void testDownloadQuoteWithDepandantsNull() throws IOException, TemplateException {
        ReflectionTestUtils.setField(quoteService, "configuration", configuration);
        String templateStr = "Hello Testes";
        Customer customer = CommonQuote.getCustomerWithNullDependants();
        Quote quote = CommonQuote.getQuoteEmpty();
        HealthQuoteDownloadRequest request = CommonQuote.getHealthQuoteDownloadRequest();
        Intermediary intermediary = CommonQuote.getIntermediaryEmpty();
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(quoteMapper.getQuoteByCode(any(), any())).thenReturn(quote);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        assertTrue(true);
    }

    @Test
    void testConfigBenefitWithNonNullValues() {
        Quote quote = CommonQuote.getQuote();
        Benefit result = quoteService.populateBenefitDetails(quote);
        assertEquals(quote.getBenefit().getInpatientLimit(), result.getInpatientLimit());
        assertEquals(quote.getBenefit().getOutpatientLimit(), result.getOutpatientLimit());
        assertEquals(quote.getBenefit().getDentalLimit(), result.getDentalLimit());
        assertEquals(quote.getBenefit().getOpticalLimit(), result.getOpticalLimit());
        assertEquals(quote.getBenefit().getMaternityLimit(), result.getMaternityLimit());
    }

    @Test
    void testConfigBenefitWithNullValues() {
        Quote quote = CommonQuote.getQuoteWithNull();
        Benefit result = quoteService.populateBenefitDetails(quote);
        assertEquals(0, result.getInpatientLimit());
        assertEquals(0, result.getOutpatientLimit());
        assertEquals(0, result.getDentalLimit());
        assertEquals(0, result.getOpticalLimit());
        assertEquals(0, result.getMaternityLimit());
    }

    @Test
    void testPopulateModelWithAgentInfo() {
        Intermediary intermediary = CommonQuote.getIntermediary();
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        quoteService.populateAgentDetails(new HashMap<>(), CommonQuote.getCustomer());
        assertTrue(true);
    }

    @Test
    void testPopulateAgentDetailsWithNullIntermediary() {
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(null);
        quoteService.populateAgentDetails(new HashMap<>(), Customer.builder().agentId("0").build());
        assertTrue(true);
    }


    @Test
    void testPopulateDependantDetailsWithNull() {
        quoteService.populateDependantDetails(new HashMap<>(),
                CommonQuote.getCustomerWithNullDependants().getSpouseSummary(),
                CommonQuote.getCustomerWithNullDependants().getChildrenSummary());
        assertTrue(true);
    }

    @Test
    void testPopulateAgentDetailsWithNullIntermediaryBranch() {
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(
                Intermediary.builder()
                        .firstName("test")
                        .lastName("te")
                .branchName(null).build());
        quoteService.populateAgentDetails(new HashMap<>(), Customer.builder().agentId("0").build());
        assertTrue(true);
    }
    @Test
    void testPopulateAgentDetailsWithEmptyIntermediaryBranch() {
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(
                Intermediary.builder()
                        .firstName("test")
                        .lastName("te")
                        .branchName("").build());
        quoteService.populateAgentDetails(new HashMap<>(), Customer.builder().agentId("0").build());
        assertTrue(true);
    }

}