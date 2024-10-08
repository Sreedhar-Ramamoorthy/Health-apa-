package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

  @NotBlank
  @Email
  @Length(min = 3, max = 200)
  private String emailAddress;
  @NotBlank
  @Length(min = 1, max = 1000)
  private String text;
  @NotBlank
  @Length(min = 1, max = 100)
  private String subject;
}
