package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.Digits;
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
public class EntityPolicyListRequest {

  @NotBlank
  @Length(min = 1, max = 50)
  @Digits(integer = 50, fraction = 0)
  private String entityId;

}
