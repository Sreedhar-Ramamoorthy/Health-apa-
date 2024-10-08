package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerByPolicyNoRequest {

  @NotBlank
  @Length(max = 50)
  private String policyNumber;

}
