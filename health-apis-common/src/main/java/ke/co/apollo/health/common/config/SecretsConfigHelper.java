package ke.co.apollo.health.common.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

public class SecretsConfigHelper {

  private static final Logger logger = LoggerFactory.getLogger(SecretsConfigHelper.class);

  private AWSSecretsManager client;

  public SecretsConfigHelper() {
    client = AWSSecretManagerHelper.buildClient("EU_WEST_1");
  }

  public String getSecret(String secretName) {

    GetSecretValueRequest getSecretValueRequest =
        new GetSecretValueRequest().withSecretId(secretName);
    GetSecretValueResult getSecretValueResult = null;

    try {
      getSecretValueResult = client.getSecretValue(getSecretValueRequest);
    } catch (DecryptionFailureException e) {

      logger.error("Failed to Decrypt secret text");
      throw e;
    } catch (InternalServiceErrorException
        | InvalidParameterException
        | InvalidRequestException
        | ResourceNotFoundException e) {

      throw e;
    }

    if (getSecretValueResult.getSecretString() != null) {
      return getSecretValueResult.getSecretString();
    } else {
      return new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
    }
  }

  public void setEnvironmentProperties(ApplicationEnvironmentPreparedEvent event) {
    ConfigurableEnvironment environment = event.getEnvironment();
    String envName = environment.getProperty("spring.profiles.active");
    logger.debug("profile: {} ", envName);
    if (StringUtils.isBlank(envName) || "local".equals(envName)) {
      return;
    }
    String secretName = envName.concat("-health1");
    logger.info("secret: {} ", secretName);
    Properties props = new Properties();
    String response = getSecret(secretName);

    logger.debug("Get value for secret: {}", secretName);

    JSONObject jsonObject = new JSONObject(response);
    Iterator<String> keys = jsonObject.keys();
    while (keys.hasNext()) {
      String key = keys.next();
      String value = jsonObject.getString(key);
      logger.debug("{} : {}", key, value);
      props.put(key, value);
    }

    logger.debug("Add properties to environment size:{}", props.size());
    environment
        .getPropertySources()
        .addFirst(new PropertiesPropertySource("aws.secret.manager", props));
  }
}
