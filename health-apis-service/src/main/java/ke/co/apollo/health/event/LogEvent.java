package ke.co.apollo.health.event;

import ke.co.apollo.health.common.domain.model.UserLog;
import org.springframework.context.ApplicationEvent;

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
public class LogEvent extends ApplicationEvent {

  private UserLog userStepsLog;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with which the event is
   * associated (never {@code null})
   */
  public LogEvent(Object source, UserLog message) {
    super(source);
    this.userStepsLog = message;
  }

  public UserLog getUserStepsLog() {
    return userStepsLog;
  }
}
