package ke.co.apollo.health.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:question.properties")
@ConfigurationProperties(prefix = "health.question")
@Getter
@Setter
public class QuestionConfig {

  private Map<String, String> questionMap = new HashMap<>();

  public Map<String, String> getQuestionMap() {
    return questionMap;
  }

}
