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
public class ProductPremiumRequest {

  @NotBlank
  @Length(min = 1, max = 30)
  private String productName;

}
