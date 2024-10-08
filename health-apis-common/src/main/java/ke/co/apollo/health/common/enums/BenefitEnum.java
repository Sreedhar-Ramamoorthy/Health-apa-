package ke.co.apollo.health.common.enums;

public enum BenefitEnum {

  INPATIENT("Inpatient"), OPTIONALBENEFITS("Optional Benefits"), OUTPATIENT("Outpatient"), OPTICAL(
      "Optical"), DENTAL("Dental"), MATERNITY("Maternity"), TRAVEL("Travel"), OTHER("Others");;

  private String value;

  BenefitEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static BenefitEnum getByValue(String value) {
    for (BenefitEnum benefitEnum : BenefitEnum.class.getEnumConstants()) {
      if (benefitEnum.getValue().equals(value)) {
        return benefitEnum;
      }
    }
    return BenefitEnum.OTHER;
  }

}
