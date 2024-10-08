package ke.co.apollo.health.notification.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class SNSClientHelper {

  @Value("${cloud.aws.region.static}")
  private String region;

  @Bean
  public AmazonSNS initClient(Environment environment) {
    AmazonSNS client;
    if ("local".equals(environment.getActiveProfiles()[0])) {
      AWSCredentials credentials = new BasicAWSCredentials(
          environment.getProperty("apa.super-apis.cognito.access_key"),
          environment.getProperty("apa.super-apis.cognito.access_secret"));
      AWSCredentialsProvider credProvider = new AWSStaticCredentialsProvider(credentials);
      client = AmazonSNSClientBuilder.standard()
          .withCredentials(credProvider)
          .withRegion(region)
          .build();
    } else {
      client = AmazonSNSClientBuilder.standard()
          .withRegion(region)
          .build();
    }
    return client;
  }

}
