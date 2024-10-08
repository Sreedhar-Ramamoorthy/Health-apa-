package ke.co.apollo.health.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormatUtils {

  private static Logger logger = LoggerFactory.getLogger(FormatUtils.class);

  private FormatUtils() {
  }

  public static Long string2Long(String str) {
    Long id = null;
    if (StringUtils.isNotBlank(str)) {
      try {
        id = Long.valueOf(str);
      } catch (Exception e) {
        logger.error("format error: {}", e.getMessage());
      }
    }
    return id;
  }

  public static String codeFormat(String code, int id, int length) {
    String fillStr = "";
    int fillLength = length - StringUtils.length(code) - StringUtils.length(String.valueOf(id));
    if (fillLength > 0) {
      fillStr = StringUtils.repeat("0", fillLength);
    }
    return StringUtils.defaultIfEmpty(code, "") + fillStr + id;
  }

  public static BigDecimal scaleValue(BigDecimal value) {
    return value.setScale(0, RoundingMode.UP);
  }

  public static BigDecimal scaleValueHalfUp(BigDecimal value) {
    return value.setScale(0,  RoundingMode.HALF_UP);
  }
  public static BigDecimal scaleValue2(BigDecimal value) {
    return value.setScale(2, RoundingMode.UP);
  }
}
