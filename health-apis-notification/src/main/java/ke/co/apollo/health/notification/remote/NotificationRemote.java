package ke.co.apollo.health.notification.remote;

import ke.co.apollo.health.common.domain.model.request.EmailAttachmentRequest;
import ke.co.apollo.health.common.domain.model.request.EmailRequest;
import ke.co.apollo.health.common.domain.model.request.SMSMessageRequest;
import ke.co.apollo.health.common.domain.model.response.SMSMessageResponse;

public interface NotificationRemote {

  SMSMessageResponse sendSMSMessage(SMSMessageRequest smsMessageRequest);

  String sendEmail(EmailRequest request);
  String sendEmail(EmailAttachmentRequest request);
}
