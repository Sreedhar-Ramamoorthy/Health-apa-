package ke.co.apollo.health.common.enums;

public enum NotificationType {

  SMS("Sms"), EMAIL("Email"), NOTIFICATION("Notification");

  private String value;

  NotificationType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
