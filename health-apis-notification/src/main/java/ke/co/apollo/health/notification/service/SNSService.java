package ke.co.apollo.health.notification.service;


import ke.co.apollo.health.common.domain.model.request.SNSNotificationRequest;

public interface SNSService {

  String sendSNSNotification(SNSNotificationRequest request);

  String registerWithSNS(String customerId, String firebaseToken);

}
