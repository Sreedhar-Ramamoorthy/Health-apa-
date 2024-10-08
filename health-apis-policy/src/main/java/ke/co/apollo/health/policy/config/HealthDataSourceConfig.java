package ke.co.apollo.health.policy.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@MapperScan(
    basePackages = "ke.co.apollo.health.policy.mapper.health",
    sqlSessionFactoryRef = "healthDataSqlSessionFactory")
public class HealthDataSourceConfig {

  @Bean(name = "healthDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.health")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "healthDataSqlSessionFactory")
  public SqlSessionFactory sqlSessionFactory(@Qualifier("healthDataSource") DataSource ds)
      throws Exception {
    SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(ds);
    return bean.getObject();
  }

  @Bean(name = "healthDataSqlSessionTemplate")
  public SqlSessionTemplate sqlSessionTemplate(
      @Qualifier("healthDataSqlSessionFactory") SqlSessionFactory sessionFactory) {
    return new SqlSessionTemplate(sessionFactory);
  }

  @Bean(name = "healthDataTransactionManager")
  public DataSourceTransactionManager transactionManager(
      @Qualifier("healthDataSource") DataSource ds) {
    return new DataSourceTransactionManager(ds);
  }
}
