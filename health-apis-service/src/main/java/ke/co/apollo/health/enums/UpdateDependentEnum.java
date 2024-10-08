package ke.co.apollo.health.enums;

public enum UpdateDependentEnum {

    SPOUSE_REMOVED("SPOUSE REMOVED"),
    SPOUSE_ADDED("SPOUSE ADDED"),
    CHILD_REMOVED("CHILD REMOVED"),
    CHILDREN_AMENDED("NUMBER OF KIDS HAS CHANGED"),
    SPOUSE_REMAINS("SPOUSE NOT REMOVED"),
    CHILD_REMAINS("CHILD NOT REMOVED"),
    CHILD_COUNT("CHILD COUNT REMAINS");

    private final String value;

    UpdateDependentEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
