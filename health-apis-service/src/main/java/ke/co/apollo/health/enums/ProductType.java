package ke.co.apollo.health.enums;

public enum ProductType {
  MOTOR("motor"), HEALTH("health"), OTHER("other");

  private String value;

  ProductType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
