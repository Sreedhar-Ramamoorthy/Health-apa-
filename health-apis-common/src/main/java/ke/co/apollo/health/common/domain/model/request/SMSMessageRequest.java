package ke.co.apollo.health.common.domain.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMSMessageRequest {

  @Length(min = 1, max = 100)
  private String from;

  @Length(min = 1, max = 100)
  private String text;

  @Length(min = 1, max = 100)
  private String to;

  @Length(min = 1, max = 100)
  private String serviceType;

}
