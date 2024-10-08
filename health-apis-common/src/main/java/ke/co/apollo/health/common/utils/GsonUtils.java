package ke.co.apollo.health.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ke.co.apollo.health.common.constants.GlobalConstant;

/**
 * {@code }
 *
 * <p> Gson Utils </p >
 *
 * @author Andy
 * @version 1.0
 * @see
 * @since 7/10/2020
 */
public class GsonUtils {

  private GsonUtils() {
  }

  public static Gson createGson() {
    GsonBuilder builder = new GsonBuilder().serializeNulls()
        .setDateFormat(GlobalConstant.YYYYMMDD_HHMMSS);
    return builder.create();
  }

  public static Gson createDefaultGson() {
    GsonBuilder builder = new GsonBuilder().setDateFormat(GlobalConstant.YYYYMMDD_HHMMSS);
    return builder.create();
  }
}
