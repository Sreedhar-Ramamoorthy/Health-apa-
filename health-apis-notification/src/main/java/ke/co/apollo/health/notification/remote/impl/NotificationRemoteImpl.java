package ke.co.apollo.health.notification.remote.impl;

import com.google.gson.Gson;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.request.EmailAttachmentRequest;
import ke.co.apollo.health.common.domain.model.request.EmailRequest;
import ke.co.apollo.health.common.domain.model.request.SMSMessageRequest;
import ke.co.apollo.health.common.domain.model.response.SMSMessageResponse;
import ke.co.apollo.health.common.utils.MappingUtils;
import ke.co.apollo.health.notification.remote.NotificationRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationRemoteImpl implements NotificationRemote {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${apa.notification-apis.service.base-url}")
  String notificationApisServiceBaseUrl;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  Gson gson;

  @Override
  public SMSMessageResponse sendSMSMessage(SMSMessageRequest smsMessageRequest) {
    SMSMessageResponse smsMessageResponse = null;
    String url = notificationApisServiceBaseUrl + GlobalConstant.SEND_SMS_MESSAGE_URL;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    // create a map for post parameters
    Map<String, Object> map = new HashMap<>();
    map.put(GlobalConstant.SEND_SMS_MESSAGE_FROM, smsMessageRequest.getFrom());
    map.put(GlobalConstant.SEND_SMS_MESSAGE_TEXT, smsMessageRequest.getText());
    String to = smsMessageRequest.getTo();
    if (!to.startsWith("+")) {
      to = "+" + to;
    }
    map.put(GlobalConstant.SEND_SMS_MESSAGE_TO, to);
    map.put(GlobalConstant.SEND_SMS_MESSAGE_SERVICE_TYPE, smsMessageRequest.getServiceType());

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
    String params = gson.toJson(map);
    logger.debug("url: {} smsMessageRequest: {}", url, params);
    try {
      smsMessageResponse = restTemplate.postForObject(url, entity, SMSMessageResponse.class);
      if (smsMessageResponse != null) {
        logger.debug("smsMessageResponse: {}", smsMessageResponse);
      }
    } catch (Exception e) {
      logger.error("Send SMS error: {}", e.getMessage());
    }

    return smsMessageResponse;
  }

  @Override
  public String sendEmail(EmailRequest request) {
    String response = null;
    String url = notificationApisServiceBaseUrl + GlobalConstant.SEND_EMAIL_MESSAGE_URL;
    logger.debug("url: {}", url);
    logger.debug("EmailRequest: {}", request);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    // create a map for post parameters
    Map<String, Object> map = MappingUtils.beanToMap(request);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

    try {
      response = restTemplate.postForObject(url, entity, String.class);
      if (response != null) {
        logger.debug("send email response: {}", response);
      }
    } catch (Exception e) {
      logger.error("Send Email error: {}", e.getMessage());
    }
    return response;
  }

  @Override
  public String sendEmail(EmailAttachmentRequest request) {
    String response = null;
    String url = notificationApisServiceBaseUrl + GlobalConstant.SEND_EMAIL_ATT_MESSAGE_URL;
    logger.debug("send email with attachment url: {}", url);
    logger.debug("email with attachment EmailRequest Body : {}", new Gson().toJson(request));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    // create a map for post parameters
    Map<String, Object> map = MappingUtils.beanToMap(request);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

    try {
      response = restTemplate.postForObject(url, entity, String.class);
      if (response != null) {
        logger.debug("send email response: {}", response);
      }
    } catch (Exception e) {
      logger.error("Send Email error: {}", e.getMessage());
    }
    return response;
  }
}
