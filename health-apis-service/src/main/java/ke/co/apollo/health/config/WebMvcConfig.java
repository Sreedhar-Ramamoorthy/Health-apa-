package ke.co.apollo.health.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${spring.server.maxThreads}")
  private int maxThreads;
  @Value("${spring.server.maxConnections}")
  private int maxConnections;
  @Value("${spring.server.connectionTimeout}")
  private int connectionTimeout;
  @Value("${health.rest.connection.maxConnections}")
  private int maxConnectionsRest;
  @Value("${health.rest.connection.maxPerRoute}")
  private int maxPerRouteRest;
  @Value("${health.rest.connection.connection-request-timeout}")
  private int connectionTimeoutRest;

  @Bean
  @ConfigurationProperties(prefix = "health.rest.connection")
  public HttpComponentsClientHttpRequestFactory httpRequestFactory() {
    return new HttpComponentsClientHttpRequestFactory(httpClient());
  }

  @Bean
  public HttpClient httpClient() {
    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.getSocketFactory())
        .register("https", SSLConnectionSocketFactory.getSocketFactory())
        .build();
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
    connectionManager.setMaxTotal(maxConnectionsRest);
    connectionManager.setDefaultMaxPerRoute(maxPerRouteRest);
    connectionManager.setValidateAfterInactivity(5);
    RequestConfig requestConfig = RequestConfig.custom()
        .setSocketTimeout(connectionTimeoutRest)
        .setConnectTimeout(connectionTimeoutRest)
        .setConnectionRequestTimeout(connectionTimeoutRest)
        .build();
    return HttpClientBuilder.create()
        .setDefaultRequestConfig(requestConfig)
        .setConnectionManager(connectionManager)
        .build();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate(httpRequestFactory());
  }

  @Bean
  public Gson gson() {
    GsonBuilder builder = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss");
    return builder.create();
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**").addResourceLocations(
        "classpath:/static/");
    registry.addResourceHandler("swagger-ui.html").addResourceLocations(
        "classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**").addResourceLocations(
        "classpath:/META-INF/resources/webjars/");
  }

  @Bean
  public ConfigurableServletWebServerFactory webServerFactory() {
    TomcatServletWebServerFactory tomcatFactory = new TomcatServletWebServerFactory();
    tomcatFactory.addConnectorCustomizers(new MyTomcatConnectorCustomizer());
    return tomcatFactory;
  }

  class MyTomcatConnectorCustomizer implements TomcatConnectorCustomizer {

    public void customize(Connector connector) {
      Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
      protocol.setMaxConnections(maxConnections);
      protocol.setMaxThreads(maxThreads);
      protocol.setConnectionTimeout(connectionTimeout);
    }
  }
}
