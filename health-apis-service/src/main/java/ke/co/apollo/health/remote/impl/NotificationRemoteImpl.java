package ke.co.apollo.health.remote.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.Policy;
import ke.co.apollo.health.common.domain.model.PolicyNotificationTask;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.InAppNotificationMessageResponse;
import ke.co.apollo.health.common.domain.model.response.InAppNotificationResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.common.utils.MappingUtils;
import ke.co.apollo.health.remote.NotificationRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationRemoteImpl implements NotificationRemote {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${apa.health-apis-notification.url}")
  String healthApisNotificationBaseUrl;
  protected ObjectMapper objectMapper = new ObjectMapper();
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  Gson gson;
  @Autowired
  private QueueMessagingTemplate queueMessagingTemplate;
  @Value("${cloud.aws.sqs.sms-notifications.end-point.uri}")
  private String sqsSmsNotificationsQueue;
  @Value("${cloud.aws.sqs.email-notifications.end-point.uri}")
  private String sqsEmailNotificationsQueue;
  @Value("${apa.notification-apis.service.base-url}")
  String notificationApisServiceBaseUrl;

  @Override
  public boolean sendSMSMessage(SMSMessageRequest smsMessageRequest) {

    boolean result = false;
    String url = healthApisNotificationBaseUrl + "/notification/sms";
    logger.debug("send sms url: {}, smsMessageRequest: {}", url, smsMessageRequest);

    try {
      Map<String, Object> map = new HashMap<>();
      map.put("from", smsMessageRequest.getFrom());
      map.put("text", smsMessageRequest.getText());
      map.put("to", smsMessageRequest.getTo());
      map.put("serviceType", smsMessageRequest.getServiceType());

      result = getResult(url, map);
      logger.debug("send sms message: {}", result);
    } catch (Exception e) {
      logger.error("send sms message exception: {}", e.getMessage());
    }
    return result;
  }

  private boolean getResult(String url, Map<String, Object> map) {
    boolean result = false;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(map, headers);
    ResponseEntity<DataWrapper> responseEntity = restTemplate
        .postForEntity(url, requestEntity, DataWrapper.class);
    DataWrapper body = responseEntity.getBody();
    logger.warn("=====getResult{}",body.getData());
    if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
      result = (Boolean) body.getData();
    }
    return result;
  }

  @Override
  public boolean sendEmail(EmailRequest request) {
    boolean result = false;
    String url = healthApisNotificationBaseUrl + "/notification/sendEmail";
    logger.debug("send email url: {}, request: {}", url, request);
    try {
      Map<String, Object> map = MappingUtils.beanToMap(request);
      result = getResult(url, map);
      logger.debug("send email message: {}", result);
    } catch (Exception e) {
      logger.error("===send email exception: {}", e.getMessage());
    }
    return result;
  }

  @Override
  public boolean sendEmailWithTemplate(EmailAttachmentRequest request) {
    boolean result = false;
    String url = healthApisNotificationBaseUrl + "/notification/sendEmailAttachment";
    logger.debug("\n \n ============SEND WITH ATTACHMENT ============= \n send email url: {}, request: {}", url, request);

    try {
      Map<String, Object> map = MappingUtils.beanToMap(request);
      result = getResult(url, map);
      logger.debug("send email message: {}", result);
    } catch (Exception e) {
      logger.error("send email exception: {}", e.getMessage());
    }
    return result;
  }

  @Override
  public boolean createPolicySMSTask(PolicyNotificationTask task) {
    boolean result = false;
    String url = healthApisNotificationBaseUrl + "/notification/createPolicySMSTask";
    logger.debug("create policy SMS task url: {}, task: {}", url, task);

    try {
      Map<String, Object> map = MappingUtils.beanToMap(task);
      result = getResult(url, map);
      logger.debug("create policy SMS task result: {}", result);
    } catch (Exception e) {
      logger.error("create policy SMS task exception: {}", e.getMessage());
    }
    return result;
  }

  @Override
  public boolean cancelPolicySMSTask(PolicyNotificationTask task) {
    boolean result = false;
    String url = healthApisNotificationBaseUrl + "/notification/cancelPolicySMSTask";

    logger.debug("cancel policy SMS task url: {}", url);
    logger.debug("cancel policy SMS task : {}", task);

    try {
      Map<String, Object> map = MappingUtils.beanToMap(task);
      result = getResult(url, map);
      logger.debug("cancel policy SMS task result: {}", result);
    } catch (Exception e) {
      logger.error("cancel policy SMS task exception: {}", e.getMessage());
    }
    return result;
  }

  @Override
  public boolean sendPolicyInstantSMSNotification(PolicyNotificationTask task) {
    boolean result = false;
    String url = healthApisNotificationBaseUrl + "/notification/sendInstantSMSTask";
    logger.debug("send policy instant SMS notification url: {}, task: {}", url, task);

    try {
      Map<String, Object> map = MappingUtils.beanToMap(task);
      logger.debug("send policy instant SMS notification task: {}", map);

      result = getResult(url, map);
      logger.debug("send policy instant SMS notification result: {}", result);
    } catch (Exception e) {
      logger.error("send policy instant SMS notification exception: {}", e.getMessage());
    }
    return result;
  }
  public boolean queueSMSMessage(SMSMessageRequest smsMessageRequest) {
    try {
      String smsBody = gson.toJson(smsMessageRequest);
      logger.info("==== SMS body : {} ", smsBody);
      queueMessagingTemplate.send(
              sqsSmsNotificationsQueue,
              MessageBuilder
                      .withPayload(smsBody)
                      .build()
      );
      return true;
    } catch (Exception ex) {
      logger.error("SMS not sent. Error message: {}", ex.getMessage());
      return false;
    }
  }


  public boolean queueEmailMessage(EmailRequest emailRequest) {
    try {
      String smsBody = gson.toJson(emailRequest);
      logger.info("==== Email body : {} ", smsBody);
      queueMessagingTemplate.send(
              sqsEmailNotificationsQueue,
              MessageBuilder
                      .withPayload(smsBody)
                      .build()
      );
      return true;
    } catch (Exception ex) {
      logger.error("Email not sent. Error message: {}", ex.getMessage());
      return false;
    }
  }
  @Override
  public boolean createInAppNotification(InAppNotificationCreateRequest createRequest) {
    boolean result = false;
    String url =  notificationApisServiceBaseUrl + GlobalConstant.CREATE_INAPPNOTIFICATION_URL;
    logger.debug("createInAppNotification url: {}, task: {}", url, createRequest);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("accept", "*/*");
    Map<String, Object> map = new HashMap<>();
    map.put("deviceRegistrationToken", createRequest.getDeviceRegistrationToken());
    map.put("notification", createRequest.getNotification());
    map.put("phoneNumber", createRequest.getPhoneNumber());
    map.put("email", createRequest.getEmail());
    map.put("serviceType", createRequest.getServiceType());
    map.put("readStatus", createRequest.getReadStatus());
    map.put("actionStatus", createRequest.getActionStatus());
    map.put("policyNumber", createRequest.getPolicyNumber());
    map.put("notificationSubject", createRequest.getNotificationSubject());
    if(createRequest.getId() != null) {
      map.put("id", createRequest.getId());
    }
    try {
      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(map, headers);
      ResponseEntity<InAppNotificationResponse> response = restTemplate
              .postForEntity(url,
                      requestEntity, InAppNotificationResponse.class);
      InAppNotificationResponse body = response.getBody();
      logger.debug("Create InAppNotification response {}", response);
      if(response != null) {
        result = body.isSuccess();
      }
    }
    catch (Exception e) {
      logger.error("createInAppNotification exception: {}", e.getMessage());
    }
    return  result;
  }

  @Override
  public boolean clearInAppNotification(List<Integer> request) {
    boolean result = false;
    String url = notificationApisServiceBaseUrl + GlobalConstant.CLEAR_INAPPNOTIFICATION_URL;
    logger.debug("clearInAppNotification url: {}, request: {}", url, request);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      HttpEntity<List<Integer>> requestEntity = new HttpEntity<>(request, headers);
      ResponseEntity<InAppNotificationResponse> responseEntity = restTemplate
              .postForEntity(url, requestEntity, InAppNotificationResponse.class);
      InAppNotificationResponse body = responseEntity.getBody();
      logger.debug("1. clearInAppNotification response {}", body);
      if(body != null) {
        result = body.isSuccess();
      }
      logger.debug("clearInAppNotification message: {}", result);
    } catch (Exception e) {
      logger.error("clearInAppNotification exception: {}", e.getMessage());
    }
    return result;
  }

  @Override
  public List<InAppNotificationMessageResponse> getAllInAppNotificationList(InAppNotificationMessageRequest inAppNotificationMessageRequest) {
    List<InAppNotificationMessageResponse> response = null;
    String url = notificationApisServiceBaseUrl + GlobalConstant.GET_ALL_INAPPNOTIFICATION_URL;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put("pageNumber", inAppNotificationMessageRequest.getPageNumber());
    queryParams.put("pageSize", inAppNotificationMessageRequest.getPageSize());
    queryParams.put("phoneNumber", inAppNotificationMessageRequest.getPhoneNumber());
    String params = gson.toJson(queryParams);
    url = url + "?pageNumber=" + inAppNotificationMessageRequest.getPageNumber()
            + "&pageSize=" + inAppNotificationMessageRequest.getPageSize()
            + "&phoneNumber=" + inAppNotificationMessageRequest.getPhoneNumber();
    logger.debug("get all inappnotification url: {} params: {}", url, params);
    try {
      ResponseEntity<DataWrapper> responseEntity = restTemplate
              .getForEntity(url, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && ReturnCode.OK.getValue() == body.getCode()) {
        response = objectMapper
                .convertValue(body.getData(), new TypeReference<List<InAppNotificationMessageResponse>>() {
                });
        logger.debug("get all inappnotification response {}", response);
      }
    } catch (Exception e) {
      logger.error("get all inappnotification error: {}", e.getMessage());
    }
    return response;
  }

}
