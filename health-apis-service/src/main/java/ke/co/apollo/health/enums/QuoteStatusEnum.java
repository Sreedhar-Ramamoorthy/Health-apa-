package ke.co.apollo.health.enums;

public enum QuoteStatusEnum {
    REJECTED("REJECTED"),DELETED("DELETED"), ACTIVE("ACTIVE");
    private final String value;

    QuoteStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
