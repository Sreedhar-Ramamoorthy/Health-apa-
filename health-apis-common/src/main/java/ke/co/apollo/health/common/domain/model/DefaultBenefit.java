package ke.co.apollo.health.common.domain.model;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class DefaultBenefit {

  @Builder.Default
  private JamiiPlusBenefit jamiiPlusBenefit = JamiiPlusBenefit.builder().build();
  @Builder.Default
  private JamiiPlusSharedBenefit jamiiPlusSharedBenefit = JamiiPlusSharedBenefit.builder().build();
  @Builder.Default
  private AfyaNafuuBenefit afyaNafuuBenefit = AfyaNafuuBenefit.builder().build();
  @Builder.Default
  private FeminaBenefit feminaBenefit = FeminaBenefit.builder().build();

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JamiiPlusBenefit {

    @Builder.Default
    private Integer inpatientLimit = 5000000;
    @Builder.Default
    private Integer outpatientLimit = 75000;
    @Builder.Default
    private Integer opticalLimit = 20000;
    @Builder.Default
    private Integer dentalLimit = 20000;
    @Builder.Default
    private Integer maternityLimit = 100000;
    @Builder.Default
    private Integer travelInsurance = 40000;
  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JamiiPlusSharedBenefit {
    @Builder.Default
    private Integer inpatientLimit = 5000000;
    @Builder.Default
    private Integer outpatientLimit = 75000;
    @Builder.Default
    private Integer opticalLimit = 20000;
    @Builder.Default
    private Integer dentalLimit = 20000;
    @Builder.Default
    private Integer maternityLimit = 100000;
    @Builder.Default
    private Integer travelInsurance = 40000;
  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class AfyaNafuuBenefit {

    @Builder.Default
    private Integer inpatientLimit = 300000;
    @Builder.Default
    private Integer outpatientLimit = 30000;
    @Builder.Default
    private Integer maternityLimit = 50000;
  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FeminaBenefit {

    @Builder.Default
    private Integer principal = 250000;
    @Builder.Default
    private Integer spouse = 250000;
    @Builder.Default
    private Integer children = 250000;
  }
}
