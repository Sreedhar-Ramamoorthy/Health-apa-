package ke.co.apollo.health.policy.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.Claim;
import ke.co.apollo.health.common.domain.model.Policy;
import ke.co.apollo.health.common.domain.model.PolicyAdjustment;
import ke.co.apollo.health.common.domain.model.PolicyDetail;
import ke.co.apollo.health.common.domain.model.remote.AddBenefitsToPolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyResponse;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.ASAPIResponse;
import ke.co.apollo.health.common.domain.model.response.AddBeneficiariesToPolicyResponse;
import ke.co.apollo.health.policy.model.AgentBranchDetails;
import ke.co.apollo.health.policy.model.Commission;

public interface PolicyService {

  CreatePolicyResponse createPolicy(CreatePolicyRequest createPolicyRequest);

  ASAPIResponse addPolicyBranchDetails(PolicyAdditionalInfoRequest request);
  ApiResponse addBenefitsToPolicy(AddBenefitsToPolicyRequest request);

  ApiResponse addIndividualPolicyBeneficiaryUWQuestions(
      AddIndividualPolicyBeneficiaryUWQuestionsRequest request);

  Map<Long, List<Policy>> getBatchPolicyLists(List<Integer> request);

  List<Policy> getPolicyLists(CustomerPolicyListRequest request);
  List<ke.co.apollo.health.policy.model.Policy> getIntermediaryPoliciesList(IntermediaryPolicyDetailsRequest request);

  List<Policy> getPolicyHistoryLists(PolicyIdRequest request);

  int getPolicyRenewalCount(PolicyIdRequest request);

  List<Claim> getPolicyClaims(Integer policyId);
  List<Commission> getCommissions(Integer agentId);

  Benefit getPolicyBenefit(PolicyIdRequest request);

  PolicyAdjustment getPolicyAdjustment(PolicyIdRequest request);
  AgentBranchDetails getAgentBranchDetails(Integer entityId);

  List<AddBeneficiariesToPolicyResponse> addBeneficiariesToPolicy(
      List<AddBeneficiariesToPolicyRequest> request);

  ApiResponse addBusinessSourceToIndividualPolicy(
      AddBusinessSourceToIndividualPolicyRequest request);

  PolicyDetail getPolicyDetailByMapper(String policyNumber, Date effectiveDate);

  Map<Integer, List<Policy>> getBatchPolicyListsById(List<Integer> request);


}
