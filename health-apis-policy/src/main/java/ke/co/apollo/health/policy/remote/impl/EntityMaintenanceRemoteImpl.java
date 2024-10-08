package ke.co.apollo.health.policy.remote.impl;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import ke.co.apollo.health.common.domain.model.EntityDetails;
import ke.co.apollo.health.common.domain.model.request.AddClientEntityRequest;
import ke.co.apollo.health.common.domain.model.request.AddContactDetailsRequest;
import ke.co.apollo.health.common.domain.model.request.GetEntityDetailsRequest;
import ke.co.apollo.health.common.domain.model.response.AddClientEntityResponse;
import ke.co.apollo.health.common.domain.model.response.AddContactDetailsResponse;
import ke.co.apollo.health.common.domain.model.response.GetEntityDetailsResponse;
import ke.co.apollo.health.policy.remote.EntityMaintenanceRemote;

/**
 * integrate Activus.Services.EntityMaintenance
 *
 * @author Rick
 * @version 1.0
 * @see
 * @since 9/3/2020
 */
@Service
public class EntityMaintenanceRemoteImpl implements EntityMaintenanceRemote {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${apa.health-apis.service.url}")
  String healthApisBaseUrl;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  Gson gson;

  @Override
  public AddClientEntityResponse addClientEntity(AddClientEntityRequest request) {

    AddClientEntityResponse response = null;
    String url = healthApisBaseUrl + "/ASEntityMaintenance/api/entitymaintenance/Entity/AddClientEntity";
    String log = gson.toJson(request);
    logger.debug("url: {}, addClientEntity: {}", url, log);
    Map<String, Object> map = new HashMap<>();
    if (StringUtils.isEmpty(request.getTitle())) {
      request.setTitle("Mr");
    }
    map.put("ClientEntityDetails", request);
    String params = gson.toJson(map);
    HttpEntity<Object> entity = new HttpEntity<>(params, getAppJsonHeader());
    logger.debug("url: {}, params: {}", url, params);
    try {
      ResponseEntity<AddClientEntityResponse> responseEntity = restTemplate
          .postForEntity(url, entity, AddClientEntityResponse.class);
      if (HttpStatus.OK == responseEntity.getStatusCode()) {
        response = responseEntity.getBody();
        logger.debug("AddClientEntity Response data: {}", response);
        if (response != null) {
          if (response.isSuccess()) {
            fulfilCustomerResponse(request, response);
          } else {
            logger.error("Add client entity occurred error , errorMessage:{} ,errors:{}",
                response.getErrorMessage(), response.getErrors());
          }
        }
      }
    } catch (Exception e) {
      logger.error("add client entity exception: {}", e.getMessage());
    }

    return response;
  }

  private void fulfilCustomerResponse(AddClientEntityRequest request,
      AddClientEntityResponse response) {
    response.setDateOfBirth(request.getDateOfBirth());
    response.setFirstName(request.getFirstName());
    response.setSurname(request.getSurname());
    response.setGender(request.getGender());
    response.setTitle(request.getTitle());
    response.setInitials(request.getInitials());
    response.setNationality(request.getNationality());
    response.setOccupation(request.getOccupation());
    response.setParentId(request.getParentId());
    response.setRelationshipDescription(request.getRelationshipDescription());
    response.setRelationshipEffectiveDate(request.getRelationshipEffectiveDate());
    response.setListRoleAdditionalInfo(request.getListRoleAdditionalInfo());
    response.setCustomerId(request.getCustomerId());
  }

  @Override
  public List<AddClientEntityResponse> addDependant(List<AddClientEntityRequest> request) {
    List<AddClientEntityResponse> result = new ArrayList<>();
    for (AddClientEntityRequest dependant : request) {
      AddClientEntityResponse response = this.addClientEntity(dependant);
      result.add(response);
    }
    return result;
  }

  @Override
  public AddContactDetailsResponse addContactDetails(AddContactDetailsRequest request) {

    AddContactDetailsResponse response = null;
    String url = healthApisBaseUrl + "/ASEntityMaintenance/api/entitymaintenance/ContactDetails/AddContactDetails";
    String log  = gson.toJson(request);
    logger.debug("url: {}, addContactDetails Request: {}", url, log);

    HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(request), getAppJsonHeader());

    try {
      ResponseEntity<AddContactDetailsResponse> responseEntity = restTemplate.postForEntity(url, entity, AddContactDetailsResponse.class);
      if (HttpStatus.OK == responseEntity.getStatusCode()) {
        response = responseEntity.getBody();
        String logRes  = gson.toJson(response);
        logger.debug("addContactDetails Response data: {}", logRes);
      } else {
          logger.error("Add contact details occurred error occurred network error: {}", responseEntity);
      }
    } catch (Exception e) {
      logger.error("addContactDetails exception: {}", e.getMessage());
    }

    return response;
  }

  @Override
  public EntityDetails getEntityDetailsById(GetEntityDetailsRequest request) {

    EntityDetails response = null;
    try {
      String url = healthApisBaseUrl
          + "/ASEntityMaintenance/api/entitymaintenance/Entity/GetEntityDetailsById/{entityId}";

      // create a map for get parameters
      Map<String, Object> map = new HashMap<>();
      map.put("entityId", request.getEntityId());
      String params = gson.toJson(map);
      logger.debug("url: {} get EntityDetailsById detail request: {}", url, params);

      ResponseEntity<GetEntityDetailsResponse> stringResponseEntity = restTemplate
          .getForEntity(url, GetEntityDetailsResponse.class, map);

      if (HttpStatus.OK == stringResponseEntity.getStatusCode()) {
        GetEntityDetailsResponse data = stringResponseEntity.getBody();
        logger.debug("get EntityDetailsById data: {}", data);
        if (data != null) {
          if (data.isSuccess()) {
            response = data.getEntityDetails();
          } else {
            logger.error("Get EntityDetailsById occurred error , errorMessage:{} ,errors:{}",
                data.getErrorMessage(), data.getErrors());
          }
        }
      }
      logger.debug("StatusCode: {}", stringResponseEntity.getStatusCode());
    } catch (ResourceAccessException e) {
      logger.error("Resource Access Exception: {}", e.getMessage());
    } catch (HttpClientErrorException e) {
      logger.error("Client Error Exception: {}", e.getMessage());
    } catch (Exception e) {
      logger.error("Exception: {}", e.getMessage());
    }

    return response;

  }

  HttpHeaders getAppJsonHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    return headers;
  }
}
