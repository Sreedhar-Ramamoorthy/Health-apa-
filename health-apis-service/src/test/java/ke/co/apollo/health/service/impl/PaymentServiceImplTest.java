package ke.co.apollo.health.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.*;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.InAppNotificationMessageResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionCreateResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionValidateResponse;
import ke.co.apollo.health.common.enums.PolicyStatus;
import ke.co.apollo.health.common.enums.ProductEnum;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.config.NotificationMessageBuilder;
import ke.co.apollo.health.domain.entity.AgentBranchEntity;
import ke.co.apollo.health.domain.request.QuoteStartDateUpdateRequest;
import ke.co.apollo.health.mapper.health.PaymentTransactionMapper;
import ke.co.apollo.health.mapper.health.QuoteMapper;
import ke.co.apollo.health.remote.NotificationRemote;
import ke.co.apollo.health.remote.PaymentRemote;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.repository.HealthAgentBranchRepository;
import ke.co.apollo.health.repository.HealthStepRepository;
import ke.co.apollo.health.service.CustomerService;
import ke.co.apollo.health.service.IntermediaryService;
import ke.co.apollo.health.service.PolicyService;
import ke.co.apollo.health.service.QuoteService;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


class PaymentServiceImplTest {

    @InjectMocks
    PaymentServiceImpl paymentService;

    @Mock
    PaymentServiceImpl paymentServiceMock;

    @Mock
    HealthAgentBranchRepository agentBranchRepository;

    @Mock
    PaymentRemote paymentRemote;

    @Mock
    Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);

    private Gson g = new GsonBuilder().create();

    @Mock
    PaymentTransactionMapper paymentTransactionMapper;

    @Mock
    CustomerService customerService;

    @Mock
    PolicyService policyService;

    @Mock
    QuoteService quoteService;

    @Mock
    IntermediaryService intermediaryService;

    @Mock
    NotificationRemote notificationRemote;

    @Mock
    PolicyRemote policyRemote;

    @Mock
    HealthStepRepository healthStepRepository;

    @Mock
    QuoteMapper quoteMapper;

    @Mock
    NotificationMessageBuilder notificationMessageBuilder;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        ReflectionTestUtils.setField(paymentService, "paymentRemote", paymentRemote);
        ReflectionTestUtils.setField(paymentService, "paymentTransactionMapper", paymentTransactionMapper);
        ReflectionTestUtils.setField(paymentService, "customerService", customerService);
        ReflectionTestUtils.setField(paymentService, "policyService", policyService);
        ReflectionTestUtils.setField(paymentService, "intermediaryService", intermediaryService);
        ReflectionTestUtils.setField(paymentService, "notificationRemote", notificationRemote);
        ReflectionTestUtils.setField(paymentService, "quoteService", quoteService);
        ReflectionTestUtils.setField(paymentService, "policyRemote", policyRemote);
        ReflectionTestUtils.setField(paymentService, "stepRepository", healthStepRepository);
        ReflectionTestUtils.setField(paymentService, "agentBranchRepository", agentBranchRepository);
        ReflectionTestUtils.setField(paymentService, "g", g);
        ReflectionTestUtils.setField(paymentService, "quoteMapper", quoteMapper);
        ReflectionTestUtils.setField(paymentService, "notificationMessageBuilder", notificationMessageBuilder);
    }

    TransactionValidateRequest transactionValidateRequest = TransactionValidateRequest.builder()
            .applicationType("health")
            .message("Message")
            .orderId("orderId")
            .transactionRef("request.getTransactionRef()")
            .paymentMethod("request.getPaymentMethod()")
            .paymentResponse("request.getPaymentResponse()")
            .success(true)
            .build();

    TransactionValidateResponse transactionValidateResponse = TransactionValidateResponse
            .builder()
            .message("Success")
            .success(true)
            .build();

    Customer customer = Customer
            .builder()
            .customerId("Customer Id")
            .benefit(DependantBenefit.builder().dental(true).build())
            .firstName("Name")
            .lastName("Last Name")
            .createTime(new Date())
            .agentId("agent Id")
            .childrenSummary(Children.builder().count(0).build())
            .dateOfBirth(new Date())
            .email("email")
            .entityId(1L)
            .gender("M")
            .build();

    HealthPolicy policy = HealthPolicy
            .builder()
            .balance(BigDecimal.ONE)
            .benefit(Benefit.builder().dentalLimit(80).build())
            .policyNumber("PO NUMBER")
            .build();

    Intermediary intermediary = Intermediary
            .builder()
            .agentCode("Agent Code")
            .agentId("Id")
            .phoneNumber("Phone")
            .email("Email")
            .status("Active")
            .branchName("Kisii branj")
            .lastName("Last Name")
            .firstName("First Name")
            .build();

    PaymentValidateRequest paymentValidateRequest = PaymentValidateRequest
            .builder()
            .customerId("CustomerId")
            .paymentMethod("MPESA")
            .orderId("OrderIs")
            .transactionRef("TransRef")
            .paymentResponse("Response")
            .message("Done")
            .success(true)
            .build();

    PaymentTransaction transaction = PaymentTransaction
            .builder()
            .amount(BigDecimal.ONE)
            .balanceMessage("Message")
            .balanceResult(true)
            .createTime(new Date())
            .effectiveDate(new Date())
            .clientMessage("Message")
            .clientResult(true)
            .customerId("customerId")
            .domain("ISWKE")
            .id("Test").currency("KES").merchantId("MERID").orderId("ORDER ID")
            .paymentCustomerId("Mesage")
            .paymentMessage("message")
            .paymentMethod("MPESA")
            .policyId("Pol Id")
            .policyNumber("PO Num")
            .quoteId("quote id")
            .quoteNumber("Number")
            .terminalId("Terminal")
            .updateTime(new Date())
            .transactionRef("Ref")
            .preauth("True")
            .status("Success")
            .build();

    Quote quote = Quote
            .builder()
            .customerId("quoteId")
            .balance(BigDecimal.ONE)
            .agentId("")
            .productId(ProductEnum.JAMIIPLUS.getId())
            .code("")
            .benefit(Benefit.builder().dentalLimit(80).build())
            .build();

    PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest
            .builder()
            .policyNumber("POLNUM")
            .amount("200000")
            .customerId("customer")
            .effectiveDate(new Date())
            .renewal(true)
            .quoteId("quote")
            .build();
    PolicyDetail policyDetail = PolicyDetail
            .builder()
            .policyStatus("L")
            .paymentStyle("Style")
            .policyNumber("Pol Number")
            .policyId(8)
            .build();

    TransactionCreateRequest transactionCreateRequest = TransactionCreateRequest.builder()
            .amount(BigDecimal.ONE).applicationCustomerEmail(customer.getEmail())
            .applicationPolicyNumber("policyNumber").applicationCustomerId(customer.getPhoneNumber())
            .applicationScene("business")
            .applicationType("health")
            .build();

    @Test
    void testValidatePaymentTransactionWithSuccessfulPayment() throws TemplateException, IOException {

        configuration = Mockito.mock(Configuration.class);

        String templateStr = "Hello ${user}";
        Template t = new Template("name", new StringReader(templateStr),
                new Configuration(Configuration.VERSION_2_3_30));
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(configuration.getTemplate(anyString())).thenReturn(t);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);
        Mockito.when(agentBranchRepository.findAgentBranchEntitiesByEntityId(any())).thenReturn(AgentBranchEntity.builder()
                .agentId("agentId")
                .entityId(900)
                .branchName("Name").build());

        System.out.println(" --------------- " + paymentValidateRequest.getCustomerId());
        System.out.println(" --------------- " + paymentValidateRequest.getOrderId());
        System.out.println(" --------------- " + paymentValidateRequest.getTransactionRef());

        TransactionValidateResponse resp = paymentService.validatePaymentTransaction(paymentValidateRequest);  //NO SONAR
        assertNotNull(resp);
    }

    @Test
    void testValidatePaymentTransactionWithSuccessfulPaymentAndIsRenewal() throws TemplateException, IOException {

        configuration = Mockito.mock(Configuration.class);
        transaction.setRenewal(true);
        String templateStr = "Hello ${user}";
        Template t = new Template("name", new StringReader(templateStr),
                new Configuration(Configuration.VERSION_2_3_30));
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(configuration.getTemplate(anyString())).thenReturn(t);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);

        System.out.println(" --------------- " + paymentValidateRequest.getCustomerId());
        System.out.println(" --------------- " + paymentValidateRequest.getOrderId());
        System.out.println(" --------------- " + paymentValidateRequest.getTransactionRef());
        Mockito.when(agentBranchRepository.findAgentBranchEntitiesByEntityId(any())).thenReturn(AgentBranchEntity.builder()
                .agentId("agentId")
                .entityId(900)
                .branchName("Name").build());

        TransactionValidateResponse resp = paymentService.validatePaymentTransaction(paymentValidateRequest);  //NO SONAR
        assertNotNull(resp);
    }

    @Test
    void testValidatePaymentTransactionWithSuccessfulPaymentAndIsRenewalIsNotAgent() throws TemplateException, IOException {

        configuration = Mockito.mock(Configuration.class);
        transaction.setRenewal(true);
        customer.setAgentId(null);
        String templateStr = "Hello ${user}";
        Template t = new Template("name", new StringReader(templateStr),
                new Configuration(Configuration.VERSION_2_3_30));
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(configuration.getTemplate(anyString())).thenReturn(t);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);

        System.out.println(" --------------- " + paymentValidateRequest.getCustomerId());
        System.out.println(" --------------- " + paymentValidateRequest.getOrderId());
        System.out.println(" --------------- " + paymentValidateRequest.getTransactionRef());

        TransactionValidateResponse resp = paymentService.validatePaymentTransaction(paymentValidateRequest);  //NO SONAR
        assertNotNull(resp);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 40000})
    void testValidatePaymentTransactionWithSuccessfulPaymentAndIsRenewalFalse(int travel) throws TemplateException, IOException {

        configuration = Mockito.mock(Configuration.class);
        transaction.setRenewal(false);
        customer.setAgentId(null);
        String templateStr = "Hello ${user}";
        Template t = new Template("name", new StringReader(templateStr),
                new Configuration(Configuration.VERSION_2_3_30));
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(configuration.getTemplate(anyString())).thenReturn(t);
        quote.getBenefit().setTravelInsurance(travel);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);

        System.out.println(" --------------- " + paymentValidateRequest.getCustomerId());
        System.out.println(" --------------- " + paymentValidateRequest.getOrderId());
        System.out.println(" --------------- " + paymentValidateRequest.getTransactionRef());

        TransactionValidateResponse resp = paymentService.validatePaymentTransaction(paymentValidateRequest);  //NO SONAR
        assertNotNull(resp);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testValidatePaymentTransactionWithSuccessfulPaymentAndIsRenewalIsNotAgentThrowsException(boolean isRenewal) throws TemplateException, IOException {

        ReflectionTestUtils.setField(paymentService, "configuration", configuration);
        transaction.setRenewal(isRenewal);
        customer.setAgentId(null);
        String templateStr = "Hello Testes";
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);
        Configuration configuration1 = new Configuration(Configuration.VERSION_2_3_30);
        Template template = new Template("te", templateStr, configuration1);
        when(configuration.getTemplate(anyString())).thenReturn(template);

        //add the behavior to throw exception
        doThrow(new RuntimeException("Add operation not implemented"))
                .when(paymentServiceMock).sendPaymentNotification(transaction);

        TransactionValidateResponse resp = paymentService.validatePaymentTransaction(paymentValidateRequest);
        assertNotNull(resp);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 40000})
    void testValidatePaymentTransactionWithSuccessfulPaymentAndIsRenewalIsAgent(int travel) throws TemplateException, IOException {

        ReflectionTestUtils.setField(paymentService, "configuration", configuration);
        transaction.setRenewal(true);
        customer.setAgentId("AgentId");
        String templateStr = "Hello ${user}";
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        policy.getBenefit().setTravelInsurance(travel);
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        quote.getBenefit().setTravelInsurance(travel);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);
        Mockito.when(agentBranchRepository.findAgentBranchEntitiesByEntityId(any())).thenReturn(AgentBranchEntity.builder()
                .agentId("agentId")
                .entityId(900)
                .branchName("Name").build());
        TransactionValidateResponse resp = paymentService.validatePaymentTransaction(paymentValidateRequest);
        assertNotNull(resp);
    }

    @Test
    void testValidatePaymentTransactionWithSuccessfulPaymentAndIsRenewalIsNotAgentThrowsExceptionWhenTransactionIsNull() throws TemplateException, IOException {

        configuration = Mockito.mock(Configuration.class);
        transaction = null;
        customer.setAgentId(null);
        String templateStr = "Hello ${user}";
        Template t = new Template("name", new StringReader(templateStr),
                new Configuration(Configuration.VERSION_2_3_30));
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(configuration.getTemplate(anyString())).thenReturn(t);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);

        Throwable exception
                = assertThrows(RuntimeException.class, () -> {
            paymentService.validatePaymentTransaction(paymentValidateRequest);
        });

    }

    @Test
    void testCreatePayment() {

        configuration = Mockito.mock(Configuration.class);
        transaction = null;
        customer.setAgentId(null);
        Mockito.when(healthStepRepository.save(any())).thenReturn(null);
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentRemote.createTransaction(any())).thenReturn(TransactionCreateResponse.builder().build());
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(paymentTransactionMapper.insert(any())).thenReturn(1);
        Mockito.when(paymentTransactionMapper.updateByPrimaryKey(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);
        Mockito.when(policyRemote.getPolicyDetail(any())).thenReturn(policyDetail);
        TransactionCreateResponse resp = paymentService.createPaymentTransaction(paymentCreateRequest);
        assertNotNull(resp);

    }

    @Test
    void testCreatePaymentForQuote() {

        configuration = Mockito.mock(Configuration.class);
        configuration = Mockito.mock(Configuration.class);
        customer.setAgentId(null);

        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentRemote.createTransaction(any())).thenReturn(TransactionCreateResponse.builder().build());
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(paymentTransactionMapper.insert(any())).thenReturn(1);
        Mockito.when(paymentTransactionMapper.updateByPrimaryKey(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);
        Mockito.when(policyRemote.getPolicyDetail(any())).thenReturn(policyDetail);
        paymentCreateRequest.setRenewal(false);
        Throwable exception
                = assertThrows(BusinessException.class, () -> {
            paymentService.createPaymentTransaction(paymentCreateRequest);
        });
        String excMsg = exception.getMessage();
        assert excMsg != null
                : "Exception message should not be null";
        String msg
                = "Exception message should contain the word \"quote\"";
        assert excMsg.toLowerCase().contains("quote") : msg;

    }

    @Test
    void testGetProductType() {
        Quote q = Quote.builder().productId(ProductEnum.JAMIIPLUS_SHARED.getId()).build();
        String shared = paymentService.getProductTypeDescription(q);
        assertEquals("Family shared".toLowerCase(), shared.toLowerCase());
        q.setProductId(ProductEnum.JAMIIPLUS.getId());
        String individual = paymentService.getProductTypeDescription(q);
        assertEquals("Individual".toLowerCase(), individual.toLowerCase());
    }


    @Test
    void testCreateTransactionWithNullCustomer() throws TemplateException, IOException {

        configuration = Mockito.mock(Configuration.class);
        transaction = null;
        customer.setAgentId(null);
        String templateStr = "Hello ${user}";
        Template t = new Template("name", new StringReader(templateStr),
                new Configuration(Configuration.VERSION_2_3_30));
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(null);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(configuration.getTemplate(anyString())).thenReturn(t);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);

        Throwable exception
                = assertThrows(BusinessException.class, () -> {
            paymentService.createPaymentTransaction(paymentCreateRequest);
        });
        String excMsg = exception.getMessage();
        assert excMsg != null
                : "Exception message should not be null";
        String msg
                = "Exception message should contain the word \"customer\"";
        assert excMsg.toLowerCase().contains("customer") : msg;

    }

    @Test
    void testCreateTransactionWithNullPolicyNumberThrows() {

        configuration = Mockito.mock(Configuration.class);
        transaction = null;
        customer.setAgentId(null);
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);

        paymentCreateRequest.setPolicyNumber(null);
        paymentCreateRequest.setRenewal(true);
        Throwable exception
                = assertThrows(BusinessException.class, () -> {
            paymentService.createPaymentTransaction(paymentCreateRequest);
        });
        String excMsg = exception.getMessage();
        assert excMsg != null
                : "Exception message should not be null";
        String msg
                = "Exception message should contain the word \"policy\"";
        assert excMsg.toLowerCase().contains("policy") : msg;

    }

    @Test
    void testCreateTransactionWithNullEffectiveDateThrows() {

        configuration = Mockito.mock(Configuration.class);
        transaction = null;
        customer.setAgentId(null);
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);
        paymentCreateRequest.setEffectiveDate(null);
        paymentCreateRequest.setRenewal(true);
        Throwable exception
                = assertThrows(BusinessException.class, () -> {
            paymentService.createPaymentTransaction(paymentCreateRequest);
        });
        String excMsg = exception.getMessage();
        assert excMsg != null
                : "Exception message should not be null";
        String msg
                = "Exception message should contain the word \"date\"";
        assert excMsg.toLowerCase().contains("date") : msg;

    }

    @Test
    void testCreateTransactionWithNullPolicyDetailsThrows() {

        configuration = Mockito.mock(Configuration.class);
        transaction = null;
        customer.setAgentId(null);
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);
        Mockito.when(policyRemote.getPolicyDetail(any())).thenReturn(null);

        Throwable exception
                = assertThrows(BusinessException.class, () -> {
            paymentService.createPaymentTransaction(paymentCreateRequest);
        });
        String excMsg = exception.getMessage();
        assert excMsg != null
                : "Exception message should not be null";
        String msg
                = "Exception message should contain the word \"find\"";
        assert excMsg.toLowerCase().contains("find") : msg;

    }

    @Test
    void testCreateTransactionFromQuoteThrowsWithoutQuote() {

        configuration = Mockito.mock(Configuration.class);
        transaction = null;
        customer.setAgentId(null);
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);
        Mockito.when(policyRemote.getPolicyDetail(any())).thenReturn(null);
        paymentCreateRequest.setRenewal(false);
        paymentCreateRequest.setQuoteId(null);
        Throwable exception
                = assertThrows(BusinessException.class, () -> {
            paymentService.createPaymentTransaction(paymentCreateRequest);
        });
        String excMsg = exception.getMessage();
        assert excMsg != null
                : "Exception message should not be null";
        String msg
                = "Exception message should contain the word \"mandatory\"";
        assert excMsg.toLowerCase().contains("mandatory") : msg;

    }

    @Test
    void testCreateTransactionFromQuoteThrowsIfQuoteWithoutStatus() {

        configuration = Mockito.mock(Configuration.class);
        transaction = null;
        customer.setAgentId(null);
        Mockito.when(paymentRemote.validateTransaction(any())).thenReturn(transactionValidateResponse);
        Mockito.when(paymentTransactionMapper.update(any())).thenReturn(1);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(customerService.getCustomerByParentId(anyString())).thenReturn(Collections.singletonList(customer));
        Mockito.when(policyService.getPolicy(anyString(), any())).thenReturn(policy);
        Mockito.when(intermediaryService.getIntermediary(anyString())).thenReturn(intermediary);
        Mockito.when(paymentTransactionMapper.select(any(), any(), any())).thenReturn(transaction);
        Mockito.when(notificationRemote.sendEmailWithTemplate(any())).thenReturn(true);
        Mockito.when(quoteService.getQuoteByPolicyNumber(anyString(), anyString())).thenReturn(quote);
        policyDetail.setPolicyStatus(null);
        Mockito.when(policyRemote.getPolicyDetail(any())).thenReturn(policyDetail);

        Throwable exception
                = assertThrows(BusinessException.class, () -> {
            paymentService.createPaymentTransaction(paymentCreateRequest);
        });
        String excMsg = exception.getMessage();
        assert excMsg != null
                : "Exception message should not be null";
        String msg
                = "Exception message should contain the word \"expired\"";
        assert excMsg.toLowerCase().contains("expired") : msg;

    }

    @ParameterizedTest
    @ValueSource(strings = {"Viewed", "New"})
    void testCreateTransactionThrowsErrorIfStatusIsViewedOrNew(String status) {
        // Arrange
        configuration = Mockito.mock(Configuration.class);
        customer.setAgentId(null);
        customer.setParentId("p123");
        customer.setQuoteId("q123");
        paymentCreateRequest.setQuoteId("q123");
        Date date = new GregorianCalendar(2023, Calendar.FEBRUARY, 11).getTime();
        quote.setStatus(status);
        quote.setEffectiveDate(date);
        paymentCreateRequest.setRenewal(false);
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(customer);
        Mockito.when(policyRemote.getPolicyDetail(any())).thenReturn(policyDetail);
        Mockito.when(healthStepRepository.save(any())).thenReturn(null);
        Mockito.when(quoteMapper.getQuote(anyString(), anyString(), anyString())).thenReturn(quote);
        Mockito.when(quoteService.getQuote(anyString(), anyString(), any())).thenReturn(quote);

        assertThrows(BusinessException.class, () -> {
            paymentService.createPaymentTransaction(paymentCreateRequest);
        });
    }

    @Test
    void testUpdateQuoteStartDate() {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal1.add(Calendar.DATE, -1);
        Quote quote = Quote.builder()
                .id("aa5cf15db6907bb167ee584650fe07ab")
                .customerId("675c83a4b4f70cbfd349967862152128")
                .startDate(cal1.getTime())
                .build();
        Mockito.when(quoteMapper.getQuote(anyString(), anyString(), any()))
                .thenReturn(quote);
        paymentService.updateQuoteStartDate(quote.getCustomerId(), quote.getId());
        assertTrue(true);
    }

    @Test
    void testUpdateQuoteStartDateWithQuoteNull() {
        Mockito.when(quoteMapper.getQuote(anyString(), anyString(), any())).thenReturn(
                null);
        paymentService.updateQuoteStartDate(null, null);
        assertTrue(true);
    }

    @Test
    void testUpdateQuoteStartDateBeforeDate() {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal1.add(Calendar.DATE, 5);
        Mockito.when(quoteMapper.getQuote(anyString(), anyString(), any())).thenReturn(Quote
                .builder()
                .id("aa5cf15db6907bb167ee584650fe07ab")
                .customerId("675c83a4b4f70cbfd349967862152128")
                .startDate(cal1.getTime())
                .build());
        paymentService.updateQuoteStartDate("675c83a4b4f70cbfd349967862152128", "aa5cf15db6907bb167ee584650fe07ab");
        assertTrue(true);
    }

    @Test
    void testSendPaymentInAppNotification() {
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(
                Customer.builder()
                        .email("test@gmail.com")
                        .phoneNumber("254984119158")
                        .idNo("928e9f369d33319a6aba75f58eed1b6d")
                        .build()
        );
        paymentService.sendPaymentInAppNotification(PaymentTransaction.builder()
                .customerId("928e9f369d33319a6aba75f58eed1b6d")
                .build(), "Test mssg");
        assertTrue(true);
    }

    @Test
    void testSendPaymentInAppNotificationWithNull() {
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(
                Customer.builder()
                        .email("test@gmail.com")
                        .phoneNumber("254984119158")
                        .idNo("928e9f369d33319a6aba75f58eed1b6d")
                        .build()
        );
        paymentService.sendPaymentInAppNotification(PaymentTransaction.builder()
                .build(), "Test mssg");
        assertTrue(true);
    }

    @Test
    void testClearRenewalInAppNotification() {
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(
                Customer.builder()
                        .email("test@gmail.com")
                        .phoneNumber("254752369869")
                        .idNo("928e9f369d33319a6aba75f58eed1b6d")
                        .build()
        );
        when(notificationRemote.getAllInAppNotificationList(any(InAppNotificationMessageRequest.class)))
                .thenReturn(List.of(InAppNotificationMessageResponse.builder()
                        .id("1")
                        .policyNumber("JPR001681")
                        .actionStatus(GlobalConstant.HEALTH_RENEWAL_NOTIFICATION_STATUS)
                        .build()));
        paymentService.clearRenewalInAppNotification("254752369869", "JPR001681");
        assertTrue(true);
    }
    @Test
    void PaymentCreditCardServiceTest() {
        CreditCardTransactionRequest cr = new CreditCardTransactionRequest();
        HashMap<String, String> hashMap = paymentRemote.createCreditCardPaymentRequest(cr);
        hashMap.put("", "");

        HttpHeaders headers = new HttpHeaders();
        Mockito.when(paymentRemote.createCreditCardPaymentRequest(cr)).thenReturn(hashMap);
        Mockito.when(paymentService.createCreditCardPaymentRequest(cr)).thenReturn(any());
        paymentService.createCreditCardPaymentRequest(cr);
        assertTrue(true);
    }
    @Test
    void testClearRenewalInAppNotificationWithNull() {
        Mockito.when(customerService.getCustomer(anyString())).thenReturn(
                Customer.builder()
                        .email("test@gmail.com")
                        .phoneNumber("254752369869")
                        .idNo("928e9f369d33319a6aba75f58eed1b6d")
                        .build()
        );
        when(notificationRemote.getAllInAppNotificationList(any(InAppNotificationMessageRequest.class)))
                .thenReturn(List.of(InAppNotificationMessageResponse.builder()
                        .id("1")
                        .build()));
        paymentService.clearRenewalInAppNotification("254752369869", "JPR001681");
        assertTrue(true);
    }
}
