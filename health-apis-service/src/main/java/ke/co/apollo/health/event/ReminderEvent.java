package ke.co.apollo.health.event;

import ke.co.apollo.health.common.domain.model.request.ReminderRequest;
import org.springframework.context.ApplicationEvent;

public class ReminderEvent extends ApplicationEvent {

  private ReminderRequest reminderRequest;

  public ReminderEvent(Object source, ReminderRequest reminderRequest) {
    super(source);
    this.reminderRequest = reminderRequest;
  }

  public ReminderRequest getReminderRequest() {
    return reminderRequest;
  }
}
