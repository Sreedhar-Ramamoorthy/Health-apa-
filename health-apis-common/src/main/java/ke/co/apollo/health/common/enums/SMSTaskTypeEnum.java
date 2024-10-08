package ke.co.apollo.health.common.enums;

public enum SMSTaskTypeEnum {

  NO_PAY("NoPay"), PAY("Pay"), ACTIVE("Active");

  private String value;

  SMSTaskTypeEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
