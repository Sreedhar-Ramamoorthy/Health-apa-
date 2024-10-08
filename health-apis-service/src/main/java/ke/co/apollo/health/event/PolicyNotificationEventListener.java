package ke.co.apollo.health.event;

import ke.co.apollo.health.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async
public class PolicyNotificationEventListener implements ApplicationListener<PolicyNotificationEvent> {


  @Autowired
  private QuoteService quoteService;


  @Override
  public void onApplicationEvent(PolicyNotificationEvent event) {
    quoteService.sendPolicySMSNotification(event.getPolicyNotificationTask());
  }
}
