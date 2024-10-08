package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteIdRequest {

  @NotEmpty
  @Length(min = 1, max = 50)
  private String quoteId;

}
