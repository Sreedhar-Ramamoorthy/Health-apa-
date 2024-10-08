package ke.co.apollo.health.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import ke.co.apollo.health.common.domain.model.*;
import ke.co.apollo.health.common.domain.model.request.PaymentCreateRequest;
import ke.co.apollo.health.common.domain.model.request.PaymentValidateRequest;
import ke.co.apollo.health.common.domain.model.request.TransactionCreateRequest;
import ke.co.apollo.health.common.domain.model.request.TransactionValidateRequest;
import ke.co.apollo.health.common.domain.model.response.TransactionCreateResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionValidateResponse;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.mapper.health.PaymentTransactionMapper;
import ke.co.apollo.health.remote.NotificationRemote;
import ke.co.apollo.health.remote.PaymentRemote;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.repository.HealthStepRepository;
import ke.co.apollo.health.service.CustomerService;
import ke.co.apollo.health.service.IntermediaryService;
import ke.co.apollo.health.service.PolicyService;
import ke.co.apollo.health.service.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ke.co.apollo.health.repository.PolicyPremiumRepository;
import ke.co.apollo.health.domain.entity.PolicyPremiumEntity;
import ke.co.apollo.health.domain.PolicyBenefitPremium;
import ke.co.apollo.health.domain.request.QuoteBaseRequest;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;

import java.util.Optional;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

class PremiumServiceImplTest {

    @InjectMocks
    PremiumServiceImpl premiumService;

    @Mock
    QuoteService quoteService;;

    @Mock
    PolicyPremiumRepository policyPremiumRepository;

   

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

   

    @Test
     void getQuotePremiumTest(){

        Optional<List<PolicyPremiumEntity>> quotePremiumDetail = Optional.of(Collections.singletonList(PolicyPremiumEntity.builder().build()));

        Mockito.when(quoteService.getQuote(any(),any(),any())).thenReturn(Quote.builder().build());
        Mockito.when(policyPremiumRepository.findAllByQuoteId(any())).thenReturn(quotePremiumDetail);
       
        List<PolicyPremiumEntity> resp = premiumService.getQuotePremium(QuoteBaseRequest.builder()
                                .quoteId("123")
                                .customerId("123")
                                .agentId("123")
                                .build());  //NO SONAR
        assertNotNull(resp);
    }


    @Test
     void getPolicyPremiumTest(){
        Optional<List<PolicyPremiumEntity>> quotePremiumEntity = Optional.of(Collections.singletonList(PolicyPremiumEntity.builder().build()));
        Mockito.when(policyPremiumRepository.findAllByPolicyIdAndEffectiveDate(any(),any())).thenReturn(quotePremiumEntity);
        PolicyIdRequest request = PolicyIdRequest.builder().build();
        List<PolicyPremiumEntity> resp = premiumService.getPolicyPremium(request);
        assertNotNull(resp);
        }


    @Test
     void getPolicyPremiumEmptyTest(){
        Optional<List<PolicyPremiumEntity>> quotePremiumEntity = Optional.empty();
        Mockito.when(policyPremiumRepository.findAllByPolicyIdAndEffectiveDate(any(),any())).thenReturn(quotePremiumEntity);
        PolicyIdRequest request = PolicyIdRequest.builder().build();
        List<PolicyPremiumEntity> resp = premiumService.getPolicyPremium(request);
        assertNotNull(resp);
        }

    // @Test
    //  void recordQuoteBeneficiaryPremiumTest(){
    //     // Optional<List<PolicyPremiumEntity>> quotePremiumEntity = Optional.of(Collections.singletonList(PolicyPremiumEntity.builder().build()));
    //     Mockito.when(policyPremiumRepository.findAllByQuoteId(any(),any())).thenReturn(quotePremiumEntity);
    //     // PolicyIdRequest request = PolicyIdRequest.builder().build();
    //     // List<PolicyPremiumEntity> resp = premiumService.getPolicyPremium(request);
    //     // assertNotNull(resp);
    //     }

}
