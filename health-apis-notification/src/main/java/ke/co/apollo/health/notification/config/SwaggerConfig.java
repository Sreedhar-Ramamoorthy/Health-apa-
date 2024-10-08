package ke.co.apollo.health.notification.config;

import ke.co.apollo.health.common.config.SwaggerCommonConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig extends SwaggerCommonConfig {

  @Bean
  @Override
  public Docket api() {
    return super.api();
  }

}
