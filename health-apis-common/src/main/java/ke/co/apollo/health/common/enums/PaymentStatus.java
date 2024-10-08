package ke.co.apollo.health.common.enums;

import org.apache.commons.lang3.StringUtils;

public enum PaymentStatus {

  PENDING("PENDING"), SUCCESSFUL("SUCCESSFUL"), FAILED("FAILED"), INIT("INIT");

  private String value;

  PaymentStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static PaymentStatus getPaymentStatus(String value) {
    if (StringUtils.isNotEmpty(value)) {
      for (PaymentStatus paymentStatus : PaymentStatus.values()) {
        if (paymentStatus.getValue().equals(value)) {
          return paymentStatus;
        }
      }
    }
    return INIT;
  }
}
