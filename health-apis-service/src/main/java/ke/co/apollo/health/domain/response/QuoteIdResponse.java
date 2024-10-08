package ke.co.apollo.health.domain.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteIdResponse implements Serializable {

  private String quoteId;

}
