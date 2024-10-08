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
    basePackages = "ke.co.apollo.health.policy.mapper.hms",
    sqlSessionFactoryRef = "hmsDataSqlSessionFactory")
public class HMSDataSource {

  @Bean(name = "hmsDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.hms")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "hmsDataSqlSessionFactory")
  public SqlSessionFactory sqlSessionFactory(@Qualifier("hmsDataSource") DataSource ds)
      throws Exception {
    SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(ds);
    return bean.getObject();
  }

  @Bean(name = "hmsDataSqlSessionTemplate")
  public SqlSessionTemplate sqlSessionTemplate(
      @Qualifier("hmsDataSqlSessionFactory") SqlSessionFactory sessionFactory) {
    return new SqlSessionTemplate(sessionFactory);
  }

  @Bean(name = "hmsDataTransactionManager")
  public DataSourceTransactionManager transactionManager(
      @Qualifier("hmsDataSource") DataSource ds) {
    return new DataSourceTransactionManager(ds);
  }
}
