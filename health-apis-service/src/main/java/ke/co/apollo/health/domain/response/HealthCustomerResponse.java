package ke.co.apollo.health.domain.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import ke.co.apollo.health.common.domain.model.Children;
import ke.co.apollo.health.common.domain.model.Dependant;
import ke.co.apollo.health.common.domain.model.DependantBenefit;
import ke.co.apollo.health.common.domain.model.Principal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthCustomerResponse {

  private String customerId;

  private String quoteId;

  private Principal principal;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date startDate;

  private Dependant spouse;

  private Children children;

  private DependantBenefit benefit;

}
