package ke.co.apollo.health.remote.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
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
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.common.utils.HttpUtils;
import ke.co.apollo.health.common.utils.JsonUtils;
import ke.co.apollo.health.common.utils.MappingUtils;
import ke.co.apollo.health.domain.request.PolicyAdditionalInfoRequest;
import ke.co.apollo.health.domain.response.ActisurePolicyBranchDetailsResponse;
import ke.co.apollo.health.domain.response.AgentBranchDetailsResponse;
import ke.co.apollo.health.remote.AbstractRemote;
import ke.co.apollo.health.remote.PolicyRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PolicyRemoteImpl extends AbstractRemote implements PolicyRemote {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${apa.health-apis-policy.url}")
  String healthApisPolicyBaseUrl;

  @Override
  public CreatePolicyResponse createPolicy(CreatePolicyRequest createPolicyRequest) {
    CreatePolicyResponse createPolicyResponse = null;
    String url = healthApisPolicyBaseUrl + "/policy/add";
    logger.debug(url);
    String input = JsonUtils.objectToJson(createPolicyRequest);
    logger.debug("createPolicyRequest: \n {}", input);
    Map<String, Object> map = MappingUtils.beanToMap(createPolicyRequest);
    logger.debug("createPolicyRequest map: {}", map);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
    try {
      ResponseEntity<DataWrapper> responseEntity = restTemplate
          .postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        createPolicyResponse = objectMapper
            .convertValue(body.getData(), new TypeReference<CreatePolicyResponse>() {
            });
        logger.debug("createPolicyResponse: {}", createPolicyResponse);
      } else {
        String errorMsg = Optional.ofNullable(body).map(DataWrapper::getMessage)
            .orElse("body is null");
        throw new BusinessException(errorMsg);
      }

    } catch (Exception e) {
      logger.error("create policy exception: {}", e.getMessage());
    }
    return createPolicyResponse;
  }

  @Override
  public ApiResponse addBenefitsToPolicy(AddBenefitsToPolicyRequest request) {
    ApiResponse response = null;
    String url = healthApisPolicyBaseUrl + "/policy/benefit";
    logger.debug(url);
    String input = JsonUtils.objectToJson(request);
    logger.debug("AddBenefitsToPolicyRequest: \n {}", input);
    Map<String, Object> map = MappingUtils.beanToMap(request);
    logger.debug("map: {}", map);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
    response = getApiResponse(url, entity, response, "AddBenefitsToPolicyResponse: {}",
        "add benefits to policy exception: {}");
    return response;
  }

  @Override
  public ApiResponse addBeneficiaryUWQuestions(AddIndividualPolicyBeneficiaryUWQuestionsRequest request) {
    ApiResponse response = null;
    String url = healthApisPolicyBaseUrl + "/policy/questions";
    String input = JsonUtils.objectToJson(request);
    logger.debug("url: {}, AddIndividualPolicyBeneficiaryUWQuestionsRequest: \n {}",url, input);
    Map<String, Object> map = MappingUtils.beanToMap(request);
    logger.debug("question map: {}", map);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
    response = getApiResponse(url, entity, response, "AddIndividualPolicyBeneficiaryUWQuestionsResponse: {}",
        "add beneficiary questions to policy exception: {}");
    return response;
  }

  @Override
  public Map<Long, List<Policy>> getBatchPolicyLists(CustomerEntityIdsRequest request) {
    String url = healthApisPolicyBaseUrl + "/policy/batchlist";
    return super.postForDataWrapper(new TypeReference<Map<Long, List<Policy>>>() {}, url, request, "get batch policy list");
  }

  @Override
  public Map<Integer, List<Policy>> getBatchPolicyListsById(PolicyIdsRequest request) {
    String url = healthApisPolicyBaseUrl + "/policy/activedlist";
    return super.postForDataWrapper(new TypeReference<Map<Integer, List<Policy>>>() {
    }, url, request, "get actived policy list");
  }

  @Override
  public List<Policy> getPolicyLists(CustomerPolicyListRequest request) {
    List<Policy> policyList = new ArrayList<>();
    String url = healthApisPolicyBaseUrl + "/policy/list";
    logger.debug(url);
    logger.debug("CustomerPolicyListRequest: {}", request);
    Map<String, Object> map = MappingUtils.beanToMap(request);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
    try {
      ResponseEntity<DataWrapper> responseEntity = restTemplate
          .postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        policyList = objectMapper
            .convertValue(body.getData(), new TypeReference<List<Policy>>() {
            });
        logger.debug("CustomerPolicyListResponse : {}", policyList);
      }
    } catch (Exception e) {
      logger.error("get policy list exception: {}", e.getMessage());
    }
    String policyListJson = new Gson().toJson(policyList);
    logger.debug("getPolicyLists json: {}", policyListJson);
    return policyList;
  }

  @Override
  public List<Policy> getPolicyHistoryLists(PolicyIdRequest request) {
    String url = healthApisPolicyBaseUrl + "/policy/history";
    return super.postForDataWrapper(new TypeReference<List<Policy>>() {
    }, url, request, "get policy history list");
  }

  @Override
  public int getPolicyRenewalCount(PolicyIdRequest request) {
    String url = healthApisPolicyBaseUrl + "/policy/renewal/count";
    return super.postForDataWrapper(new TypeReference<Integer>() {
    }, url, request, "get policy renewal count");
  }

  @Override
  public PolicyDetail getPolicyDetail(PolicyNumberRequest request) {
    PolicyDetail policyDetail = null;
    String url = healthApisPolicyBaseUrl + "/policy/detail/mapper";
    logger.debug(url);
    logger.debug("PolicyNumberRequest: {}", request);
    Map<String, Object> map = MappingUtils.beanToMap(request);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
    try {
      ResponseEntity<DataWrapper> responseEntity = restTemplate
          .postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        policyDetail = objectMapper
            .convertValue(body.getData(), new TypeReference<PolicyDetail>() {
            });
        logger.debug("policy detail: {}", policyDetail);
      }
    } catch (Exception e) {
      logger.error("get policy list exception: {}", e.getMessage());
    }
    return policyDetail;
  }

  @Override
  public List<AddBeneficiariesToPolicyResponse> addBeneficiariesToPolicy(
      List<AddBeneficiariesToPolicyRequest> request) {

    List<AddBeneficiariesToPolicyResponse> response = null;
    String url = healthApisPolicyBaseUrl + "/policy/beneficiary";
    logger.debug(url);
    String input = JsonUtils.objectToJson(request);
    logger.debug("List AddBeneficiariesToPolicyRequest: \n {}", input);
    HttpEntity<String> entity = new HttpEntity<>(input, HttpUtils.getAppJsonHeader());
    try {
      ResponseEntity<DataWrapper> responseEntity = restTemplate
          .postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        response = objectMapper
            .convertValue(body.getData(),
                new TypeReference<List<AddBeneficiariesToPolicyResponse>>() {
                });
        logger.debug("List AddBeneficiariesToPolicyResponse: {}", response);
      }
    } catch (Exception e) {
      logger.error("Add beneficiaries to policy exception: {}", e.getMessage());
    }
    return response;
  }

  @Override
  public ApiResponse addBusinessSourceToIndividualPolicy(
      AddBusinessSourceToIndividualPolicyRequest request) {

    String url = healthApisPolicyBaseUrl + "/policy/businesssource";
    String input = JsonUtils.objectToJson(request);
    logger.debug("url:{}, AddBusinessSourceToIndividualPolicyRequest: \n {}", url, input);
    Map<String, Object> map = MappingUtils.beanToMap(request);
    logger.debug("request map: {}", map);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
    ApiResponse response = null;
    response = getApiResponse(url, entity, response,
        "AddBusinessSourceToIndividualPolicyResponse: {}",
        "add business source to policy exception: {}");
    return response;
  }

  private ApiResponse getApiResponse(String url, HttpEntity<Map<String, Object>> entity,
      ApiResponse response, String s, String s2) {
    try {
      ResponseEntity<DataWrapper> responseEntity = restTemplate
          .postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        response = objectMapper
            .convertValue(body.getData(), new TypeReference<ApiResponse>() {
            });
        logger.debug(s, response);
      }
    } catch (Exception e) {
      logger.error(s2, e.getMessage());
    }
    return response;
  }

  @Override
  public List<Claim> getPolicyClaims(Integer policyId) {
    List<Claim> claimList = null;
    String url = healthApisPolicyBaseUrl + "/policy/claims";
    logger.debug("getPolicyClaims url:{}, policyId: {}", url, policyId);
    Map<String, Object> map = new HashMap<>();
    map.put("policyId", policyId);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
    try {
      ResponseEntity<DataWrapper> responseEntity = restTemplate.postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        claimList = objectMapper.convertValue(body.getData(), new TypeReference<List<Claim>>() {});
        logger.debug("getPolicyClaims claimList: {}", claimList);
      }
    } catch (Exception e) {
      logger.error("getPolicyClaims exception: {}", e.getMessage());
    }
    return claimList;
  }

  @Override
  public List<DependantDetail> getPolicyBeneficiary(PolicyIdRequest request) {
    List<DependantDetail> dependantList = null;
    String url = healthApisPolicyBaseUrl + "/entityMaintenance/entity/dependant";
    logger.debug("url:{}, getPolicyBeneficiary Request: {}", url, request);
    Map<String, Object> map = MappingUtils.beanToMap(request);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
    try {
      ResponseEntity<DataWrapper> responseEntity = restTemplate.postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        dependantList = objectMapper.convertValue(body.getData(), new TypeReference<List<DependantDetail>>() {
        });
        logger.debug("getPolicyBeneficiary Response: {}", dependantList);
      }
    } catch (Exception e) {
      logger.error("get policy dependant list exception: {}", e.getMessage());
    }
    return dependantList;
  }

  @Override
  public BenefitBeanResponse getPolicyBenefit(GetBenefitBreakDownRequest request) {
    BenefitBeanResponse benefit = null;
    String url = healthApisPolicyBaseUrl + "/policy/benefit/breakdown";
    logger.debug(url);
    logger.debug("GetBenefitBreakDownRequest: {}", request);
    Map<String, Object> map = MappingUtils.beanToMap(request);
    logger.debug("request map: {}", map);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
    try {
      ResponseEntity<DataWrapper> responseEntity = restTemplate
          .postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        benefit = objectMapper
            .convertValue(body.getData(), new TypeReference<BenefitBeanResponse>() {
            });
        logger.debug("benefit detail: {}", benefit);
      }
    } catch (Exception e) {
      logger.error("get policy benefit exception: {}", e.getMessage());
    }
    return benefit;
  }

  @Override
  public Benefit getPolicyBenefit(PolicyIdRequest request) {
    String url = healthApisPolicyBaseUrl + "/policy/benefit/all";
    return super.postForDataWrapper(new TypeReference<Benefit>() {
    }, url, request, "get policy benefit");
  }

  @Override
  public PolicyAdjustment getPolicyAdjustment(PolicyIdRequest request) {
    String url = healthApisPolicyBaseUrl + "/policy/adjustment";
    return super.postForDataWrapper(new TypeReference<PolicyAdjustment>() {
    }, url, request, "get policy adjustment");
  }

  @Override
  public PolicyOverComingResponse comingPolicyList(ComingPolicyListRequest request) {
    String url = healthApisPolicyBaseUrl + "/policy/coming/PolicyList";
    return super.postForDataWrapper(new TypeReference<PolicyOverComingResponse>() {
    }, url, request, "comingPolicyList");
  }

  @Override
  public AgentBranchDetailsResponse getAgentBranchDetails(AgentDetailsRequest request) {
    String url = healthApisPolicyBaseUrl + "/policy/agent/branchDetails";
    return super.postForDataWrapper(new TypeReference<AgentBranchDetailsResponse>() {
    }, url, request, "agentBranchDetails");
  }

  @Override
  public ActisurePolicyBranchDetailsResponse addPolicyAdditionalBranchDetails(PolicyAdditionalInfoRequest request) {
    String url = healthApisPolicyBaseUrl + "/policy/additionalInfo/branchDetails";
    ActisurePolicyBranchDetailsResponse actisurePolicyBranchDetailsResponse;
    String input = getJsonString(request);
    HttpEntity<String> entity = new HttpEntity<>(input, HttpUtils.getAppJsonHeader());
    ResponseEntity<DataWrapper> actSureResponse = restTemplate.postForEntity(url, entity, DataWrapper.class);
    logger.info("Raw response : {}", actSureResponse);
    DataWrapper body = actSureResponse.getBody();
    if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
      actisurePolicyBranchDetailsResponse = new Gson().fromJson(new Gson().toJson(body.getData()),ActisurePolicyBranchDetailsResponse.class);
      return  actisurePolicyBranchDetailsResponse;
    }else {
      return ActisurePolicyBranchDetailsResponse.builder().success(false).build();
    }
  }



  public String getJsonString(Object request){
    return JsonUtils.objectToJson(request);
  }
}
