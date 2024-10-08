package ke.co.apollo.health.common.domain.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.NotNull;
import ke.co.apollo.health.common.domain.model.UnderwritingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBeneficiariesToPolicyRequest {

  @NotNull
  private Integer policyId;
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd")
  private String policyEffectiveDate;
  @NotNull
  private Integer entityId;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date originalJoinDate;
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date joinDate;
  @NotNull
  private UnderwritingType underwritingType;


}
