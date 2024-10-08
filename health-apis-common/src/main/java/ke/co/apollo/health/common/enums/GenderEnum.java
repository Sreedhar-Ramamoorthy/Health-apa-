package ke.co.apollo.health.common.enums;

public enum GenderEnum {
  MALE("Male"), FEMALE("Female");

  private String value;

  GenderEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
