package ke.co.apollo.health.common.domain.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BenefitCategoryMap {

  private Inpatient inpatient;
  private OptionalBenefits optionalBenefits;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Inpatient {

    private Map<Integer, String> person;
    private Map<Integer, String> family;
    private Map<Integer, String> childOnly;
  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class OptionalBenefits {

    private Outpatient outpatient;
    private Map<Integer, String> maternity;
    private Map<Integer, String> dental;
    private Map<Integer, String> optical;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Outpatient {
      private Map<Integer, String> person;
      private Map<Integer, String> childOnly;

    }

  }

}
