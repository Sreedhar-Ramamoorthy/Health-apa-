package ke.co.apollo.health.domain.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadDeleteRequest {

  @NotBlank
  @Length(min = 1, max = 50)
  private String agentId;

  @NotBlank
  @Length(min = 1, max = 50)
  private String leadId;

}