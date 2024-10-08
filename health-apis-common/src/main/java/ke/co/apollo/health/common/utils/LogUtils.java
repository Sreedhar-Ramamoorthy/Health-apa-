package ke.co.apollo.health.common.utils;

import ke.co.apollo.health.common.enums.PositionType;
import org.apache.commons.lang3.StringUtils;

public class LogUtils {

  private LogUtils() {
  }

  public static String overlayValue(String message, PositionType place) {
    if (StringUtils.isBlank(message)) {
      return message;
    }
    int len = message.length();
    int half = len / 2;
    if (PositionType.LEFT == place) {
      message = StringUtils
          .overlay(message, StringUtils.repeat("*", half), 0, half);
    } else if (PositionType.MIDDLE == place) {
      int left = len / 3;
      int right = len * 2 / 3;
      message = StringUtils
          .overlay(message, StringUtils.repeat("*", right - left), left, right);
    } else if (PositionType.RIGHT == place) {
      message = StringUtils.overlay(message, StringUtils.repeat("*", len - half), half, len);
    }
    return message;

  }

}
