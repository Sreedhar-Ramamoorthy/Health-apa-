package ke.co.apollo.health.event;

import ke.co.apollo.health.common.domain.model.PolicyNotificationTask;
import org.springframework.context.ApplicationEvent;

public class PolicyNotificationEvent extends ApplicationEvent {

  private PolicyNotificationTask policyNotificationTask;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with which the event is
   * associated (never {@code null})
   */
  public PolicyNotificationEvent(Object source, PolicyNotificationTask message) {
    super(source);
    this.policyNotificationTask = message;
  }

  public PolicyNotificationTask getPolicyNotificationTask() {
    return policyNotificationTask;
  }
}
