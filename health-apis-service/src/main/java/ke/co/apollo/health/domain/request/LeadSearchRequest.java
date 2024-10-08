package ke.co.apollo.health.domain.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
public class LeadSearchRequest {

  @NotBlank
  @Length(min = 1, max = 50)
  private String agentId;

  @Length(max = 30)
  private String searchKey;

  @Length(max = 30)
  private String sort;

  @Length(max = 30)
  private String filter;

  @Min(1)
  @Max(100)
  private int index;

  @Min(1)
  @Max(100)
  private int limit;

}
