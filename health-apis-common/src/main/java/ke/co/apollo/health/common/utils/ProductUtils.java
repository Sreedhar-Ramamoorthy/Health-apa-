package ke.co.apollo.health.common.utils;

import java.math.BigDecimal;
import ke.co.apollo.health.common.constants.GlobalConstant;

public class ProductUtils {

  private ProductUtils() {
  }

  public static BigDecimal calculateITL(BigDecimal premium) {
    return premium.multiply(GlobalConstant.ITL_RATE);
  }

  public static BigDecimal calculatePHCF(BigDecimal premium) {
    return premium.multiply(GlobalConstant.PHCF_RATE);
  }

}
