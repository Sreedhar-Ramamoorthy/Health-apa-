package ke.co.apollo.health.event;

import ke.co.apollo.health.common.domain.model.PolicyNotificationTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PolicyNotificationEventPublisher {

  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  public void publishTask(PolicyNotificationTask policyNotificationTask) {
    PolicyNotificationEvent policyNotificationEvent = new PolicyNotificationEvent(this, policyNotificationTask);
    applicationEventPublisher.publishEvent(policyNotificationEvent);
  }

}
