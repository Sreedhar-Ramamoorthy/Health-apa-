package ke.co.apollo.health.remote;

import ke.co.apollo.health.common.domain.model.PolicyNotificationTask;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.InAppNotificationMessageResponse;
import java.util.List;

public interface NotificationRemote {

  boolean sendSMSMessage(SMSMessageRequest smsMessageRequest);

  boolean sendEmail(EmailRequest request);

  boolean sendEmailWithTemplate(EmailAttachmentRequest request);

  boolean createPolicySMSTask(PolicyNotificationTask task);

  boolean cancelPolicySMSTask(PolicyNotificationTask task);

  boolean sendPolicyInstantSMSNotification(PolicyNotificationTask task);

  boolean queueSMSMessage(SMSMessageRequest smsMessageRequest);

  boolean queueEmailMessage(EmailRequest emailRequest);

  boolean createInAppNotification(InAppNotificationCreateRequest createRequest);

  boolean clearInAppNotification(List<Integer> request);

  List<InAppNotificationMessageResponse> getAllInAppNotificationList(InAppNotificationMessageRequest inAppNotificationMessageRequest);
}
