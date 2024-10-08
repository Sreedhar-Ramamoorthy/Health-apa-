package ke.co.apollo.health.event;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * {@code }
 *
 * <p>Custom Log Event Listener </p>
 *
 * @author wang
 * @version 1.0
 * @see
 * @since 2020/7/9
 */

@Component
@Async
public class CustomLogEventListener implements ApplicationListener<LogEvent> {

  @Autowired
  private QueueMessagingTemplate queueMessagingTemplate;

  @Autowired
  private Gson gson;

  @Value("${cloud.aws.sqs.health.end-point.uri}")
  private String sqsEndpoint;

  @Override
  public void onApplicationEvent(LogEvent event) {
    queueMessagingTemplate.send(
        sqsEndpoint,
        MessageBuilder
            .withPayload(gson.toJson(event.getUserStepsLog()))
            .build()
    );
  }
}
