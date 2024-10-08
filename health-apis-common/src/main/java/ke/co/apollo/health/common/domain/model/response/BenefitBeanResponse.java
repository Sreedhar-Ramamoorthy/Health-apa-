package ke.co.apollo.health.common.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BenefitBeanResponse {

  private String entityId;
  private BenefitBean benefit;

  @Builder
  @Data
  @AllArgsConstructor
  public static class BenefitBean {

    private BenefitLimit inpatientLimit;
    private BenefitLimit outpatientLimit;
    private BenefitLimit opticalLimit;
    private BenefitLimit dentalLimit;
    private BenefitLimit maternityLimit;
    private BenefitLimit travelInsurance;

    public BenefitBean() {
      this.inpatientLimit = new BenefitLimit();
      this.outpatientLimit = new BenefitLimit();
      this.opticalLimit = new BenefitLimit();
      this.dentalLimit = new BenefitLimit();
      this.maternityLimit = new BenefitLimit();// If travel exists will be init, otherwise display as null
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BenefitLimit {

      private int used;
      private int limit;

    }

  }


}
