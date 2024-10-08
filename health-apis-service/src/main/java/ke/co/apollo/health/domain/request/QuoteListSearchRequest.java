package ke.co.apollo.health.domain.request;

import javax.validation.constraints.Max;

import ke.co.apollo.health.enums.QuoteStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteListSearchRequest {

  @Length(max = 50)
  private String agentId;

  @Length(max = 50)
  private String filter;

  @Length(max = 50)
  private String sortType;

  @Length(max = 50)
  private String sort;

  @Max(100)
  private int index;

  @Max(100)
  private int limit;

  private Boolean hide;

  private QuoteStatusEnum quoteStatus;

}
