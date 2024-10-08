package ke.co.apollo.health.common.domain.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.map.MultiKeyMap;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPremiumMap {

  private JamiiPlusMap jamiiPlusMap;
  private AfyaNafuuMap afyaNafuuMap;
  private FeminaMap feminaMap;
  private JamiiPlusSharedMap jamiiPlusSharedMap;
  private JamiiPlusChildOnlyCoverMap childOnlyCoverMap;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JamiiPlusChildOnlyCoverMap {

    private Map<Integer, Integer> inpatient;;
    private Map<Integer, Integer> outpatient;
  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JamiiPlusSharedMap {

    private InpatientMap inpatient;
    private MultiKeyMap<Integer, Integer> outpatient;
    private Map<Integer, Integer> maternity;
    private Map<Integer, Integer> dental;
    private Map<Integer, Integer> optical;
    private Map<Integer, Integer> travel;
    private List<Integer> inpatientAge;
    private List<Integer> outpatientAge;

  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JamiiPlusMap {

    private InpatientMap inpatient;
    private MultiKeyMap<Integer, Integer> outpatient;
    private Map<Integer, Integer> maternity;
    private Map<Integer, Integer> dental;
    private Map<Integer, Integer> optical;
    private Map<Integer, Integer> travel;
    private List<Integer> inpatientAge;
    private List<Integer> outpatientAge;

  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class InpatientMap {

    private MultiKeyMap<Integer, Integer> principal;
    private MultiKeyMap<Integer, Integer> spouse;
    private Map<Integer, Integer> child;
  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class AfyaNafuuMap {

    private MultiKeyMap<Integer, Integer> inpatient;
    private MultiKeyMap<Integer, Integer> outpatient;
    private Map<Integer, Integer> maternity;
    private List<Integer> inpatientAge;
    private List<Integer> outpatientAge;
  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FeminaMap {

    private MultiKeyMap<Integer, Integer> femina;
    private List<Integer> age;
  }
}


