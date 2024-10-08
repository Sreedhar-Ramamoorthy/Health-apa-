package ke.co.apollo.health.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:policy-status.properties")
@ConfigurationProperties(prefix = "health.policy")
@Getter
@Setter
public class PolicyStatusConfig {

    private Map<String, String> statusMap = new HashMap<>();

    public Map<String, String> getStatusMap(){
        return statusMap;
    }

}
