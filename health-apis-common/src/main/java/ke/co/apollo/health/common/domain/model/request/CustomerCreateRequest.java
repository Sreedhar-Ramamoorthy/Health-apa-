package ke.co.apollo.health.common.domain.model.request;


import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import ke.co.apollo.health.common.annotation.Enums;
import ke.co.apollo.health.common.domain.model.Children;
import ke.co.apollo.health.common.domain.model.Dependant;
import ke.co.apollo.health.common.domain.model.DependantBenefit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreateRequest {

  private String customerId;
  @NotBlank
  private String agentId;
  private String quoteId;
  private String product;
  private int productId = 49;
  @NotNull(message = "Date Of Birth is mandatory")
  private Date dateOfBirth;
  @NotBlank
  @Length(min = 1, max = 30)
  private String firstName;
  @NotBlank
  @Length(min = 1, max = 30)
  private String lastName;
  @NotBlank
  @Length(min = 1, max = 30)
  private String title;
  @NotBlank
  @Length(min = 1, max = 30)
  private String gender;
  private Dependant spouse;
  private Children children;
  private DependantBenefit benefit;
  private boolean onlyChild;

}
