package ke.co.apollo.health.policy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ke.co.apollo.health.common.domain.model.PolicyOverComing;
import ke.co.apollo.health.common.domain.model.PolicyOverComingSize;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.ASAPIResponse;
import ke.co.apollo.health.common.domain.model.response.PolicyOverComingResponse;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.policy.common.CommonObjects;
import ke.co.apollo.health.policy.mapper.hms.PolicyHMSMapper;
import ke.co.apollo.health.policy.model.AgentBranchDetails;
import ke.co.apollo.health.policy.model.Policy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.policy.model.Commission;
import ke.co.apollo.health.policy.service.impl.PolicyServiceImpl;

class PolicyControllerTest {
    @Mock
    private PolicyServiceImpl policyService;

    @Mock
    private PolicyHMSMapper policyHMSMapper;

    @InjectMocks
    private PolicyController policyController;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    void testGetCommissionsEmptyList() {

        Commission com1 = new Commission().builder()
                .countPaid(10L)
                .countDueProcessed(10L)
                .countDueNotProcessed(10L)
                .totalPaid(20F)
                .totalDueProcessed(20F)
                .totalDueNotProcessed(20F)
                .build();

        List<Commission> commissionList = new ArrayList<Commission>();
        commissionList.add(com1);

        when(policyService.getCommissions(anyInt()))
                .thenReturn(commissionList);

        IntermediaryCommissionResponse resp = policyController.getCommissions(1);

        assertNotNull(resp);
    }


    @Test
    void testGetCommissions() {

        Commission com1 = new Commission().builder()
                .countPaid(10L)
                .countDueProcessed(10L)
                .countDueNotProcessed(10L)
                .totalPaid(20F)
                .totalDueProcessed(20F)
                .totalDueNotProcessed(20F)
                .build();

        List<Commission> commissionList = new ArrayList<Commission>();
        commissionList.add(com1);

        when(policyService.getCommissions(anyInt()))
                .thenReturn(commissionList);

        IntermediaryCommissionResponse resp = policyController.getCommissions(1);

        assertNotNull(resp);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    void testGetBranchDetails(boolean status){
        ASAPIResponse re = null;
        if(status){
            re = ASAPIResponse.builder().errorMessage("message").success(true).build();
        }
        when(policyService.addPolicyBranchDetails(any())).thenReturn(re);
        ResponseEntity<DataWrapper> wr = policyController.addPolicyBranchDetails(PolicyAdditionalInfoRequest.builder().policyId(89).build());
        assertNotNull(wr);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    void addPolicyBranchDetails(boolean status){
        AgentBranchDetails re = null;
        if(status){
            re = AgentBranchDetails.builder().branch("branch").build();
        }
        when(policyService.getAgentBranchDetails(anyInt())).thenReturn(re);
        ResponseEntity<DataWrapper> wr = policyController.getBranchDetails(AgentDetailsRequest.builder().entityId(90).build());
        assertNotNull(wr);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    void addIntermediaryPolicyDetails(boolean status){
        ke.co.apollo.health.policy.model.Policy p = new Policy();
        List<ke.co.apollo.health.policy.model.Policy> policies = new ArrayList<>();
        if(status){
            p.setPolicyAmount(BigDecimal.ZERO);
            policies.add(p);
        }
        when(policyService.getIntermediaryPoliciesList(any())).thenReturn(policies);
        IntermediaryPolicyDetailsRequest request = IntermediaryPolicyDetailsRequest.builder().build();
        ResponseEntity<DataWrapper> wr = policyController.getIntermediaryPolicies(request);
        assertNotNull(wr);
    }

    @Test
    void testGetComingPolicyListWhenPolicyHMSMapperReturnsAtLeastOnePolicy() {
        // Arrange
        ComingPolicyListRequest comingPolicyListRequest = CommonObjects.getComingPolicyListRequest;
        List<PolicyOverComing> policyOverComingsList = new ArrayList<>();
        policyOverComingsList.add(CommonObjects.getPolicyOverComing);
        int policyListSize = policyOverComingsList.size();
        PolicyOverComingSize policyOverComingSize = PolicyOverComingSize.builder()
                .total(policyListSize)
                .build();

        when(policyHMSMapper.getComingPolicyList()).thenReturn(policyOverComingsList);
        when(policyHMSMapper.getComingPolicyListSize()).thenReturn(policyOverComingSize);

        ResponseEntity<DataWrapper> expectedResponse = ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));

        //Act
        ResponseEntity<DataWrapper> actualResponse = policyController.getComingPolicyList(comingPolicyListRequest);

        //Assert
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    void testGetComingPolicyListWhenPolicyHMSMapperDoesNotFindAnyPolicy() {
        //Arrange
        DataWrapper dataWrapper = new DataWrapper();
        ComingPolicyListRequest comingPolicyListRequest = CommonObjects.getComingPolicyListRequest;
        List<PolicyOverComing> policyOverComingsList = new ArrayList<>();
        int policyListSize = policyOverComingsList.size();
        PolicyOverComingSize policyOverComingSize = PolicyOverComingSize.builder()
                .total(policyListSize)
                .build();

        when(policyHMSMapper.getComingPolicyList()).thenReturn(policyOverComingsList);
        when(policyHMSMapper.getComingPolicyListSize()).thenReturn(policyOverComingSize);

        ResponseEntity<DataWrapper> expectedResponse = ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));

        //Act
        ResponseEntity<DataWrapper> actualResponse = policyController.getComingPolicyList(comingPolicyListRequest);

        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());




    }
}
