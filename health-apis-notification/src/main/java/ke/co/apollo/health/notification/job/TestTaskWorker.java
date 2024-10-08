package ke.co.apollo.health.notification.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TestTaskWorker {

  private Logger logger = LoggerFactory.getLogger(getClass());

//  @Scheduled(cron = "0/1 * * * * ?")
  @Async("taskExecutor1")
  public void doTask1() throws InterruptedException {
    logger.debug("task1 run == {}", Thread.currentThread().getName());
    Thread.sleep(30000);
    logger.debug("task1 end == {}", Thread.currentThread().getName());
  }

//  @Scheduled(cron = "0/1 * * * * ?")
  @Async("taskExecutor2")
  public void doTask2() throws InterruptedException {
    logger.debug("task2 run == {}", Thread.currentThread().getName());
    Thread.sleep(30000);
    logger.debug("task2 end == {}", Thread.currentThread().getName());
  }
}
