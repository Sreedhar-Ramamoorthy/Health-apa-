package ke.co.apollo.health.domain;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyBenefitPremium {

  private String benefitType;
  private BigDecimal benefitLimit;
  private List<MemberPremium> list;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class MemberPremium {

    private String customerId;
    private Long entityId;
    private String name;
    private int age;
    private String relationship;
    private BigDecimal premium;
  }
}
