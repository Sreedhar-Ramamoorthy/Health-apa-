package ke.co.apollo.health.common.domain.model;

import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BenefitBreakDownCategory {

  private List<BenefitBean> inpatient;
  private List<BenefitBean> outpatient;
  private List<BenefitBean> maternity;
  private List<BenefitBean> dental;
  private List<BenefitBean> optical;
  private List<BenefitBean> travel;
  private HashMap<String,String> categories;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BenefitBean {

    private int benefit;
    private String name;

  }

}
