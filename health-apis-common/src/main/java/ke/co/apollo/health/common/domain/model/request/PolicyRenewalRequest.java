package ke.co.apollo.health.common.domain.model.request;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyRenewalRequest {

  @NotNull
  private Date effectiveDate;

  @NotBlank
  @Length(min = 1, max = 50)
  private String policyNumber;

}
