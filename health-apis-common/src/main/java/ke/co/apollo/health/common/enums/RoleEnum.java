package ke.co.apollo.health.common.enums;

public enum RoleEnum {
  PRINCIPAL("Principal"), SPOUSE("Spouse"), CHILDREN("Children");

  private String value;

  RoleEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
