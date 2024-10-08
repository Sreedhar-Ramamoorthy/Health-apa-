package ke.co.apollo.health.remote.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ke.co.apollo.health.common.domain.model.Principal;
import ke.co.apollo.health.common.domain.model.request.AddClientEntityRequest;
import ke.co.apollo.health.common.domain.model.request.AddContactDetailsRequest;
import ke.co.apollo.health.common.domain.model.response.AddClientEntityResponse;
import ke.co.apollo.health.common.domain.model.response.AddContactDetailsResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.common.utils.HttpUtils;
import ke.co.apollo.health.common.utils.JsonUtils;
import ke.co.apollo.health.remote.CustomerRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Access policy micro-service operate customer info
 *
 * @author Rick
 * @version 1.0
 * @see
 * @since 9/17/2020
 */
@Component
public class CustomerRemoteImpl implements CustomerRemote {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${apa.health-apis-policy.url}")
  String healthApisPolicyBaseUrl;
  @Autowired
  private RestTemplate restTemplate;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public AddClientEntityResponse addClientEntity(AddClientEntityRequest request) {

    AddClientEntityResponse addClientEntityResponse = null;
    String url = healthApisPolicyBaseUrl + "/entityMaintenance/client";
    logger.debug(url);
    try {
      ResponseEntity<DataWrapper> responseEntity = sendPostRequest(url,
          JsonUtils.objectToJson(request), "AddClientEntityRequest: \n {}");
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        addClientEntityResponse = objectMapper
            .convertValue(body.getData(), new TypeReference<AddClientEntityResponse>() {
            });
        logger.debug("addClientEntityResponse: {}", addClientEntityResponse);
      }
    } catch (Exception e) {
      logger.error("add client exception: {}", e.getMessage());
    }

    return addClientEntityResponse;
  }

  @Override
  public AddContactDetailsResponse addClientContact(AddContactDetailsRequest request) {

    AddContactDetailsResponse result = null;
    String url = healthApisPolicyBaseUrl + "/entityMaintenance/contact";
    logger.debug(url);
    try {
      ResponseEntity<DataWrapper> responseEntity = sendPostRequest(url,
          JsonUtils.objectToJson(request), "AddContactDetailsRequest: \n {}");
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        result = objectMapper
            .convertValue(body.getData(), new TypeReference<AddContactDetailsResponse>() {
            });
        logger.debug("addContactDetailsResponse: {}", result);
      }
    } catch (Exception e) {
      logger.error("add client contact exception: {}", e.getMessage());
    }

    return result;
  }

  private ResponseEntity<DataWrapper> sendPostRequest(String url, String input, String logTitle) {
    logger.debug(logTitle, input);
    HttpEntity<String> entity = new HttpEntity<>(input, HttpUtils.getAppJsonHeader());
    return restTemplate.postForEntity(url, entity, DataWrapper.class);
  }

  @Override
  public Principal getPrincipalByEntityId(Long entityId) {

    Map<String, Object> map = new HashMap<>();
    map.put("entityId", entityId);
    String url = healthApisPolicyBaseUrl + "/entityMaintenance/entity/principal/detail";
    logger.debug("url: {}, getPrincipalByEntityId params: {}", url, map);
    Principal principal = null;
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
    try {
      ResponseEntity<DataWrapper> responseEntity = restTemplate
          .postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null) {
        if (ReturnCode.OK.getValue() == body.getCode()) {
          principal = objectMapper
              .convertValue(body.getData(), new TypeReference<Principal>() {
              });
          logger.debug("getPrincipalByEntityId response: {}", principal);
        } else {
          throw new BusinessException(body.getMessage());
        }
      }
    } catch (Exception e) {
      logger.error("getPrincipalByEntityId exception: {}", e.getMessage());
    }

    return principal;
  }

  @Override
  public List<AddClientEntityResponse> addDependant(List<AddClientEntityRequest> request) {

    List<AddClientEntityResponse> addDependantResponse = null;
    String url = healthApisPolicyBaseUrl + "/entityMaintenance/client/dependant";
    logger.debug(url);
    try {
      ResponseEntity<DataWrapper> responseEntity = sendPostRequest(url,
          JsonUtils.objectToJson(request),
          "addDependant: \n {}");
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        addDependantResponse = objectMapper
            .convertValue(body.getData(), new TypeReference<List<AddClientEntityResponse>>() {
            });
        logger.debug("addDependantResponse: {}", addDependantResponse);
      }
    } catch (Exception e) {
      logger.error("add dependant exception: {}", e.getMessage());
    }

    return addDependantResponse;
  }

}
