package ke.co.apollo.health.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource({"classpath:notification-message.properties"})

public class NotificationMessageBuilder {

  @Autowired
  private Environment env;

  public String getMessage(String key) {
    return env.getProperty(key);
  }

  public String getMessage(String key, String... dynamicMessages) {
    String message = env.getProperty(key);
    return String.format(message, dynamicMessages);
  }
}
