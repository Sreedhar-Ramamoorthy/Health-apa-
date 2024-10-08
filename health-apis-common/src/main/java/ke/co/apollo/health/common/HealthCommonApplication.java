package ke.co.apollo.health.common;

import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HealthCommonApplication {

  private static final Logger logger = LoggerFactory.getLogger(HealthCommonApplication.class);

  public static void main(String[] args) {

    Environment env = SpringApplication.run(HealthCommonApplication.class).getEnvironment();
    String protocol = "http";
    try {
      logger.debug(
          "\n----------------------------------------------------------\n\t"
              + "Application '{}' is running! Access URLs:\n\t"
              + "Local: \t\t{}://localhost:{}\n\t"
              + "External: \t{}://{}:{}\n\t"
              + "Profile(s): \t{}\n----------------------------------------------------------",
          env.getProperty("spring.application.name"),
          protocol,
          env.getProperty("server.port"),
          protocol,
          InetAddress.getLocalHost().getHostAddress(),
          env.getProperty("server.port"),
          env.getActiveProfiles());
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
}
