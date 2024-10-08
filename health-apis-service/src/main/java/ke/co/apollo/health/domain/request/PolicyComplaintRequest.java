package ke.co.apollo.health.domain.request;

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
public class PolicyComplaintRequest {

  @NotBlank
  @Length(min = 1, max = 50)
  private String policyNumber;

  @NotNull
  private Date effectiveDate;

  @Length(max = 50)
  private String agentId;

  @Length(max = 50)
  private String customerId;

  @NotBlank
  @Length(min = 1, max = 100)
  private String title;

  @NotBlank
  @Length(min = 1, max = 1000)
  private String content;

}
