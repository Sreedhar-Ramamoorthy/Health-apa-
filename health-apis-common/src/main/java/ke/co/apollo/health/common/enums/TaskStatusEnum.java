package ke.co.apollo.health.common.enums;

public enum TaskStatusEnum {

  TODO("Todo"), PENDING("Pending"), DONE("Done"), CANCEL("Cancel");

  private String value;

  TaskStatusEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
