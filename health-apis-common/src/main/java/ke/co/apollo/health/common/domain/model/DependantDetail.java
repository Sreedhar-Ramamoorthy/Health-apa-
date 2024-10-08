package ke.co.apollo.health.common.domain.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependantDetail {

  Long policyId;
  Date effectiveDate;
  String title;
  String firstName;
  String lastName;
  Long entityId;
  Date dateOfBirth;
  String gender;
  String relationship;

}
