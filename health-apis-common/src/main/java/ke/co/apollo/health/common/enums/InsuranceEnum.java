package ke.co.apollo.health.common.enums;

public enum InsuranceEnum {
  ALL("all"), HEALTH("health"), LIFE("life"), MOTOR("motor");;

  private String value;

  InsuranceEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
