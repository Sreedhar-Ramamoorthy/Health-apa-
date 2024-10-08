package ke.co.apollo.health.common.enums;

public enum PolicyStatus {
  NEW("New"), ENQUIRY("Enquiry"), APPLICATION("Application"), UNDERWRITING("Underwriting"), LIVE(
      "L"), RENEWAL("RL"), HISTORY("H"), LAPSE("LA"), CANCEL("C"), VIEWED("Viewed");

  private String value;

  PolicyStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
