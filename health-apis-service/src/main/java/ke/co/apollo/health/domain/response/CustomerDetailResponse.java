package ke.co.apollo.health.domain.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

import ke.co.apollo.health.common.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailResponse {

  private String customerId;

  private String quoteId;

  private Principal principal;
  private Quote quoteDetails;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date startDate;

  private Dependant spouse;

  private Children children;

  private DependantBenefit benefit;

  private boolean onlyChild;

  private String updateDependent;

}
