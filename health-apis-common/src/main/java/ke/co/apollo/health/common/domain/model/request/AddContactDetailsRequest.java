package ke.co.apollo.health.common.domain.model.request;

import ke.co.apollo.health.common.domain.model.ContactDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddContactDetailsRequest {

  private Long entityId;
  private ContactDetails contactDetails;

}
