package ke.co.apollo.health.common.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Benefit {

  @Range(min = 0, max = 10000000)
  private Integer inpatientLimit;
  @Range(min = 0, max = 200000)
  private Integer outpatientLimit;
  @Range(min = 0, max = 30000)
  private Integer opticalLimit;
  @Range(min = 0, max = 30000)
  private Integer dentalLimit;
  @Range(min = 0, max = 150000)
  private Integer maternityLimit;
  @Range(min = 0, max = 40000)
  private Integer travelInsurance;
  @Range(min = 0, max = 500000)
  private Integer principal;
  @Range(min = 0, max = 500000)
  private Integer spouse;

  private List<Integer> children;
}
