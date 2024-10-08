package ke.co.apollo.health.policy.config;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import javax.sql.DataSource;
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
  DataSource healthDataSource;

  @Autowired
  DataSource hmsDataSource;

  @Bean
  public ApplicationRunner runner() {
    return args -> {
      if (!"local".equals(environment.getActiveProfiles()[0]) && eagerHikariPool) {
        Connection connection1 = healthDataSource.getConnection();
        log.info("healthDataSource connection1: {}", connection1);
        HikariDataSourcePoolMetadata hikariDataSourcePoolMetadata1 = new HikariDataSourcePoolMetadata(
            (HikariDataSource)healthDataSource);
        log.info("healthDataSource datasource1 active: {}, idle: {}, mix: {}, max: {}",
            hikariDataSourcePoolMetadata1.getActive(),
            hikariDataSourcePoolMetadata1.getIdle(),
            hikariDataSourcePoolMetadata1.getMin(),
            hikariDataSourcePoolMetadata1.getMax());

        Connection connection2 = hmsDataSource.getConnection();
        log.info("hmsDataSource connection2: {}", connection2);
        HikariDataSourcePoolMetadata hikariDataSourcePoolMetadata2 = new HikariDataSourcePoolMetadata(
            (HikariDataSource)hmsDataSource);
        log.info("hmsDataSource datasource2 active: {}, idle: {}, mix: {}, max: {}",
            hikariDataSourcePoolMetadata2.getActive(),
            hikariDataSourcePoolMetadata2.getIdle(),
            hikariDataSourcePoolMetadata2.getMin(),
            hikariDataSourcePoolMetadata2.getMax());
      }
    };
  }
}
