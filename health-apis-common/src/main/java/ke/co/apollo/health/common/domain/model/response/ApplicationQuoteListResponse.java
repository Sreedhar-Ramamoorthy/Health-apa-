package ke.co.apollo.health.common.domain.model.response;

import java.util.List;
import ke.co.apollo.health.common.domain.model.ApplicationQuote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationQuoteListResponse {

  private List<ApplicationQuote> list;
  private int total;

}
