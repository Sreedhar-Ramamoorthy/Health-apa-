package ke.co.apollo.health.notification.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import ke.co.apollo.health.common.domain.model.PolicyNotificationTask;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.ResultResponse;
import ke.co.apollo.health.common.domain.model.response.SNSNotificationResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.notification.service.CognitoSnsService;
import ke.co.apollo.health.notification.service.NotificationService;
import ke.co.apollo.health.notification.service.SNSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@Api(tags = "Health Notification Integration API")
public class NotificationController {

  @Autowired
  NotificationService notificationService;

  @Autowired
  private SNSService snsService;

  @Autowired
  private CognitoSnsService cognitoSnsService;

  @PostMapping("/sms")
  @ApiOperation("Send SMS Message")
  public ResponseEntity<DataWrapper> sendSMSMessage(
      @Valid @RequestBody SMSMessageRequest smsMessageRequest) {
    return ResponseEntity
        .ok(new DataWrapper(notificationService.sendSMSMessage(smsMessageRequest)));
  }

  @PostMapping("/sendEmail")
  public ResponseEntity<DataWrapper> sendEmail(@RequestBody EmailRequest emailRequest) {
    return ResponseEntity.ok(new DataWrapper(notificationService.sendEmail(emailRequest)));
  }

  @PostMapping("/sendEmailAttachment")
  public ResponseEntity<DataWrapper> sendEmailAttachment(@RequestBody EmailAttachmentRequest emailRequest) {
    return ResponseEntity.ok(new DataWrapper(notificationService.sendEmailAttachment(emailRequest)));
  }

  @PostMapping("/createPolicySMSTask")
  @ApiOperation("Create Policy SMS Task")
  public ResponseEntity<DataWrapper> createPolicySMSTask(
      @Valid @RequestBody PolicyNotificationTask task) {
    return ResponseEntity
        .ok(new DataWrapper(notificationService.createPolicyNotificationTask(task)));
  }

  @PostMapping("/updatePolicySMSTask")
  @ApiOperation("Update Policy SMS Task")
  public ResponseEntity<DataWrapper> updatePolicySMSTask(
      @Valid @RequestBody PolicyNotificationTask task) {
    return ResponseEntity
        .ok(new DataWrapper(notificationService.updatePolicyNotificationTask(task)));
  }

  @PostMapping("/sendInstantSMSTask")
  @ApiOperation("Send Instant SMS Task")
  public ResponseEntity<DataWrapper> sendInstantSMSTask(
      @Valid @RequestBody PolicyNotificationTask task) {
    return ResponseEntity
        .ok(new DataWrapper(notificationService.sendInstantNotificationTask(task)));
  }

  @PostMapping("/cancelPolicySMSTask")
  @ApiOperation("Update Policy SMS Task")
  public ResponseEntity<DataWrapper> cancelPolicySMSTask(
      @Valid @RequestBody PolicyNotificationTask task) {
    return ResponseEntity
        .ok(new DataWrapper(notificationService.cancelPolicyNotificationTask(task)));
  }

  @PostMapping("/sendSNSNotification")
  @ApiOperation("Send SNS notification")
  public ResponseEntity<DataWrapper> sendSNSNotification(
      @Valid @RequestBody SNSNotificationRequest request) {
    return ResponseEntity
        .ok(new DataWrapper(
            SNSNotificationResponse.builder().messageId(snsService.sendSNSNotification(request))
                .build()));
  }

  @PostMapping("/cognitoSNS/addOrUpdate")
  @ApiOperation("CognitoSns add or update")
  public ResponseEntity<DataWrapper> addOrUpdateCognitoSns(
      @Valid @RequestBody CognitoSNSRequest request) {
    return ResponseEntity
        .ok(new DataWrapper(
            ResultResponse.builder().result(cognitoSnsService.addOrUpdate(request)).build()));
  }
}
