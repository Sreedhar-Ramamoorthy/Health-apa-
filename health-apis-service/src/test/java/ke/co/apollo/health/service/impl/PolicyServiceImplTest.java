package ke.co.apollo.health.service.impl;


import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.*;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.config.HealthInitializer;
import ke.co.apollo.health.config.NotificationMessageBuilder;
import ke.co.apollo.health.config.PolicyRenewalDaysConfig;
import ke.co.apollo.health.config.PolicyRenewalExecutorConfiguration;
import ke.co.apollo.health.config.PolicyStatusConfig;
import ke.co.apollo.health.feign.NotificationClient;
import ke.co.apollo.health.mapper.health.QuoteMapper;
import ke.co.apollo.health.remote.NotificationRemote;
import ke.co.apollo.health.utils.GenericExcelFileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.domain.request.CustomerSearchRequest;
import ke.co.apollo.health.domain.response.HealthPolicyListResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.domain.request.TransactionDetailTaskAddRequest;
import ke.co.apollo.health.mapper.health.TransactionDetailTaskMapper;
import ke.co.apollo.health.service.PolicyService;
import ke.co.apollo.health.service.QuoteService;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.service.CustomerService;
import ke.co.apollo.health.service.ProductService;
import ke.co.apollo.health.mapper.health.PolicyMapper;
import ke.co.apollo.health.repository.PolicyOverComingRepository;
import ke.co.apollo.health.service.PremiumService;
import ke.co.apollo.health.service.ProductService;
import ke.co.apollo.health.event.ReminderEventPublisher;

import java.math.BigDecimal;

import ke.co.apollo.health.common.domain.model.response.PolicyRenewalResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;
import ke.co.apollo.health.domain.entity.PolicyOverComingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import ke.co.apollo.health.common.domain.model.response.PolicyOverComingResponseDto;
import ke.co.apollo.health.common.domain.model.response.ApplicationRenewalPolicyListResponse;
import ke.co.apollo.health.domain.request.CustomerIdRequest;


class PolicyServiceImplTest {

        @InjectMocks
        PolicyServiceImpl policyService;

        @Mock
        PolicyRemote policyRemote;

        @Mock
        CustomerService customerService;

        @Mock
        PolicyMapper policyMapper;

        @Mock
        QuoteService quoteService;
        @Mock
        private GenericExcelFileUtils fileUtils;
        @Mock
        PolicyRenewalDaysConfig policyRenewalDaysConfig;
        @Mock
        ProductService productService;
        @Mock
        NotificationMessageBuilder notificationMessageBuilder;
        @Mock
        PremiumService premiumService;
        @Mock
        QuoteMapper quoteMapper;
        @Mock
        ReminderEventPublisher reminderEventPublisher;

        @Mock
        PolicyOverComingRepository policyOverComingRepository;

        @Mock
        private NotificationClient notificationClient;

        @Mock
        private NotificationRemote notificationRemote;

        @Mock
        PolicyRenewalExecutorConfiguration renewalExecutorConfiguration;

        @Mock
        PolicyStatusConfig policyStatusConfig;

        @BeforeEach
        void setUpMocks(){
                initMocks(this);
                }
  
        @Test
        void renewalPolicyExceptionTest(){

                PolicyDetail policyDetail = PolicyDetail.builder()
                        .totalPremium(new BigDecimal(100))
                        .premiumPaid(new BigDecimal(10))
                        .premiumLeftToPay(new BigDecimal(10))
                        .policyRenewalDate(new Date())
                        .policyStartDate(new Date())
                        .policyEffectiveDate(new Date())
                        .policyHolderEntityId(1L)
                        .policyStatus("M")
                        .policyId(123)
                        .policyNumber("123")
                        .policyHolderName("Test User")
                        .productName("Test Productt")
                        .principalMember("Principal")
                        .principalId(1L)
                        .principalDob(new Date())
                        .paymentStyle("Style")
                        .productId(1)
                        .build();

                when(policyRemote.getPolicyDetail(any())).thenReturn(null);
                when(productService.calcRenewPremiumByTotalPremium(any(),any(),any(),any(),any())).thenReturn(RenewalPremium.builder().build());
                when(quoteService.searchQuoteByPolicyId(any(),any())).thenReturn("QUOTE001");

                PolicyRenewalRequest obj = PolicyRenewalRequest.builder()
                                .policyNumber("123")
                                .effectiveDate(new Date())
                                .build();


                assertThrows(BusinessException.class, () -> policyService.renewalPolicy(obj));

        }


        @Test
        void renewalPolicyTest(){

                PolicyDetail policyDetail = PolicyDetail.builder()
                        .totalPremium(new BigDecimal(100))
                        .premiumPaid(new BigDecimal(10))
                        .premiumLeftToPay(new BigDecimal(10))
                        .policyRenewalDate(new Date())
                        .policyStartDate(new Date())
                        .policyEffectiveDate(new Date())
                        .policyHolderEntityId(1L)
                        .policyStatus("M")
                        .policyId(123)
                        .policyNumber("123")
                        .policyHolderName("Test User")
                        .productName("Test Productt")
                        .principalMember("Principal")
                        .principalId(1L)
                        .principalDob(new Date())
                        .paymentStyle("Style")
                        .productId(1)
                        .build();

                HealthPolicy hp = HealthPolicy.builder()
                        .quoteId("QUOTE001")
                        .policyId(policyDetail.getPolicyId())
                        .policyNumber(policyDetail.getPolicyNumber())
                        .startDate(policyDetail.getPolicyStartDate())
                        .effectiveDate(policyDetail.getPolicyEffectiveDate())
                        .renewalDate(policyDetail.getPolicyRenewalDate())
                        .policyHolderId(policyDetail.getPolicyHolderEntityId())
                        .productId(policyDetail.getProductId())
                        .status(policyDetail.getPolicyStatus())
                        .balance(policyDetail.getPremiumLeftToPay())
                        .createTime(new Date())
                        .build();

                when(policyMapper.select(any(),any())).thenReturn(hp);
                PolicyRenewalRequest obj = PolicyRenewalRequest.builder()
                                .policyNumber("123")
                                .effectiveDate(new Date())
                                .build();


                PolicyRenewalResponse resp = policyService.renewalPolicy(obj);
                assertNotNull(resp);
        }

        @Test
        void policyUpdateDetailsTest(){
                List<PolicyOverComingEntity> quote = Collections.singletonList(PolicyOverComingEntity.builder().build());
                Page<PolicyOverComingEntity> page = new PageImpl<PolicyOverComingEntity>(quote);
                when(policyOverComingRepository.findAllByRenewalDateBetween(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(page);
                ComingPolicyListRequest obj = ComingPolicyListRequest.builder()
                                                .index(1)
                                                .limit(10)
                        .startDate(DateUtils.addMonths(new Date(), -1))
                        .endDate(DateUtils.addMonths(new Date(), 2))
                                                .build();
                PolicyOverComingResponseDto resp = policyService.policyUpdateDetails(obj);
                assertNotNull(resp);
                }
        @Test
        void policyUpdateDetailsTestNullDates(){
                List<PolicyOverComingEntity> quote = Collections.singletonList(PolicyOverComingEntity.builder().build());
                Page<PolicyOverComingEntity> page = new PageImpl<PolicyOverComingEntity>(quote);
                when(policyOverComingRepository.findAllByRenewalDateBetween(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(page);
                ComingPolicyListRequest obj = ComingPolicyListRequest.builder()
                        .index(1)
                        .limit(10)
                        .build();
                PolicyOverComingResponseDto resp = policyService.policyUpdateDetails(obj);
                assertNotNull(resp);
        }
        @ParameterizedTest
        @ValueSource(booleans = {true, false} )
        void comingPolicyListInExcel(boolean list){
                List<PolicyOverComingEntity> policyList = new java.util.ArrayList<>(Collections.singletonList(PolicyOverComingEntity.builder().build()));
                if(list)
                        policyList.clear();

                byte [] b = new byte[0];
                when(policyOverComingRepository.findAllByRenewalDateBetween(Mockito.any(),Mockito.any())).thenReturn(policyList);
                when(fileUtils.createExcelFile(any(),anyString(),anyString())).thenReturn(b);
                ComingPolicyListRequestInExcel obj = ComingPolicyListRequestInExcel.builder()
                                                .startDate(new Date())
                                                .endDate(new Date())
                                                .build();
                byte[] resp = policyService.comingPolicyListInExcel(obj);
                assertNotNull(resp);
                }


        @ParameterizedTest
        @ValueSource(booleans = {true, false} )
        void comingPolicyIn60Days(boolean list){
                List<PolicyOverComingEntity> policyList = new java.util.ArrayList<>(Collections.singletonList(PolicyOverComingEntity.builder().build()));
                if(list)
                        policyList.clear();

                byte [] b = new byte[]{1,2,4,6,8,0,8,9};
                when(policyOverComingRepository.findAllByRenewalDateBetween(Mockito.any(),Mockito.any())).thenReturn(policyList);
                when(fileUtils.createExcelFile(any(),anyString(),anyString())).thenReturn(b);
                ComingPolicyListRequestInExcel obj = ComingPolicyListRequestInExcel.builder()
                        .startDate(new Date())
                        .endDate(new Date())
                        .build();
                when(notificationClient.sendEmailAttachmentBytes(anyString())).thenReturn("");
                policyService.renewalsDueIn60Days();
                assertNotNull(obj);
        }

        @Test
        void getCustomerPolicyListNullEntityTest(){
                CustomerDetailResponse obj = CustomerDetailResponse.builder()
                                        .customerId("1")
                                        .quoteId("2")
                                        .startDate(new Date())
                                        .build();

                CustomerPolicyListRequest request = CustomerPolicyListRequest.builder()
                                        .customerId("1")
                                        .build();

                when(customerService.getCustomer(any(CustomerSearchRequest.class))).thenReturn(obj);

                List<HealthPolicyListResponse> resp = policyService.getCustomerPolicyList(request);
                assertNotNull(resp);
                }

        @Test
        void getCustomerPolicyListTest(){
                CustomerDetailResponse obj = CustomerDetailResponse.builder()
                                        .customerId("1")
                                        .quoteId("2")
                                        .startDate(new Date())
                                        .principal(Principal.builder().firstName("John").entityId(1L).build())
                                        .build();

                CustomerPolicyListRequest request = CustomerPolicyListRequest.builder()
                                        .customerId("1")
                                        .build();

                when(customerService.getCustomer(any(CustomerSearchRequest.class))).thenReturn(obj);

                List<HealthPolicyListResponse> resp = policyService.getCustomerPolicyList(request);
                assertNotNull(resp);
                }



        @Test
        void getCustomerPolicyListEntityPolicyListRequestTest(){
                EntityPolicyListRequest request = EntityPolicyListRequest.builder()
                                        .build();
                List<HealthPolicyListResponse> resp = policyService.getCustomerPolicyList(request);
                assertNotNull(resp);
                }


        @Test
        void getPolicyListsTest(){
                
                Policy p = Policy.builder()
                        .policyId(123)
                        .policyEffectiveDate(new Date())
                        .productId(1)
                        .policyStartDate(new Date())
                        .policyRenewalDate(new Date())
                        .policyStatus("A")
                        .policyAmount(BigDecimal.ZERO)
                        .build();
                                        
                when(policyRemote.getPolicyLists(any())).thenReturn(Collections.singletonList(p));

                ApplicationRenewalPolicy renewalPolicy = ApplicationRenewalPolicy.builder()
                                .policyId(123)
                                .effectiveDate(new Date())
                                .build();

                when(policyMapper.searchRenewedPolicyList(any())).thenReturn(Collections.singletonList(renewalPolicy));


                CustomerPolicyListRequest request = CustomerPolicyListRequest.builder()
                                        .customerId("1")
                                        .build();

                List<HealthPolicyListResponse> resp = policyService.getPolicyLists(request);
                assertNotNull(resp);
                }

        @Test
        void createCustomerPolicyCacheNullEntityTest(){

                when(customerService.getCustomer(any(String.class))).thenReturn(null);

                CustomerIdRequest request = CustomerIdRequest.builder()
                                                .customerId("1")
                                                .build();

                boolean b = policyService.createCustomerPolicyCache(request);
                assertFalse(b);
                }

        // @Test
        // void createCustomerPolicyCacheFalseTest(){

        //         Customer cust = Customer.builder()
        //                 .entityId(1L)
        //                 .build();

        //         when(customerService.getCustomer(any(String.class))).thenReturn(cust);

        //         Policy p = Policy.builder()
        //                 .policyId(123)
        //                 .policyEffectiveDate(new Date())
        //                 .productId(1)
        //                 .policyStartDate(new Date())
        //                 .policyRenewalDate(new Date())
        //                 .policyStatus("A")
        //                 .policyAmount(BigDecimal.ZERO)
        //                 .build();
                                        
        //         when(policyRemote.getPolicyLists(any())).thenReturn(Collections.singletonList(p));


        //         CustomerIdRequest request = CustomerIdRequest.builder()
        //                                         .customerId("1")
        //                                         .build();

        //         boolean b = policyService.createCustomerPolicyCache(request);
        //         assertFalse(b);
        //         }

        @Test
        void createCustomerPolicyCacheTest(){

                Customer cust = Customer.builder()
                        .entityId(1L)
                        .build();

                when(customerService.getCustomer(any(String.class))).thenReturn(cust);

                Policy p = Policy.builder()
                        .policyId(123)
                        .policyEffectiveDate(new Date())
                        .productId(1)
                        .policyStartDate(new Date())
                        .policyRenewalDate(new Date())
                        .policyStatus("A")
                        .policyAmount(BigDecimal.ZERO)
                        .build();
                                        
                when(policyRemote.getPolicyLists(any())).thenReturn(Collections.singletonList(p));
                when(policyMapper.insertCustomerPolicy(any())).thenReturn(1);

                CustomerIdRequest request = CustomerIdRequest.builder()
                                                .customerId("1")
                                                .build();

                boolean b = policyService.createCustomerPolicyCache(request);
                assertTrue(b);
                }

        // @Test
        // void renewalPolicyForComingWorkerTest(){

        //         PolicyDetail policyDetail = PolicyDetail.builder()
        //                 .totalPremium(new BigDecimal(100))
        //                 .premiumPaid(new BigDecimal(10))
        //                 .premiumLeftToPay(new BigDecimal(10))
        //                 .policyRenewalDate(new Date())
        //                 .policyStartDate(new Date())
        //                 .policyEffectiveDate(new Date())
        //                 .policyHolderEntityId(1L)
        //                 .policyStatus("M")
        //                 .policyId(123)
        //                 .policyNumber("123")
        //                 .policyHolderName("Test User")
        //                 .productName("Test Productt")
        //                 .principalMember("Principal")
        //                 .principalId(1L)
        //                 .principalDob(new Date())
        //                 .paymentStyle("Style")
        //                 .productId(1)
        //                 .build();

        //         when(policyRemote.getPolicyDetail(any())).thenReturn(policyDetail);

        //         PolicyRenewalRequest request = PolicyRenewalRequest.builder()
        //                 .policyNumber("123")
        //                 .effectiveDate(new Date())
        //                 .build();

        //         PolicyRenewalResponse resp = policyService.renewalPolicyForComingWorker(request);
        //         assertNotNull(resp);

        //         }

        @Test
        void testRenewalNotificationPolicies() throws InterruptedException {
                List<Integer> renewalDaysList = Arrays.asList(30,15,7,0);
                when(policyRenewalDaysConfig.getRenewal()).thenReturn(renewalDaysList);
                when(policyOverComingRepository.findAllPoliciesDueForRenewalIn(anyInt()))
                        .thenReturn(Collections.emptyList());
                policyService.renewalNotificationPolicies();
                assertTrue(true);

        }
        @Test
        void testExpiredNotificationPolicies() throws InterruptedException {
                List<Integer> renewalDaysList = Arrays.asList(1,7,14,21,30);
                when(policyRenewalDaysConfig.getExpired()).thenReturn(renewalDaysList);
                when(policyOverComingRepository.findAllPoliciesDueForRenewalIn(anyInt()))
                        .thenReturn(Collections.emptyList());
                policyService.expiredNotificationPolicies();
                assertTrue(true);
        }
        @Test
        void testGetUnderwritingQuotes() {
                CustomerPolicyListRequest request = CustomerPolicyListRequest
                        .builder()
                        .customerId("928e9f369d33319a6aba75f58eed1b6d")
                        .entityId("1707356")
                        .filter("")
                        .index(1)
                        .limit(10)
                        .range("All")
                        .sort("desc")
                        .sortType("renewalDate")
                        .build();
                Quote quote = Quote
                        .builder()
                        .productId(49)
                        .extPolicyNumber("JPR0115549")
                        .startDate(new Date())
                        .effectiveDate(new Date())
                        .renewalDate(new Date())
                        .status("Underwriting")
                        .premium(Premium.builder().totalPremium(BigDecimal.valueOf(199867)).build())
                        .build();
                List<Quote> mockQuotesList = Arrays.asList(quote);
                when(quoteMapper.getCustomerQuotes(any(), any(), any())).thenReturn(mockQuotesList);
                List<HealthPolicyListResponse> result = policyService.getUnderwritingQuotes(request);
                assertEquals(mockQuotesList.size(), result.size());
        }

        @Test
        void getPolicyListsTestWithStatusChanges() {
                Policy p = Policy.builder()
                        .policyId(1121986)
                        .policyEffectiveDate(new Date())
                        .productId(1)
                        .policyStartDate(new Date())
                        .policyRenewalDate(new Date())
                        .policyStatus("Underwriting")
                        .policyAmount(BigDecimal.valueOf(100000))
                        .build();
                when(policyRemote.getPolicyLists(any())).thenReturn(Collections.singletonList(p));
                ApplicationRenewalPolicy renewalPolicy = ApplicationRenewalPolicy.builder()
                        .policyId(1121986)
                        .effectiveDate(new Date())
                        .status("Underwriting")
                        .build();
                when(policyMapper.searchRenewedPolicyList(any())).thenReturn(Collections.singletonList(renewalPolicy));
                when(policyMapper.searchPolicyList(any())).thenReturn(Collections.singletonList(renewalPolicy));
                CustomerPolicyListRequest request = CustomerPolicyListRequest.builder()
                        .customerId("1")
                        .build();
                List<HealthPolicyListResponse> resp = policyService.getPolicyLists(request);
                assertNotNull(resp);
        }

        @Test
        void updatePolicyStatusTest() {
                Policy p = Policy.builder()
                        .policyId(1121986)
                        .policyEffectiveDate(new Date())
                        .productId(1)
                        .policyStartDate(new Date())
                        .policyRenewalDate(new Date())
                        .policyStatus("Underwriting")
                        .policyAmount(BigDecimal.valueOf(100000))
                        .build();
                ApplicationRenewalPolicy renewalPolicy = ApplicationRenewalPolicy.builder()
                        .policyId(1121986)
                        .effectiveDate(new Date())
                        .status("Underwriting")
                        .build();
                policyService.updatePolicyStatus(List.of(renewalPolicy), p);
                assertTrue(true);
        }

        @Test
        void updatePolicyStatusTestWithPolicyNull() {
                ApplicationRenewalPolicy renewalPolicy = ApplicationRenewalPolicy.builder()
                        .policyId(1121986)
                        .effectiveDate(new Date())
                        .status("Underwriting")
                        .build();
                policyService.updatePolicyStatus(List.of(renewalPolicy), null);
                assertTrue(true);
        }

        @Test
        void updatePolicyStatusTestWithPolicyIdNull() {
                Policy p = Policy.builder()
                        .build();
                ApplicationRenewalPolicy renewalPolicy = ApplicationRenewalPolicy.builder()
                        .policyId(1121986)
                        .effectiveDate(new Date())
                        .status("Underwriting")
                        .build();
                policyService.updatePolicyStatus(List.of(renewalPolicy), p);
                assertTrue(true);
        }

}
