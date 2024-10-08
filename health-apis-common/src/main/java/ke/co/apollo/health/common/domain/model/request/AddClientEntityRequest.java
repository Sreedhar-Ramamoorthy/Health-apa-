package ke.co.apollo.health.common.domain.model.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import ke.co.apollo.health.common.domain.model.RoleAdditionalInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddClientEntityRequest {

  private Long parentId;
  private String relationshipDescription;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date relationshipEffectiveDate;
  private String title;
  @NotBlank(message = "First Name is mandatory")
  private String firstName;
  @NotBlank(message = "Surname is mandatory")
  private String surname;
  private String initials;
  @NotBlank(message = "Gender is mandatory")
  private String gender;
  @NotNull(message = "Date Of Birth is mandatory")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date dateOfBirth;
  private String occupation;
  @NotNull(message = "Nationality is mandatory")
  private String nationality;
  private List<RoleAdditionalInfo> listRoleAdditionalInfo;
  private String customerId;

}
