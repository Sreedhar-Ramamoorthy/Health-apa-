package ke.co.apollo.health.domain.request;

import java.util.Date;
import javax.validation.constraints.NotNull;

import ke.co.apollo.health.common.annotation.Enums;
import ke.co.apollo.health.common.domain.model.Children;
import ke.co.apollo.health.common.domain.model.Dependant;
import ke.co.apollo.health.common.domain.model.DependantBenefit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependantAddRequest {

  @NotNull
  private String customerId;

  private String quoteId;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date startDate;

  private Dependant spouse;

  private Children children;

  private DependantBenefit benefit;

  private boolean onlyChild;

  private String agentId;

  private int productId = 49;

}
