package ke.co.apollo.health.common.enums;

public enum PaymentStyle {
  BANK("Bank Transfer"), CASH("Cash/Cheque"), MPESA("Mpesa");

  private String value;

  PaymentStyle(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
