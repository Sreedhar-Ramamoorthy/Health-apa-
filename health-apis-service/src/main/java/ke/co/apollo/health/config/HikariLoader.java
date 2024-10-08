package ke.co.apollo.health.config;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HikariLoader {

  @Value("${eager.hikari.pool:true}")
  boolean eagerHikariPool;

  @Autowired
  Environment environment;

  @Autowired
  HikariDataSource healthDataSource;

  @Bean
  public ApplicationRunner runner() {
    return args -> {
      if (!"local".equals(environment.getActiveProfiles()[0]) && eagerHikariPool) {
        Connection connection = healthDataSource.getConnection();
        log.info("connection: {}", connection);
        HikariDataSourcePoolMetadata hikariDataSourcePoolMetadata = new HikariDataSourcePoolMetadata(
            healthDataSource);
        log.info("datasource active: {}, idle: {}, mix: {}, max: {}",
            hikariDataSourcePoolMetadata.getActive(),
            hikariDataSourcePoolMetadata.getIdle(),
            hikariDataSourcePoolMetadata.getMin(),
            hikariDataSourcePoolMetadata.getMax());
      }
    };
  }
}
