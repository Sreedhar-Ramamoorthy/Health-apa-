package ke.co.apollo.health.common.enums;

public enum PolicyRenewalType {
  BEFORE30DAYS("B30"), BEFORE14DAYS("B14"), BEFORE1DAY("B1"), AFTER14DAYS("A14"), AFTER29DAYS(
      "A29");

  private String value;

  PolicyRenewalType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
