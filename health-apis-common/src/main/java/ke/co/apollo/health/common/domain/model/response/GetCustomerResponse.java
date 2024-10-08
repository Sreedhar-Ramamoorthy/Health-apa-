package ke.co.apollo.health.common.domain.model.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCustomerResponse {

  private Long entityId;

  private String firstName;

  private String lastName;

  private Date dateOfBirth;

  private String title;

  private String gender;

  private String phoneNumber;

}
