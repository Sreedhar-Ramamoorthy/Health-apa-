package ke.co.apollo.health.notification.service.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.GetEndpointAttributesResult;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.NotFoundException;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ke.co.apollo.health.common.domain.model.CognitoSns;
import ke.co.apollo.health.common.domain.model.request.SNSNotificationRequest;
import ke.co.apollo.health.notification.service.CognitoSnsService;
import ke.co.apollo.health.notification.service.SNSService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SNSServiceImpl implements SNSService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private String regex = ".*Endpoint (arn:aws:sns[^ ]+) already exists with the same [Tt]oken.*";

  @Autowired
  private CognitoSnsService cognitoSnsService;

  @Autowired
  private AmazonSNS client;

  @Value("${apa.health-apis-notification.app-arn}")
  private String appARN;

  public String sendSNSNotification(SNSNotificationRequest request) {

    String messageId = null;

    String endpointARN = this.registerWithSNS(request.getCognitoId(), request.getFirebaseToken());
    if (StringUtils.isNotEmpty(endpointARN)) {
      messageId = this.publish(request.getMessage(), endpointARN);
    }

    return messageId;
  }

  public String registerWithSNS(String cognitoId, String firebaseToken) {

    String endpointArn = null;
    CognitoSns cognitoSns = cognitoSnsService.getCognitoSnsById(cognitoId);
    if (cognitoSns != null) {
      //Retrieve endpoint ARN and firebase token
      endpointArn = cognitoSns.getEndpointArn();
      if (StringUtils.isEmpty(firebaseToken)) {
        firebaseToken = cognitoSns.getFirebaseToken();
      }
    }

    boolean updateNeeded = false;
    boolean createNeeded = StringUtils.isEmpty(endpointArn);

    if (createNeeded) {
      // No platform endpoint ARN is stored; need to call createEndpoint.
      endpointArn = createEndpoint(cognitoId, firebaseToken);
      createNeeded = false;
    }

    logger.debug("Retrieving platform endpoint data...");
    // Look up the platform endpoint and make sure the data in it is current, even if
    // it was just created.
    try {
      GetEndpointAttributesRequest geaReq =
          new GetEndpointAttributesRequest()
              .withEndpointArn(endpointArn);
      GetEndpointAttributesResult geaRes = client.getEndpointAttributes(geaReq);

      updateNeeded = !geaRes.getAttributes().get("Token").equals(firebaseToken)
          || !geaRes.getAttributes().get("Enabled").equalsIgnoreCase("true");

    } catch (NotFoundException nfe) {
      // We had a stored ARN, but the platform endpoint associated with it
      // disappeared. Recreate it.
      createNeeded = true;
    } catch (Exception e) {
      logger.error("Error occurred when SNS get endpont attr on endpoint:{}, error message :{}",
          endpointArn, e.getMessage());
    }

    if (createNeeded) {
      endpointArn = createEndpoint(cognitoId, firebaseToken);
    }

    logger.debug("updateNeeded: {}", updateNeeded);

    if (updateNeeded) {
      // The platform endpoint is out of sync with the current data
      // update the token and enable it.
      logger.debug("Updating platform endpoint :{}", endpointArn);
      Map<String, String> attribs = new HashMap<>();
      attribs.put("Token", firebaseToken);
      attribs.put("Enabled", "true");
      SetEndpointAttributesRequest saeReq =
          new SetEndpointAttributesRequest()
              .withEndpointArn(endpointArn)
              .withAttributes(attribs);
      client.setEndpointAttributes(saeReq);
    }

    return endpointArn;
  }

  private String publish(String message, String endPointARN) {
    PublishRequest publishRequest = new PublishRequest().withMessage(message)
        .withTargetArn(endPointARN);
    String response = null;
    try {
      PublishResult result = client.publish(publishRequest);
      response = result.getMessageId();
    } catch (Exception e) {
      logger.error("Error occurred when SNS publish message on endpoint:{}, error message:{}",
          endPointARN, e.getMessage());
    }

    return response;
  }

  private String createEndpoint(String cognitoId, String token) {

    String endpointArn = null;
    try {
      logger.debug("Creating platform endpoint with token: {}", token);
      CreatePlatformEndpointRequest cpeReq =
          new CreatePlatformEndpointRequest()
              .withPlatformApplicationArn(appARN)
              .withToken(token);
      CreatePlatformEndpointResult cpeRes = client.createPlatformEndpoint(cpeReq);
      endpointArn = cpeRes.getEndpointArn();
    } catch (InvalidParameterException ipe) {
      String message = ipe.getErrorMessage();
      logger.error("createEndpoint occurred error: {}", message);
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(message);
      if (m.matches()) {
        // The platform endpoint already exists for this token, but with
        // additional custom data that
        // createEndpoint doesn't want to overwrite. Just use the
        // existing platform endpoint.
        endpointArn = m.group(1);
      } else {
        // Rethrow the exception, the input is actually bad.
        throw ipe;
      }
    } catch (Exception e) {
      logger.error("Error occurred when SNS create platform endpoint on token:{}, error message:{}",
          token, e.getMessage());
    }
    storeEndpointArn(cognitoId, endpointArn);
    return endpointArn;
  }


  private void storeEndpointArn(String cognitoId, String endpointArn) {
    cognitoSnsService.updateEndpointArn(
        CognitoSns.builder().cognitoId(cognitoId).endpointArn(endpointArn).build());
  }


}
