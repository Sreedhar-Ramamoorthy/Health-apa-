package ke.co.apollo.health.event;

import ke.co.apollo.health.common.domain.model.UserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * {@code }
 *
 * <p> </p>
 *
 * @author wang
 * @version 1.0
 * @see
 * @since 2020/7/9
 */

@Component
public class CustomLogEventPublisher {

  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  public void publishLog(UserLog userLog) {
    LogEvent customSpringEvent = new LogEvent(this, userLog);
    applicationEventPublisher.publishEvent(customSpringEvent);
  }

}
