package ke.co.apollo.health.policy.remote;

import ke.co.apollo.health.common.domain.model.remote.AddBenefitsToPolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyResponse;
import ke.co.apollo.health.common.domain.model.request.AddBeneficiariesToPolicyRequest;
import ke.co.apollo.health.common.domain.model.request.AddBusinessSourceToIndividualPolicyRequest;
import ke.co.apollo.health.common.domain.model.request.AddIndividualPolicyBeneficiaryUWQuestionsRequest;
import ke.co.apollo.health.common.domain.model.request.PolicyAdditionalInfoRequest;
import ke.co.apollo.health.common.domain.model.response.ASAPIResponse;

public interface PolicyRemote {

  CreatePolicyResponse createPolicy(CreatePolicyRequest createPolicyRequest);

  ApiResponse addBenefitsToPolicy(AddBenefitsToPolicyRequest request);

  ASAPIResponse addBeneficiaryToPolicy(AddBeneficiariesToPolicyRequest request);
  ASAPIResponse addAgentBranchDetailsToPolicy(PolicyAdditionalInfoRequest request);

  ApiResponse addBusinessSourceToIndividualPolicy(
      AddBusinessSourceToIndividualPolicyRequest request);

  ApiResponse addIndividualPolicyBeneficiaryUWQuestions(
      AddIndividualPolicyBeneficiaryUWQuestionsRequest request);
}
