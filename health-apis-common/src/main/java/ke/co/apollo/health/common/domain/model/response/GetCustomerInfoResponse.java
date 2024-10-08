package ke.co.apollo.health.common.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerInfoResponse {


  private String customerId;
  private String firstName;
  private String lastName;
  private String title;
  private String dateOfBirth;
  private String gender;
  private String agentId;
  private String type;

}
