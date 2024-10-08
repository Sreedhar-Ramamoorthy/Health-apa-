package ke.co.apollo.health.policy.remote.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import ke.co.apollo.health.common.domain.model.request.PolicyAdditionalInfoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import ke.co.apollo.health.common.domain.model.remote.AddBenefitsToPolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyResponse;
import ke.co.apollo.health.common.domain.model.request.AddBeneficiariesToPolicyRequest;
import ke.co.apollo.health.common.domain.model.request.AddBusinessSourceToIndividualPolicyRequest;
import ke.co.apollo.health.common.domain.model.request.AddIndividualPolicyBeneficiaryUWQuestionsRequest;
import ke.co.apollo.health.common.domain.model.response.ASAPIResponse;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.common.utils.HttpUtils;
import ke.co.apollo.health.common.utils.JsonUtils;
import ke.co.apollo.health.common.utils.MappingUtils;
import ke.co.apollo.health.policy.remote.PolicyRemote;

@Component
public class PolicyRemoteImpl implements PolicyRemote {

  private final Logger logger = LoggerFactory.getLogger(getClass());

//  @Value("${apa.health-apis.service.url}")
  @Value("http://192.168.100.102")
  String healthApisBaseUrl;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  Gson gson;

  @Override
  public CreatePolicyResponse createPolicy(CreatePolicyRequest createPolicyRequest) {
    CreatePolicyResponse response = null;
    String url = healthApisBaseUrl
        + "/ASPolicyMaintenance/api/policymaintenance/Policy/CreatePolicy";
    String input = JsonUtils.objectToJson(createPolicyRequest);
    logger.debug("url: {}, createPolicyRequest: \n {}", url, input);

    try {
      Map<String, Object> map = MappingUtils.beanToMap(createPolicyRequest);
      logger.debug("map: {}", map);
      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());

      ResponseEntity<CreatePolicyResponse> responseEntity = restTemplate
          .postForEntity(url, entity, CreatePolicyResponse.class);
      if (HttpStatus.OK == responseEntity.getStatusCode()) {
        response = responseEntity.getBody();
        logger.debug("CreatePolicyResponse data: {}", response);
      }
    } catch (Exception e) {
      logger.error("create policy exception: {}", e.getMessage());
      this.throwErrorMsg(e);
    }
    return response;
  }

  @Override
  public ApiResponse addBenefitsToPolicy(AddBenefitsToPolicyRequest request) {
    ApiResponse response = null;
    String url = healthApisBaseUrl
        + "/ASPolicyMaintenance/api/policymaintenance/IndividualPolicyBenefits/AddBenefitsToPolicy";
    String input = JsonUtils.objectToJson(request);
    logger.debug("url: {}, AddBenefitsToPolicyRequest: \n {}", url, input);

    try {
      Map<String, Object> map = MappingUtils.beanToMap(request);
      logger.debug("map: {}", map);
      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());

      ResponseEntity<ApiResponse> responseEntity = restTemplate
          .postForEntity(url, entity, ApiResponse.class);
      if (HttpStatus.OK == responseEntity.getStatusCode()) {
        response = responseEntity.getBody();
        logger.debug("add benefits to policy response: {}", response);
      }
    } catch (Exception e) {
      logger.error("add benefits to policy exception: {}", e.getMessage());
      this.throwErrorMsg(e);
    }
    return response;
  }

  @Override
  public ASAPIResponse addBeneficiaryToPolicy(AddBeneficiariesToPolicyRequest request) {

    logger.info("Beneficiary Details: policyId = {}, entityId = {}, effectiveDate = {}, joinDate = {}, originalJoinDate = {}" , request.getPolicyId(), request.getEntityId(), request.getPolicyEffectiveDate(), request.getJoinDate(), request.getOriginalJoinDate());

    Date effectiveDate = null;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    try{
      effectiveDate = formatter.parse(request.getPolicyEffectiveDate());
      }
    catch(Exception e) {
      logger.error("Add Beneficiaries To Policy exception : {}", e.getMessage());
      this.throwErrorMsg(e);    //NO SONAR
      }

    request.setJoinDate(effectiveDate);
    request.setOriginalJoinDate(effectiveDate);

    ASAPIResponse response = null;
    String url = healthApisBaseUrl
        + "/ASPolicyMaintenance/api/policymaintenance/IndividualPolicyBeneficiary/AddBeneficiaryToIndividualPolicy";
    String json = new Gson().toJson(request);

    logger.debug("url: {}, AddBeneficiariesToPolicyRequest: {}", url, json);

    Map<String, Object> map = new HashMap<>();
    map.put("Beneficiary", request);
    HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(map), HttpUtils.getAppJsonHeader());

    try {
      ResponseEntity<ASAPIResponse> responseEntity = restTemplate
          .postForEntity(url, entity, ASAPIResponse.class);
      if (HttpStatus.OK == responseEntity.getStatusCode()) {
        response = responseEntity.getBody();
        logger.debug("Add Beneficiaries To Policy response: {}", response);
        if (response != null && !response.isSuccess()) {
          logger.error("Add Beneficiaries To Policy occurred error , errorMessage:{} ,errors:{}",
              response.getErrorMessage(), response.getErrors());
        }
      }
    } catch (Exception e) {
      logger.error("Add Beneficiaries To Policy exception: {}", e.getMessage());
      this.throwErrorMsg(e);  //NO SONAR
    }

    return response;
  }

  private Date getFormatedDate(String date){
    Date effectiveDate = null;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    try{
      effectiveDate = formatter.parse(date);
    }
    catch(Exception e) {
      logger.error("Date Parse exception : {}", e.getMessage());
      this.throwErrorMsg(e);    //NO SONAR
    }
    return effectiveDate;
  }

  @Override
  public ASAPIResponse addAgentBranchDetailsToPolicy(PolicyAdditionalInfoRequest request) {
    logger.info("Policy Additional Information : {}" ,gson.toJson(request));
    ASAPIResponse response = null;
    String url = healthApisBaseUrl
            + "/ASPolicyMaintenance/api/policymaintenance/Policy/AddPolicyAdditionalInfoToPolicy";
    String json = new Gson().toJson(request);
    logger.debug("url: {}, Add Policy Details (AGENT BRANCH) : {}", url, json);

    Map<String, Object> map = MappingUtils.beanToMap(request);
    HttpEntity<Object> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());

    try {
      ResponseEntity<ASAPIResponse> responseEntity = restTemplate.postForEntity(url, entity, ASAPIResponse.class);
      if (HttpStatus.OK == responseEntity.getStatusCode()) {
        response = responseEntity.getBody();
        logger.debug("Add Beneficiaries To Policy response: {}", response);
        if (response != null && !response.isSuccess()) {
          logger.error("Add Beneficiaries To Policy occurred error , errorMessage:{} ,errors:{}",
                  response.getErrorMessage(), response.getErrors());
        }
      }
    } catch (Exception e) {
      logger.error("Add Beneficiaries To Policy exception: {}", e.getMessage()); //NO SONAR
      this.throwErrorMsg(e);  //NO SONAR
    }
    return response;
  }

  @Override
  public ApiResponse addBusinessSourceToIndividualPolicy(AddBusinessSourceToIndividualPolicyRequest request) {

    String url = healthApisBaseUrl
        + "/ASPolicyMaintenance/api/policymaintenance/BusinessSource/AddBusinessSourceToIndividualPolicy";
    String input = JsonUtils.objectToJson(request);
    ApiResponse response = null;
    logger.debug("url: {}, AddBusinessSourceToIndividualPolicyRequest: \n {}", url, input);
    try {
      Map<String, Object> map = MappingUtils.beanToMap(request);
      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
      logger.debug("request map: {}", map);
      ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(url, entity, ApiResponse.class);
      if (HttpStatus.OK == responseEntity.getStatusCode()) {
        response = responseEntity.getBody();
        String responseOut = JsonUtils.objectToJson(response);
        logger.debug("add business source to policy response: {}", response);
        logger.debug("add business source to policy response Json: {}", responseOut);
      }
    } catch (Exception e) {
      logger.error("add business source to policy exception: {}", e.getMessage());
      this.throwErrorMsg(e);
    }
    return response;
  }

  @Override
  public ApiResponse addIndividualPolicyBeneficiaryUWQuestions(
      AddIndividualPolicyBeneficiaryUWQuestionsRequest request) {

    String url = healthApisBaseUrl
        + "/ASPolicyMaintenance/api/policymaintenance/Questions/AddIndividualPolicyBeneficiaryUnderwritingQuestions";
    String input = JsonUtils.objectToJson(request);
    ApiResponse response = null;
    logger.debug("url: {}, AddIndividualPolicyBeneficiaryUWQuestionsRequest: \n {}", url, input);
    try {
      Map<String, Object> map = MappingUtils.beanToMap(request);
      logger.debug("request map: {}", map);
      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(MappingUtils.beanToMap(request), HttpUtils.getAppJsonHeader());
      ResponseEntity<ApiResponse> responseEntity = restTemplate
          .postForEntity(url, entity, ApiResponse.class);
      if (HttpStatus.OK == responseEntity.getStatusCode()) {
        logger.debug("add beneficiary question to policy response: {}", response);
        response = responseEntity.getBody();
      }
    } catch (Exception e) {
      logger.error("add beneficiary questio to policy exception: {}", e.getMessage());
      this.throwErrorMsg(e);
    }
    return response;
  }

  private void throwErrorMsg(Exception e) {
    String msg = e.getMessage();
    if (e instanceof ResourceAccessException) {
      msg = "resource access exception";
    } else if (e instanceof HttpClientErrorException) {
      msg = "http client exception";
    }
    throw new BusinessException(msg);
  }
}
