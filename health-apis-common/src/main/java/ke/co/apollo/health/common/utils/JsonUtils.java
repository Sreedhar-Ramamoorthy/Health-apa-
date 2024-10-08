package ke.co.apollo.health.common.utils;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class JsonUtils {

  private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

  private JsonUtils() {

  }

  public static <T> T readJsonFromClassPath(String path, Type typeOfT) {
    ClassPathResource resource = new ClassPathResource(path);
    if (resource.exists()) {
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(resource.getInputStream()));) {
        return GsonUtils.createGson().fromJson(reader, typeOfT);
      } catch (Exception e) {
        logger.error("read json file error: {}", e.getMessage());
      }
    }
    return null;
  }

  public static String objectToJson(Object obj) {
    return GsonUtils.createGson().toJson(obj);
  }

  public static <T> List<T> jsonStringConvertToList(String string, Class<T[]> cls) {
    Gson gson = GsonUtils.createGson();
    T[] array = gson.fromJson(string, cls);
    return Arrays.asList(array);
  }

  public static <T> List<T> objectConvertToList(Object obj, Class<T[]> cls) {
    return jsonStringConvertToList(objectToJson(obj),cls);
  }
}
