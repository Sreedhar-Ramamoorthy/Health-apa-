package ke.co.apollo.health.common.domain.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadSearchCondition implements Serializable {

  private String name;
  private String product;
  private String orderbyCause;

}
