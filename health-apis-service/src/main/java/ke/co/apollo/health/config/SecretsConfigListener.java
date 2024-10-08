package ke.co.apollo.health.config;

import ke.co.apollo.health.common.config.SecretsConfigHelper;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SecretsConfigListener extends SecretsConfigHelper
    implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  @Override
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    super.setEnvironmentProperties(event);
  }
}
