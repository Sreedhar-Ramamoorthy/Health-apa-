package ke.co.apollo.health.common.domain.model.response;

import java.util.Date;
import ke.co.apollo.health.common.domain.model.UnderwritingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBeneficiariesToPolicyResponse {

  private Integer policyId;

  private String policyEffectiveDate;

  private Integer entityId;

  private Date originalJoinDate;

  private Date joinDate;

  private UnderwritingType underwritingType;

  private Boolean success;

}
