package ke.co.apollo.health.common.enums;

public enum  PolicyState {
  ACTIVE("ACTIVE"),LAPSED("LAPSED"),CANCELLED("CANCELLED"),DRAFT("DRAFT"), REINSTATED("REINSTATED");

  private String value;

  PolicyState(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
