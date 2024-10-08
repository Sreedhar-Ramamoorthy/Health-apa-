package ke.co.apollo.health.common.domain.model.remote;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyNumberRequest {

  @NotBlank
  private String policyNumber;

  @NotNull
  private Date effectiveDate;

}
