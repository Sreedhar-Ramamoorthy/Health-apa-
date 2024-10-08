package ke.co.apollo.health.policy.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import ke.co.apollo.health.common.domain.model.request.IntermediaryPolicyDetailsRequest;
import ke.co.apollo.health.common.domain.model.request.PolicyAdditionalInfoRequest;
import ke.co.apollo.health.common.domain.model.response.ASAPIResponse;
import ke.co.apollo.health.policy.mapper.hms.PolicyHMSMapper;
import ke.co.apollo.health.policy.model.AgentBranchDetails;
import ke.co.apollo.health.policy.remote.PolicyRemote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import ke.co.apollo.health.policy.mapper.hms.CommissionHMSMapper;
import ke.co.apollo.health.policy.model.Commission;

class PolicyServiceImplTest {

    @Mock
    CommissionHMSMapper commissionHMSMapper;

    @Mock
    PolicyRemote policyRemote;

    @Mock
    PolicyHMSMapper policyHMSMapper;

    @InjectMocks
    PolicyServiceImpl policyService;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    void testGetCommissions() {
        List<Commission> commissionList = new ArrayList<Commission>();
        when(commissionHMSMapper.getCommissions(anyInt()))
                .thenReturn(commissionList);
        List<Commission> comms = policyService.getCommissions(1);
        assertNotNull(comms);
    }

    @Test
    void testAddPolicyBranchDetails(){
        when(policyRemote.addAgentBranchDetailsToPolicy(any())).thenReturn(ASAPIResponse.builder().build());
        ASAPIResponse response = policyService.addPolicyBranchDetails(PolicyAdditionalInfoRequest.builder().build());
        assertNotNull(response);
    }

    @Test
    void testGetAgentBranchDetails(){
        when(policyHMSMapper.getAgentBranchDetails(any())).thenReturn(AgentBranchDetails.builder().build());
        AgentBranchDetails response = policyService.getAgentBranchDetails(8);
        assertNotNull(response);
    }

    @Test
    void testGetIntermediaryPolicyDetails(){
        List<ke.co.apollo.health.policy.model.Policy> policies = new ArrayList<>();
        when(policyHMSMapper.getPolicyListByIntermediary(any())).thenReturn(policies);
        List<ke.co.apollo.health.policy.model.Policy> response = policyService.getIntermediaryPoliciesList(IntermediaryPolicyDetailsRequest.builder().build());
        assertEquals(response.size(), 0);
    }
}
