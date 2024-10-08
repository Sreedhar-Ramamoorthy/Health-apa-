package ke.co.apollo.health.domain.response;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadListResponse implements Serializable {

  private Integer total;
  private List<LeadResponse> leads;

}
