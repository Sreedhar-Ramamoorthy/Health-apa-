package ke.co.apollo.health.config;

import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.request.EmailRequest;
import ke.co.apollo.health.common.domain.model.request.InAppNotificationCreateRequest;
import ke.co.apollo.health.common.domain.model.request.SMSMessageRequest;
import ke.co.apollo.health.domain.entity.PolicyOverComingEntity;
import ke.co.apollo.health.remote.NotificationRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Configuration
@EnableAsync
public class PolicyRenewalExecutorConfiguration {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    NotificationMessageBuilder notificationMessageBuilder;
    @Autowired
    NotificationRemote notificationRemote;
    private static final int TIME_INTERVAL_SECONDS = 5;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    int batchSize = 20;


    public void sendNotificationsAsync(List<PolicyOverComingEntity> toBeRenewal, boolean isRenewal) throws InterruptedException {
        logger.debug("Start sending Notification...{}: " , toBeRenewal.size() );
        int totalBatches = (int) Math.ceil((double) toBeRenewal.size() / batchSize);
        scheduler= Executors.newScheduledThreadPool(totalBatches);
        for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
            int startIndex = batchIndex * batchSize;
            int endIndex = Math.min((batchIndex + 1) * batchSize, toBeRenewal.size());
            List<PolicyOverComingEntity> batch = toBeRenewal.subList(startIndex, endIndex);
            long delay = batchIndex * TIME_INTERVAL_SECONDS;
            batch.forEach(policyOverComingEntity -> scheduledSendNotificationsAsync(policyOverComingEntity, isRenewal, delay));
        }
        Thread.sleep(totalBatches*TIME_INTERVAL_SECONDS*1000);
        try {
            scheduler.shutdown();
            logger.debug("Renewal notification done for {} records" , toBeRenewal.size() );
        } catch (Exception e) {
            logger.error("Renewal notification error {}", e.getMessage());
        }
    }

    public void scheduledSendNotificationsAsync(PolicyOverComingEntity policyOverComingEntity, boolean isRenewal, long delay) {
        scheduler.schedule(() -> {
            sendNotification(policyOverComingEntity, isRenewal);
        }, delay, TimeUnit.SECONDS);
    }

    public void sendNotification(PolicyOverComingEntity policyOverComingEntity, boolean isRenewal) {
        logger.debug("Scheduled Notifications: {} ");
        String phoneNumber = policyOverComingEntity.getMobile();
        String email = policyOverComingEntity.getEmail();
        String name = policyOverComingEntity.getAgentName();
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(policyOverComingEntity.getRenewalDate());
        String message = "";
        if (isRenewal) {
            message = notificationMessageBuilder.getMessage("SMS_MESSAGE_RENEWAL_POLICY", name, date, date);
        } else {
            message = notificationMessageBuilder.getMessage("SMS_MESSAGE_LAPSED_POLICY", name, date);
        }
        if (phoneNumber != null) {
            notificationRemote
                    .queueSMSMessage(
                            SMSMessageRequest.builder().from(GlobalConstant.APOLLO_GROUP)
                                    .to(phoneNumber)
                                    .text(message)
                                    .serviceType(GlobalConstant.HEALTH_SERVICE_TYPE).build());

            notificationRemote.createInAppNotification(
                    InAppNotificationCreateRequest.builder()
                            .notification(message)
                            .readStatus(GlobalConstant.READ_STATUS)
                            .phoneNumber(phoneNumber)
                            .email(email)
                            .serviceType(GlobalConstant.HEALTH_SERVICE_TYPE)
                            .actionStatus(GlobalConstant.HEALTH_RENEWAL_NOTIFICATION_STATUS)
                            .notificationSubject(GlobalConstant.HEALTH_RENEWAL_NOTIFICATION_TITLE)
                            .policyNumber(policyOverComingEntity.getPolicyNumber())
                            .build()
            );
        }
        if (email != null) {
            notificationRemote.queueEmailMessage(
                    EmailRequest.builder().emailAddress(email)
                            .text(message)
                            .subject("" + policyOverComingEntity.getPolicyNumber()
                            ).build());
        }
    }

}
