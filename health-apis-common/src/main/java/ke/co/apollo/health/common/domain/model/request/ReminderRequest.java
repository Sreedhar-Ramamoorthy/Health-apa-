package ke.co.apollo.health.common.domain.model.request;

import java.io.Serializable;
import ke.co.apollo.health.common.annotation.Enums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReminderRequest implements Serializable {

  private static final long serialVersionUID = 1060535688127796224L;

  private String customerId;

  private String quoteId;

  private String policyNumber;

  private String effectiveDate;

  @Enums(enumCode = {"health_quote", "health_renewal"})
  private String type;

}
