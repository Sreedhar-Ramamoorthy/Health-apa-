package ke.co.apollo.health.remote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.util.Map;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.common.utils.HttpUtils;
import ke.co.apollo.health.common.utils.JsonUtils;
import ke.co.apollo.health.common.utils.MappingUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AbstractRemote {

  @Autowired
  protected RestTemplate restTemplate;

  protected ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  Gson gson;

  protected <T> T postForDataWrapper(TypeReference<T> toValueTypeRef, String url, Object request,
      String business) {
    log.debug("{} url: {}", business, url);
    log.debug("{} request: {}", business, request);
    try {
      Map<String, Object> map = MappingUtils.beanToMap(request);
      log.debug("{} map: {}", business, map);
      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, HttpUtils.getAppJsonHeader());
      ResponseEntity<DataWrapper> responseEntity = restTemplate
          .postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      log.debug("{} response: {}", business, body);
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        return objectMapper.convertValue(body.getData(), toValueTypeRef);
      } else {
        log.error("{} exception: {}", business, responseEntity);
      }
    } catch (Exception e) {
      log.error("{} Exception: {}", business, e);
      this.throwErrorMsg(e);
    }

    return null;
  }

  protected <T> T postForEntity(Class<T> responseType, String url, Object request,
      String business) {
    log.debug("{} url: {}", business, url);
    try {
      ResponseEntity<T> responseEntity;
      HttpHeaders headers = HttpUtils.getAppJsonHeader();

      if (request != null) {
        log.debug("{} request: {}", business, request);
        Map<String, Object> map = MappingUtils.beanToMap(request);
        log.debug("{} json: \n {}", business, JsonUtils.objectToJson(request));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        responseEntity = restTemplate.postForEntity(url, entity, responseType);
      } else {
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(headers);
        responseEntity = restTemplate.postForEntity(url, entity, responseType);
      }
      if (HttpStatus.OK == responseEntity.getStatusCode()) {
        log.debug("{} response: {}", business, responseEntity.getBody());
        return responseEntity.getBody();
      } else {
        log.error("{} exception: {}", business, responseEntity);
      }
    } catch (Exception e) {
      this.handException(e, responseType);
    }

    return null;
  }

  protected <T> T handException(Exception e, Class<T> responseType) {
    String msg = e.getMessage();
    if (e instanceof ResourceAccessException) {
      log.error("Resource Access Exception: {}", e.getMessage());
      msg = "resource access exception";
    } else if (e instanceof HttpClientErrorException) {
      log.error("Http Client Exception: {}", e.getMessage());
      final String ERROR_CODE_422 = "422 : ";
      if (StringUtils.startsWith(e.getMessage(), ERROR_CODE_422)) {
        String error = e.getMessage()
            .substring(ERROR_CODE_422.length() + 1, e.getMessage().length() - 1);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return gson.fromJson(error, responseType);
      }
      msg = "http client exception";
    } else if (e instanceof RestClientException) {
      log.error("Client Error Exception: {}", e.getMessage());

      msg = "rest client exception";
    }
    log.error("Exception: {}", msg);

    return null;
  }


  private void throwErrorMsg(Exception e) {
    String msg = e.getMessage();
    if (e instanceof ResourceAccessException) {
      msg = "resource access exception";
    } else if (e instanceof RestClientException) {
      msg = "rest client exception";
    } else if (e instanceof HttpClientErrorException) {
      msg = "http client exception";
    }
    throw new BusinessException(msg);
  }

}
