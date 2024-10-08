package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import ke.co.apollo.health.common.annotation.Enums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPolicyListRequest {

  @NotBlank
  @Length(min = 1, max = 50)
  private String customerId;

  private String entityId;

  @Default
  @Min(1)
  @Max(100)
  private Integer index = 1;

  @Default
  @Min(1)
  @Max(100)
  private Integer limit = 10;

  @Length(max = 50)
  private String filter;

  @Default
  @Length(max = 50)
  @Enums(enumCode = {"All", "30", "60", "90"})
  private String range = "All";

  @Length(max = 50)
  @Enums(enumCode = {"asc", "desc"})
  private String sort;

  @Length(max = 50)
  @Enums(enumCode = {"renewalDate", "policyNumber"})
  private String sortType;
}
