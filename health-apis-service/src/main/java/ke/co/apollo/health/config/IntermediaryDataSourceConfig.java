package ke.co.apollo.health.config;

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
    basePackages = "ke.co.apollo.health.mapper.intermediary",
    sqlSessionFactoryRef = "intermediaryDataSqlSessionFactory")
public class IntermediaryDataSourceConfig {

  @Bean(name = "intermediaryDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.intermediary")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "intermediaryDataSqlSessionFactory")
  public SqlSessionFactory sqlSessionFactory(@Qualifier("intermediaryDataSource") DataSource ds)
      throws Exception {
    SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(ds);
    return bean.getObject();
  }

  @Bean(name = "intermediaryDataSqlSessionTemplate")
  public SqlSessionTemplate sqlSessionTemplate(
      @Qualifier("intermediaryDataSqlSessionFactory") SqlSessionFactory sessionFactory) {
    return new SqlSessionTemplate(sessionFactory);
  }

  @Bean(name = "intermediaryDataTransactionManager")
  public DataSourceTransactionManager transactionManager(
      @Qualifier("intermediaryDataSource") DataSource ds) {
    return new DataSourceTransactionManager(ds);
  }
}
