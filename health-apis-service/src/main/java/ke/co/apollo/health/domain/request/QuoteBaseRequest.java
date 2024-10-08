package ke.co.apollo.health.domain.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteBaseRequest {

  @Length(max = 50)
  private String agentId;

  @NotBlank
  @Length(min = 1, max = 50)
  private String customerId;

  @NotBlank
  @Length(min = 1, max = 50)
  private String quoteId;

}
