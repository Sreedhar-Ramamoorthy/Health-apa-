package ke.co.apollo.health.job;

import java.time.LocalDateTime;
import ke.co.apollo.health.service.PolicyService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CustomerPolicyCacheTaskWorker {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  PolicyService policyService;

  //  @Scheduled(cron = "0 0 3 * * ?")
  @Scheduled(cron = "${cronExpression.policy.cache.update.task}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "ProcessUpdateCustomerPolicyCacheTask", lockAtLeastFor = "PT60M", lockAtMostFor = "PT60M")
//  @Scheduled(cron = "*/60 * * * * ?")
//  @SchedulerLock(name = "ProcessUpdateCustomerPolicyCacheTask", lockAtLeastFor = "60s", lockAtMostFor = "60s")
  public void updateCustomerPolicyCache() {
    logger.debug("=====process update customer policy cache cron started: {}====",
        LocalDateTime.now());
    policyService.processUpdateCustomerPolicyCacheTask();
    logger
        .debug("=====process update customer policy cache completed: {}====", LocalDateTime.now());
  }

}
