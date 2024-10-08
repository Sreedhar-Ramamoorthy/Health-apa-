package ke.co.apollo.health.common.enums;

public enum IntermediaryRole {
  ADMIN("admin"), USER("user");

  private String value;

  IntermediaryRole(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
