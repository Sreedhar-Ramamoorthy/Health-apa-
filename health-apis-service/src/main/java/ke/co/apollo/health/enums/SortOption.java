package ke.co.apollo.health.enums;

public enum SortOption {
  NAME("name"), TIME("time");

  private String value;

  SortOption(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
