package ke.co.apollo.health.domain.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import ke.co.apollo.health.common.domain.model.Interest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadUpdateRequest {

  @NotBlank
  @Length(min = 1, max = 50)
  private String agentId;

  @NotBlank
  @Length(min = 1, max = 50)
  private String leadId;

  @NotBlank
  @Length(min = 1, max = 30)
  private String firstName;

  @NotBlank
  @Length(min = 1, max = 30)
  private String lastName;

  @NotBlank
  @Email
  @Length(min = 1, max = 50)
  private String email;

  @NotBlank
  @Length(min = 1, max = 50)
  private String mobile;

  @NotNull
  @JsonFormat(pattern = "dd/MM/yyyy")
  @Past
  private Date dob;

  @Valid
  private Interest interest;

}
