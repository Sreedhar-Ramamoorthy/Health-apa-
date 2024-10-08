package ke.co.apollo.health.domain.request;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailTaskAddRequest {

  @NotBlank
  private String checkoutRequestId;
  @NotNull
  private Date scheduleTime;

}
