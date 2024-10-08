package ke.co.apollo.health.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.Premium;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteListResponse {

  String agentId;
  String quoteId;
  String quoteNumber;
  String productId;
  @JsonFormat(pattern = "yyyy-MM-dd")
  Date startDate;
  Benefit benefit;
  String status;
  Premium premium;
  BigDecimal balance;
  boolean onlyChild;
  String quoteStatus;
}
