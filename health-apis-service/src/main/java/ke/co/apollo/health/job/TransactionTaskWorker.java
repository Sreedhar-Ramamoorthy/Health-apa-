package ke.co.apollo.health.job;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ke.co.apollo.health.service.TransactionDetailService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
public class TransactionTaskWorker {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  TransactionDetailService transactionDetailService;

//  @Scheduled(cron = "0 */5 * * * ?")
  @SchedulerLock(name = "ProcessTransactionDetailTask", lockAtLeastFor = "5s", lockAtMostFor = "5s")
  public void sendNotification() {
    logger.debug("=====process transaction detail cron started: {}====", LocalDateTime.now());
    transactionDetailService.processTransactionDetailTask();
    logger.debug("=====process transaction detail completed: {}====", LocalDateTime.now());
  }


}
