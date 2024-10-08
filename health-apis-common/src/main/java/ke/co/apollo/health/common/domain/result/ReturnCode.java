package ke.co.apollo.health.common.domain.result;

public enum ReturnCode {

  // Success
  OK(0, "OK"),

  // Invalid parameter
  INVALID_PARAMETER(1, "Invalid parameter"),

  // No Data
  NO_DATA(2, "No Data"),

  // Business Exception
  EXCEPTION(3, "Business Exception"),

  // Error
  ERROR(500, "Internal Server Runtime Exception");

  private final int value;
  private final String reasonPhrase;

  ReturnCode(int value, String reasonPhrase) {
    this.value = value;
    this.reasonPhrase = reasonPhrase;
  }

  public int getValue() {
    return value;
  }

  public String getReasonPhrase() {
    return reasonPhrase;
  }

  @Override
  public String toString() {
    return Integer.toString(this.value);
  }

}
