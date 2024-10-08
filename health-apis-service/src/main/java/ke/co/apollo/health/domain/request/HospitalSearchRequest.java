package ke.co.apollo.health.domain.request;

import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class HospitalSearchRequest {

  private Integer locationId;

  @Size(max = 100)
  private List<Integer> serviceId;

  @Size(max = 100)
  private List<Integer> coPaymentId;

  @Min(1)
  @Max(100)
  private Integer index = 1;

  @Min(1)
  @Max(100)
  private Integer limit = 10;

}
