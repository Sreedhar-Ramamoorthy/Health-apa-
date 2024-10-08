package ke.co.apollo.health.event;

import ke.co.apollo.health.common.domain.model.request.ReminderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ReminderEventPublisher {

  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  public void publishReminder(ReminderRequest reminderRequest) {
    ReminderEvent reminderEvent = new ReminderEvent(this, reminderRequest);
    applicationEventPublisher.publishEvent(reminderEvent);
  }

}
