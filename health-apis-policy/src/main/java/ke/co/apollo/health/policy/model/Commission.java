package ke.co.apollo.health.policy.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Commission implements Serializable {

  private Long countPaid;
  private Float totalPaid;
  private Long countDueProcessed;
  private Float totalDueProcessed;
  private Long countDueNotProcessed;
  private Float totalDueNotProcessed;
}
