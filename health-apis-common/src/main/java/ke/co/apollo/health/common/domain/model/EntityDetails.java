package ke.co.apollo.health.common.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityDetails {

  private Long entityId;
  private int entityType;
  private String title;
  private String firstName;
  private String surname;
  private String initials;
  private String companyName;
  private boolean isActiveEntity;
  private List<Roles> roles;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Roles {

    private int roleId;
    private String startDate;
    private String endDate;
    private String roleDescription;
    private List<RoleAdditionalInformation> roleAdditionalInformation;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleAdditionalInformation {

      private int infoId;
      private String infoValue;
      private String infoDescription;
      private boolean isRequired;

    }
  }
}
