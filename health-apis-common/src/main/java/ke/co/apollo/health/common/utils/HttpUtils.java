package ke.co.apollo.health.common.utils;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


public class HttpUtils {

  private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

  private HttpUtils() {
  }

  public static HttpHeaders getAppJsonHeader() {
    Map<String, String> mdc = MDC.getCopyOfContextMap();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    if (MapUtils.isNotEmpty(mdc) && MDC.getCopyOfContextMap()
                                       .get("con_auth") != null) {
      headers.add("auth", MDC.getCopyOfContextMap().get("con_auth"));
    }
    return headers;
  }

  public static URL getWsdlURL(String profile, String wsdl) {
    URL url = null;
    String path = "wsdl/" + profile + "-" + wsdl;
    try {
      ClassPathResource resource = new ClassPathResource(path);
      url = resource.getURL();
    } catch (Exception e) {
      logger.error("Can not get the wsdl from {}, error: {}", path, e.getMessage());
    }
    return url;
  }
}
