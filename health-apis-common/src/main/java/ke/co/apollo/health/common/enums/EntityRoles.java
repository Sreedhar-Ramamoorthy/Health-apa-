package ke.co.apollo.health.common.enums;

public enum EntityRoles {

  CLIETN("Client");

  private String value;

  EntityRoles(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
