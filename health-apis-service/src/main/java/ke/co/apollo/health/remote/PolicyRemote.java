package ke.co.apollo.health.remote;

import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.Claim;
import ke.co.apollo.health.common.domain.model.DependantDetail;
import ke.co.apollo.health.common.domain.model.Policy;
import ke.co.apollo.health.common.domain.model.PolicyAdjustment;
import ke.co.apollo.health.common.domain.model.PolicyDetail;
import ke.co.apollo.health.common.domain.model.remote.AddBenefitsToPolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyResponse;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.AddBeneficiariesToPolicyResponse;
import ke.co.apollo.health.common.domain.model.response.BenefitBeanResponse;
import ke.co.apollo.health.common.domain.model.response.PolicyOverComingResponse;
import ke.co.apollo.health.domain.request.PolicyAdditionalInfoRequest;
import ke.co.apollo.health.domain.response.ActisurePolicyBranchDetailsResponse;
import ke.co.apollo.health.domain.response.AgentBranchDetailsResponse;

import java.util.List;
import java.util.Map;

public interface PolicyRemote {

  CreatePolicyResponse createPolicy(CreatePolicyRequest createPolicyRequest);

  ApiResponse addBenefitsToPolicy(AddBenefitsToPolicyRequest request);

  ApiResponse addBeneficiaryUWQuestions(AddIndividualPolicyBeneficiaryUWQuestionsRequest request);

  Map<Long, List<Policy>> getBatchPolicyLists(CustomerEntityIdsRequest request);

  Map<Integer, List<Policy>> getBatchPolicyListsById(PolicyIdsRequest request);

  List<Policy> getPolicyLists(CustomerPolicyListRequest request);

  List<Policy> getPolicyHistoryLists(PolicyIdRequest request);

  int getPolicyRenewalCount(PolicyIdRequest request);

  PolicyDetail getPolicyDetail(PolicyNumberRequest request);

  List<AddBeneficiariesToPolicyResponse> addBeneficiariesToPolicy(
      List<AddBeneficiariesToPolicyRequest> request);

  ApiResponse addBusinessSourceToIndividualPolicy(
      AddBusinessSourceToIndividualPolicyRequest request);

  List<Claim> getPolicyClaims(Integer policyId);

  List<DependantDetail> getPolicyBeneficiary(PolicyIdRequest request);

  BenefitBeanResponse getPolicyBenefit(GetBenefitBreakDownRequest request);

  Benefit getPolicyBenefit(PolicyIdRequest request);

  PolicyAdjustment getPolicyAdjustment(PolicyIdRequest request);

  PolicyOverComingResponse comingPolicyList(ComingPolicyListRequest request);
  AgentBranchDetailsResponse getAgentBranchDetails(AgentDetailsRequest entityId);
  ActisurePolicyBranchDetailsResponse addPolicyAdditionalBranchDetails(PolicyAdditionalInfoRequest request);
}
