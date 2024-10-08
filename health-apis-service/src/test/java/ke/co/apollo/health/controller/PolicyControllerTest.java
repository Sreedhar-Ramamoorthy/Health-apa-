package ke.co.apollo.health.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.Date;

import ke.co.apollo.health.common.domain.model.request.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import ke.co.apollo.health.common.domain.model.ApplicationRenewalPolicy;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.response.ApplicationRenewalPolicyListResponse;
import ke.co.apollo.health.common.domain.model.response.PolicyOverComingResponseDto;
import ke.co.apollo.health.common.domain.model.response.PolicyRenewalResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.domain.request.ApplicationPolicyListSearchRequest;
import ke.co.apollo.health.domain.request.CustomerIdRequest;
import ke.co.apollo.health.domain.request.PolicyComplaintRequest;
import ke.co.apollo.health.domain.response.HealthPolicyListResponse;
import ke.co.apollo.health.service.ComplaintService;
import ke.co.apollo.health.service.PolicyService;

class PolicyControllerTest {

    @InjectMocks
    PolicyController policyController;

    @Mock
    PolicyService policyService;

    @Mock
    ComplaintService complaintService;

    @BeforeEach
    void setUpMocks(){
        initMocks(this);
    }

    @Test
    void testSubmitComplaint(){
        when(complaintService.submitComplaint(any())).thenReturn(true);
        ResponseEntity<DataWrapper> wrapper = policyController.submitComplaint(PolicyComplaintRequest.builder().build());
        assertNotNull(wrapper);
        }


    @Test
    void testCreateCustomerPolicyCache(){
        when(policyService.createCustomerPolicyCache(any())).thenReturn(true);
        ResponseEntity<DataWrapper> wrapper = policyController.createCustomerPolicyCache(CustomerIdRequest.builder().build());
        assertNotNull(wrapper);
        }
        
    @Test
    void testUpdatePolicyRenewalBalance(){
        when(policyService.updatePolicyRenewalBalance(any(),any(),any())).thenReturn(true);
        ResponseEntity<DataWrapper> wrapper = policyController.updatePolicyRenewalBalance(PolicyRenewalBalanceRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void testArchiveApplicationRenewalPolicy(){
        when(policyService.archiveApplicationRenewalPolicy(any())).thenReturn(true);
        ResponseEntity<DataWrapper> wrapper = policyController.archiveApplicationRenewalPolicy(PolicyNumberRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void testRenewalPolicy(){
        when(policyService.renewalPolicy(any())).thenReturn(PolicyRenewalResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = policyController.renewalPolicy(PolicyRenewalRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void testGetApplicationRenewalPolicy(){
        when(policyService.getApplicationRenewalPolicy(any())).thenReturn(ApplicationRenewalPolicy.builder().build());
        ResponseEntity<DataWrapper> wrapper = policyController.getApplicationRenewalPolicy(PolicyNumberRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void testComingPolicyList(){
        when(policyService.policyUpdateDetails(any())).thenReturn(PolicyOverComingResponseDto.builder().build());
        ResponseEntity<DataWrapper> wrapper = policyController.comingPolicyList(ComingPolicyListRequest.builder().build());
        assertNotNull(wrapper);
        }
    @Test
    void comingPolicyListInExcel(){
        byte[] bytes = new byte[0];
        ComingPolicyListRequestInExcel excel = ComingPolicyListRequestInExcel.builder()
                .startDate(new Date())
                .endDate(new Date())
                .build();
        when(policyService.comingPolicyListInExcel(any())).thenReturn(bytes);
        ResponseEntity<DataWrapper> wrapper = policyController.comingPolicyListInExcel(excel);
        assertNotNull(wrapper);
    }

    @Test
    void getCustomerPolicyListTest(){
        when(policyService.getCustomerPolicyList(any(CustomerPolicyListRequest.class))).thenReturn(Collections.singletonList(HealthPolicyListResponse.builder().build()));
        ResponseEntity<DataWrapper> wrapper = policyController.getCustomerPolicyList(CustomerPolicyListRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getCustomerPolicyListEmptyListTest(){
        when(policyService.getCustomerPolicyList(any(CustomerPolicyListRequest.class))).thenReturn(Collections.emptyList());
        ResponseEntity<DataWrapper> wrapper = policyController.getCustomerPolicyList(CustomerPolicyListRequest.builder().build());
        assertNotNull(wrapper);
        }


    @Test
    void getCustomerPolicyList2Test(){
        when(policyService.getCustomerPolicyList(any(EntityPolicyListRequest.class))).thenReturn(Collections.singletonList(HealthPolicyListResponse.builder().build()));
        ResponseEntity<DataWrapper> wrapper = policyController.getCustomerPolicyList(EntityPolicyListRequest.builder().build());
        assertNotNull(wrapper);
        }


    @Test
    void getCustomerPolicyListEmptyList2Test(){
        when(policyService.getCustomerPolicyList(any(EntityPolicyListRequest.class))).thenReturn(Collections.emptyList());
        ResponseEntity<DataWrapper> wrapper = policyController.getCustomerPolicyList(EntityPolicyListRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void searchApplicationRenewalPolicyListTest(){
        when(policyService.searchApplicationRenewalPolicyList(any())).thenReturn(ApplicationRenewalPolicyListResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = policyController.searchApplicationRenewalPolicyList(ApplicationPolicyListSearchRequest.builder().build());
        assertNotNull(wrapper);
        }


    @Test
    void searchApplicationRenewalPolicyListNullResponseTest(){
        when(policyService.searchApplicationRenewalPolicyList(any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = policyController.searchApplicationRenewalPolicyList(ApplicationPolicyListSearchRequest.builder().build());
        assertNotNull(wrapper);
        }

}
