package ke.co.apollo.health.common.enums;

public enum QuoteNumberEnum {

  HJ("HJ"), HA("HA"), HF("HF"), HS("HJS");

  private String value;

  QuoteNumberEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
