package ke.co.apollo.health.common.enums;


public enum DependantRelationship {

  POLICY_HOLDER("Policy Holder"), SPOUSE("Spouse"), PARTNER("Partner"), MARRIED_CHILD(
      "Married Child"), UNMARRIED_CHILD("Unmarried Child"), OTHERS("Others");

  private String value;

  DependantRelationship(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static DependantRelationship getRelationship(String value) {
    for (DependantRelationship relationship : DependantRelationship.values()) {
      if (relationship.getValue().equals(value)) {
        return relationship;
      }
    }
    return OTHERS;
  }

}
