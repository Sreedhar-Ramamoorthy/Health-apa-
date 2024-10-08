package ke.co.apollo.health.common.enums;

public enum ProductEnum {
  JAMIIPLUS(49, "Jamii Plus"), AFYANAFUU(50, "AfyaNafuu"), FEMINA(51, "Femina"), OTHER(0, "Other"),
  JAMIIPLUS_SHARED(52, "Jamii Plus Shared");

  private int id;
  private String value;

  ProductEnum(int id, String value) {
    this.id = id;
    this.value = value;
  }

  public int getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  public static ProductEnum getById(int id) {
    for (ProductEnum productEnum : ProductEnum.class.getEnumConstants()) {
      if (id == productEnum.getId()) {
        return productEnum;
      }
    }
    return ProductEnum.OTHER;
  }

  public static ProductEnum getByValue(String value) {
    for (ProductEnum productEnum : ProductEnum.class.getEnumConstants()) {
      if (productEnum.getValue().equals(value)) {
        return productEnum;
      }
    }
    return ProductEnum.OTHER;
  }

}
