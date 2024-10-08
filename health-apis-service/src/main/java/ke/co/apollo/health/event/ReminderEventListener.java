package ke.co.apollo.health.event;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async
public class ReminderEventListener implements ApplicationListener<ReminderEvent> {

  @Autowired
  private QueueMessagingTemplate queueMessagingTemplate;

  @Autowired
  private Gson gson;

  @Value("${cloud.aws.sqs.intermediary.end-point.uri}")
  private String intermediarySqsEndpoint;

  @Override
  public void onApplicationEvent(ReminderEvent event) {
    queueMessagingTemplate.send(intermediarySqsEndpoint,
        MessageBuilder.withPayload(gson.toJson(event.getReminderRequest()))
            .build()
    );
  }
}
