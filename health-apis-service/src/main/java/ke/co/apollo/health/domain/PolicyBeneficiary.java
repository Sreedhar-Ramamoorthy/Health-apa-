package ke.co.apollo.health.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyBeneficiary {

  private String id;
  private Beneficiary principal;
  private Beneficiary spouse;
  private List<Beneficiary> children;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Beneficiary {

    private String customerId;
    private Long entityId;
    private String name;
    private int age;
    private String gender;
    private String relationship;
    @Builder.Default
    private List<BenefitPremium> benefitPremiums = new ArrayList<>();

  }
}
