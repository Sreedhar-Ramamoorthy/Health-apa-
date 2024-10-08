package ke.co.apollo.health.policy.controller;

import java.math.BigDecimal;
import java.util.List;

import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.ASAPIResponse;
import ke.co.apollo.health.policy.model.AgentBranchDetails;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.Claim;
import ke.co.apollo.health.common.domain.model.Policy;
import ke.co.apollo.health.common.domain.model.PolicyAdjustment;
import ke.co.apollo.health.common.domain.model.PolicyDetail;
import ke.co.apollo.health.common.domain.model.PolicyOverComing;
import ke.co.apollo.health.common.domain.model.PolicyOverComingSize;
import ke.co.apollo.health.common.domain.model.remote.AddBenefitsToPolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyResponse;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.response.AddBeneficiariesToPolicyResponse;
import ke.co.apollo.health.common.domain.model.response.BenefitBeanResponse;
import ke.co.apollo.health.common.domain.model.response.PolicyOverComingResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.policy.mapper.hms.PolicyHMSMapper;
import ke.co.apollo.health.policy.model.Commission;
import ke.co.apollo.health.policy.service.ClaimsService;
import ke.co.apollo.health.policy.service.PolicyService;

@RestController
@RequestMapping("/policy")
@Api(tags = "Health Policy Integration API")
public class PolicyController {
  private Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  PolicyService policyService;

  @Autowired
  ClaimsService claimsService;

  @Autowired
  PolicyHMSMapper policyHMSMapper;

  @GetMapping("/home")
  public String home() {
    return "hello health policy";
  }

  @PostMapping("/add")
  @ApiOperation("Create policy")
  public ResponseEntity<DataWrapper> createPolicy(
      @Validated @RequestBody CreatePolicyRequest createPolicyRequest) {
    CreatePolicyResponse createPolicyResponse = policyService.createPolicy(createPolicyRequest);
    if (createPolicyResponse == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(createPolicyResponse));
  }

  @PostMapping("/benefit")
  @ApiOperation("Add benefits to policy")
  public ResponseEntity<DataWrapper> addBenefitsToPolicy(
      @Validated @RequestBody AddBenefitsToPolicyRequest addBenefitsToPolicyRequest) {
    ApiResponse apiResponse = policyService.addBenefitsToPolicy(addBenefitsToPolicyRequest);
    if (apiResponse == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(apiResponse));
  }

  @PostMapping("/questions")
  @ApiOperation("Add beneficiary question to policy")
  public ResponseEntity<DataWrapper> addIndividualPolicyBeneficiaryUWQuestions(
      @Validated @RequestBody AddIndividualPolicyBeneficiaryUWQuestionsRequest request) {
    ApiResponse apiResponse = policyService.addIndividualPolicyBeneficiaryUWQuestions(request);
    if (apiResponse == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(apiResponse));
  }

  @PostMapping("/list")
  @ApiOperation("Get policy list by customer id")
  public ResponseEntity<DataWrapper> getPolicyLists(
      @Validated @RequestBody CustomerPolicyListRequest request) {
    List<Policy> policies = policyService.getPolicyLists(request);
    if (CollectionUtils.isEmpty(policies)) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(policies));
  }

  @PostMapping("/batchlist")
  @ApiOperation("Batch get policy list by entity id")
  public ResponseEntity<DataWrapper> getPolicyLists(
      @Validated @RequestBody CustomerEntityIdsRequest request) {
    return ResponseEntity
        .ok(new DataWrapper(policyService.getBatchPolicyLists(request.getEntityIds())));
  }

  @PostMapping("/activedlist")
  @ApiOperation("Batch get policy list by policy id")
  public ResponseEntity<DataWrapper> getPolicyListsById(
      @Validated @RequestBody PolicyIdsRequest request) {
    return ResponseEntity
        .ok(new DataWrapper(policyService.getBatchPolicyListsById(request.getIds())));
  }

  @PostMapping("/history")
  @ApiOperation("Get policy history list")
  public ResponseEntity<DataWrapper> getPolicyHistoryLists(
      @Validated @RequestBody PolicyIdRequest request) {
    List<Policy> policies = policyService.getPolicyHistoryLists(request);
    if (CollectionUtils.isEmpty(policies)) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(policies));
  }

  @PostMapping("/intermediaryPolicies")
  @ApiOperation("Get intermediary policies ")
  public ResponseEntity<DataWrapper> getIntermediaryPolicies(
          @Validated @RequestBody IntermediaryPolicyDetailsRequest request) {
    List<ke.co.apollo.health.policy.model.Policy> policies = policyService.getIntermediaryPoliciesList(request);
    if (CollectionUtils.isEmpty(policies)) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(policies));
  }

  @PostMapping("/renewal/count")
  @ApiOperation("Get policy renewal count")
  public ResponseEntity<DataWrapper> getPolicyRenewalCount(
      @Validated @RequestBody PolicyIdRequest request) {
    int count = policyService.getPolicyRenewalCount(request);
    return ResponseEntity.ok(new DataWrapper(count));
  }

  @PostMapping("/beneficiary")
  @ApiOperation("Add beneficiaries to an individual policy")
  public ResponseEntity<DataWrapper> addBeneficiariesToPolicy(
      @Validated @RequestBody List<AddBeneficiariesToPolicyRequest> request) {
    List<AddBeneficiariesToPolicyResponse> result = policyService.addBeneficiariesToPolicy(request);
    if (result == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(result));
  }

  @PostMapping("/businesssource")
  @ApiOperation("Add business source to an individual policy")
  public ResponseEntity<DataWrapper> addBeneficiariesToPolicy(
      @Validated @RequestBody AddBusinessSourceToIndividualPolicyRequest request) {
    ApiResponse apiResponse = policyService.addBusinessSourceToIndividualPolicy(request);
    if (apiResponse == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(apiResponse));
  }

  @PostMapping("/detail/mapper")
  @ApiOperation("Get policy detail from mapper")
  public ResponseEntity<DataWrapper> getPolicyDetailByMapper(
      @Validated @RequestBody PolicyNumberRequest request) {
    PolicyDetail policy = policyService
        .getPolicyDetailByMapper(request.getPolicyNumber(), request.getEffectiveDate());
    if (policy == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(policy));
  }

  @PostMapping("/benefit/breakdown")
  @ApiOperation("Get benefit breakdown")
  public ResponseEntity<DataWrapper> getBenefit(
      @Validated @RequestBody GetBenefitBreakDownRequest request) {

    BenefitBeanResponse response = claimsService.getBenefitBreakDown(request);
    if (response == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/claims")
  @ApiOperation("Get policy claims list by policy id")
  public ResponseEntity<DataWrapper> getPolicyClaims(
      @Validated @RequestBody ke.co.apollo.health.common.domain.model.request.PolicyIdRequest request) {
    List<Claim> claims = policyService.getPolicyClaims(request.getPolicyId());
    if (CollectionUtils.isEmpty(claims)) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(claims));
  }

  @GetMapping("/commissions")
  @ApiOperation("Get commisions")
  public IntermediaryCommissionResponse getCommissions(@RequestParam("agentId") Integer agentId) {
    List<Commission> commsList = policyService.getCommissions(agentId);
    Commission comm = commsList.get(0);

    IntermediaryCommissionResponse resp = new IntermediaryCommissionResponse();
      resp.setCountPaid(comm.getCountPaid());
      resp.setCountDueProcessed(comm.getCountDueProcessed());
      resp.setCountDueNotProcessed(comm.getCountDueNotProcessed());
      resp.setTotalPaid(BigDecimal.valueOf(comm.getTotalPaid()));
      resp.setTotalDueProcessed(BigDecimal.valueOf(comm.getTotalDueProcessed()));
      resp.setTotalDueNotProcessed(BigDecimal.valueOf(comm.getTotalDueNotProcessed()));

    return resp;
  }

  @PostMapping("/benefit/all")
  @ApiOperation("Get All Benefit")
  public ResponseEntity<DataWrapper> getPolicyBenefit(
      @Validated @RequestBody PolicyIdRequest request) {
    Benefit response = policyService.getPolicyBenefit(request);
    if (response == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/adjustment")
  @ApiOperation("Get Policy Adjustment")
  public ResponseEntity<DataWrapper> getPolicyAdjustment(
      @Validated @RequestBody PolicyIdRequest request) {
    PolicyAdjustment response = policyService.getPolicyAdjustment(request);
    if (response == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/agent/branchDetails")
  @ApiOperation("Get Policy Adjustment")
  public ResponseEntity<DataWrapper> getBranchDetails(
  @Validated @RequestBody AgentDetailsRequest agentDetails
  ) {
    AgentBranchDetails response = policyService.getAgentBranchDetails(agentDetails.getEntityId());
    if (response == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/additionalInfo/branchDetails")
  @ApiOperation("Get Policy Adjustment")
  public ResponseEntity<DataWrapper> addPolicyBranchDetails(
  @Validated @RequestBody PolicyAdditionalInfoRequest agentDetails
  ) {
    ASAPIResponse response = policyService.addPolicyBranchDetails(agentDetails);
    if (response == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/coming/PolicyList")
  @ApiOperation("PolicyList")
  public ResponseEntity<DataWrapper> getComingPolicyList(@Validated @RequestBody ComingPolicyListRequest request) {
    List<PolicyOverComing> policies = policyHMSMapper.getComingPolicyList();
    logger.debug("policyOverComingSize :{}", CollectionUtils.size(policies));
    PolicyOverComingSize policyOverComingSize = policyHMSMapper.getComingPolicyListSize();

    logger.debug("PolicyOverComingResponse total size:{}", policyOverComingSize);

    PolicyOverComingResponse response = PolicyOverComingResponse.builder()
                                                                .policyOverComingList(policies)
                                                                .total(policyOverComingSize.getTotal())
                                                                .build();


    if (CollectionUtils.isEmpty(policies)) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(response));
  }

}
