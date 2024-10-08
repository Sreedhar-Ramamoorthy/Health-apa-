package ke.co.apollo.health.common.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BenefitCategory {

  private Inpatient inpatient;
  private OptionalBenefits optionalBenefits;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Inpatient {

    private List<Benifit> person;
    private List<Benifit> family;
    private List<Benifit> childOnly;
  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class OptionalBenefits {

    private Outpatient outpatient;
    private List<Benifit> maternity;
    private List<Benifit> dental;
    private List<Benifit> optical;

  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Outpatient {
    private List<Benifit> person;
    private List<Benifit> childOnly;
  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Benifit {

    private Integer benefit;
    private String name;
  }
}
