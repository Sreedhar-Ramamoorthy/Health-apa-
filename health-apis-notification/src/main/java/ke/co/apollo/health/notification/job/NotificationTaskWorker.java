package ke.co.apollo.health.notification.job;

import java.time.LocalDateTime;
import ke.co.apollo.health.notification.service.NotificationService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationTaskWorker {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private boolean reminderSuccessStatus;

  @Autowired
  NotificationService notificationService;

  //  @Scheduled(cron = "0/5 * * * * ?")
//  @SchedulerLock(name = "SendReminder", lockAtLeastFor = "5s", lockAtMostFor = "5s")
  public void sendReminder() {
    logger.debug("=====send reminder cron started: {}====", LocalDateTime.now());
    reminderSuccessStatus = notificationService.sendReminder();
    logger.debug("=====send reminder cron completed: {}====", LocalDateTime.now());
  }

  //    @Scheduled(cron = "0/10 * * * * ?")
//    @SchedulerLock(name = "SendPolicyNotification", lockAtLeastFor = "10s", lockAtMostFor = "10s")

  /**
   * Runs every 10 minutes
   */
  @Scheduled(cron = "${cronExpression.policy.notification.reminder}", zone = "${scheduler.zone}")
//  @Scheduled(cron = "0 0/10 * * * ?")
  @SchedulerLock(name = "SendPolicyNotification", lockAtLeastFor = "PT10M", lockAtMostFor = "PT10M")
  public void sendPolicyNotification() {
    logger.debug("=====send policy notification reminder cron started: {}====", LocalDateTime.now());
    reminderSuccessStatus = notificationService.sendPolicyNotificationReminder();
    logger.debug("=====send policy notification reminder cron completed: {}====",
        LocalDateTime.now());
  }

  //  @Scheduled(cron = "*/30 * * * * ?")
//  @SchedulerLock(name = "CreateCustomerNotification", lockAtLeastFor = "30s", lockAtMostFor = "30s")

  /**
   * Runs every day at 01-03:00:00 AM
   */
  @Scheduled(cron = "${cronExpression.policy.renewal.notification.task}", zone = "${scheduler.zone}")
//  @Scheduled(cron = "0 0 1-3 * * ?")   //Kenya Time = GMT + 3
  @SchedulerLock(name = "CreatePolicyRenewalNotificationTask", lockAtLeastFor = "PT60M", lockAtMostFor = "PT60M")
  public void createPolicyRenewalNotificationTask() {
    logger.debug("=====create customer policy renewal notification task cron started: {}====",
        LocalDateTime.now());
    reminderSuccessStatus = notificationService.createPolicyRenewalNotificationTask();
    logger.debug("=====create customer policy renewal notification task completed: {}====",
        LocalDateTime.now());
  }

  public boolean getReminderSuccessStatus() {
    return reminderSuccessStatus;
  }
}
