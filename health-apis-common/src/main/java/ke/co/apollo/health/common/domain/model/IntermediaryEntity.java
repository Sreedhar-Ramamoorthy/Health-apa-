package ke.co.apollo.health.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntermediaryEntity {

  private Integer entityId;
  private String entityTypeDesc;
  private String roleDesc;
  private Integer roleId;
  private String firstName;
  private String surname;
  private String companyName;

}
