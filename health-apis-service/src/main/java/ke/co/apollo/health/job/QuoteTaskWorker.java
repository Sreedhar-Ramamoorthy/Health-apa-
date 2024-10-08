package ke.co.apollo.health.job;

import java.time.LocalDateTime;
import ke.co.apollo.health.service.QuoteService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QuoteTaskWorker {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  QuoteService quoteService;


  /**
   * Runs every 30 minutes
   */
  @Scheduled(cron = "${cronExpression.policy.active.notification.task}", zone = "${scheduler.zone}")
//  @Scheduled(cron = "0 0/10 * * * ?")
//  @Scheduled(cron = "0/30 * * * * ?")
  @SchedulerLock(name = "createActivedQuoteTask", lockAtLeastFor = "PT30M", lockAtMostFor = "PT30M")
  public void createActivedQuoteNotificationTask() {
    logger.debug("=====create actived quote notification task started: {}====", LocalDateTime.now());
    quoteService.createActivedQuoteNotificationTask();
    logger.debug("=====create actived quote notification task completed: {}====", LocalDateTime.now());
  }

}
