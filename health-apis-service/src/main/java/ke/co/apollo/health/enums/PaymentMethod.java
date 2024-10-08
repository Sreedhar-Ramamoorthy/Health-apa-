package ke.co.apollo.health.enums;

public enum PaymentMethod {

  MPESA("Mpesa"), BANKTRANSFER("BankTransfer");

  private String value;

  PaymentMethod(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static PaymentMethod getPaymentMethod(String value){
    for (PaymentMethod type : PaymentMethod.values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    return null;
  }

}
