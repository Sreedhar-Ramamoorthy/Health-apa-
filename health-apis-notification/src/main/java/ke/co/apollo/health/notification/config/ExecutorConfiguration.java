package ke.co.apollo.health.notification.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ExecutorConfiguration {

  @Value("${executor.pool.core.size}")
  private int corePoolSize;
  @Value("${executor.pool.max.size}")
  private int maxPoolSize;
  @Value("${executor.queue.capacity}")
  private int queueCapacity;

  @Bean(name = "taskExecutor1")
  public Executor taskExecutor1() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("task-executor1-");
    executor.setMaxPoolSize(corePoolSize);
    executor.setCorePoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    return executor;
  }

  @Bean(name = "taskExecutor2")
  public Executor taskExecutor2() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("task-executor2-");
    executor.setMaxPoolSize(corePoolSize);
    executor.setCorePoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    return executor;
  }

}
