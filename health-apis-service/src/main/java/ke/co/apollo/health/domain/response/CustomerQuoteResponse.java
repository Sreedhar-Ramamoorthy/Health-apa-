package ke.co.apollo.health.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import ke.co.apollo.health.common.domain.model.Premium;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerQuoteResponse {

  String quoteId;
  String quoteNumber;
  String productId;
  @JsonFormat(pattern = "yyyy-MM-dd")
  Date startDate;
  Premium premium;
  boolean onlyChild;

}
