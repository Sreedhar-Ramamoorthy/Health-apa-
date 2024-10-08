package ke.co.apollo.health.notification.service;

import java.util.List;
import ke.co.apollo.health.common.domain.model.PolicyNotificationTask;
import ke.co.apollo.health.common.domain.model.request.EmailAttachmentRequest;
import ke.co.apollo.health.common.domain.model.request.EmailRequest;
import ke.co.apollo.health.common.domain.model.request.SMSMessageRequest;
import ke.co.apollo.health.common.domain.model.response.SMSMessageResponse;

public interface NotificationService {

  SMSMessageResponse sendSMSMessageRequest(SMSMessageRequest smsMessageRequest);

  boolean sendSMSMessage(SMSMessageRequest smsMessageRequest);

  String sendPolicySMSMessage(SMSMessageRequest smsMessageRequest);

  boolean sendReminder();

  boolean createPolicyRenewalNotificationTask();

  boolean sendPolicyNotificationReminder();

  boolean sendEmail(EmailRequest request);

  boolean sendEmailAttachment(EmailAttachmentRequest request);

  List<PolicyNotificationTask> searchPolicyNotificationTask();

  boolean createPolicyNotificationTask(PolicyNotificationTask task);

  boolean updatePolicyNotificationTask(PolicyNotificationTask task);

  boolean deletePolicyNotificationTask(String taskId);

  boolean cancelPolicyNotificationTask(PolicyNotificationTask task);

  boolean sendInstantNotificationTask(PolicyNotificationTask task);

}
