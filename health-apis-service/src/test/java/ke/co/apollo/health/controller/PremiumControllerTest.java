package ke.co.apollo.health.controller;

import ke.co.apollo.health.service.PremiumService;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.domain.PolicyBenefitPremium;
import ke.co.apollo.health.domain.entity.LocationEntity;
import ke.co.apollo.health.domain.response.HospitalInitialResponse;
import ke.co.apollo.health.service.HospitalService;
import ke.co.apollo.health.service.PremiumService;
import ke.co.apollo.health.domain.entity.PolicyPremiumEntity;
import ke.co.apollo.health.domain.request.QuoteBaseRequest;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;



class PremiumControllerTest {

    @InjectMocks
    PremiumController premiumController;

    @Mock
    PremiumService premiumService;

    @BeforeEach
    void setUpMocks(){
        initMocks(this);
        }

    @Test
    void getQuotePremiumTest() {
        when(premiumService.getQuotePremium(any())).thenReturn(Collections.singletonList(PolicyPremiumEntity.builder().build()));
        ResponseEntity<DataWrapper> wrapper = premiumController.getQuotePremium(QuoteBaseRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getPolicyPremiumTest() {
        when(premiumService.getPolicyPremium(any())).thenReturn(Collections.singletonList(PolicyPremiumEntity.builder().build()));
        ResponseEntity<DataWrapper> wrapper = premiumController.getPolicyPremium(PolicyIdRequest.builder().build());
        assertNotNull(wrapper);
        }
    
    @Test
    void getQuoteBenefitPremiumTest() {
        when(premiumService.getQuoteBenefitPremium(any())).thenReturn(Collections.singletonList(PolicyBenefitPremium.builder().build()));
        ResponseEntity<DataWrapper> wrapper = premiumController.getQuoteBenefitPremium(QuoteBaseRequest.builder().build());
        assertNotNull(wrapper);
        }
    
    @Test
    void getPolicyBenefitPremiumTest() {
        when(premiumService.getPolicyBenefitPremium(any())).thenReturn(Collections.singletonList(PolicyBenefitPremium.builder().build()));
        ResponseEntity<DataWrapper> wrapper = premiumController.getPolicyBenefitPremium(PolicyIdRequest.builder().build());
        assertNotNull(wrapper);
        }
}
