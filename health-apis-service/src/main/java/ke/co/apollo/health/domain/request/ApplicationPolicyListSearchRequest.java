package ke.co.apollo.health.domain.request;

import javax.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationPolicyListSearchRequest {

  @Length(max = 50)
  private String filter;

  private boolean paid;

  private boolean archived;

  @Length(max = 50)
  private String sortType;

  @Length(max = 50)
  private String sort;

  @Max(100)
  private int index;

  @Max(100)
  private int limit;

}
