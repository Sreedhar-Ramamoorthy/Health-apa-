package ke.co.apollo.health.common.domain.model.response;

import java.util.Date;
import java.util.List;
import ke.co.apollo.health.common.domain.model.RoleAdditionalInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddClientEntityResponse extends ASAPIResponse {

  private Long entityId;
  private Long parentId;
  private String relationshipDescription;
  private Date relationshipEffectiveDate;
  private String title;
  private String firstName;
  private String surname;
  private String initials;
  private String gender;
  private Date dateOfBirth;
  private String occupation;
  private String nationality;
  private List<RoleAdditionalInfo> listRoleAdditionalInfo;
  private String customerId;

}
