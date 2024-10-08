package ke.co.apollo.health.domain.request;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import ke.co.apollo.health.annotation.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAddRequest {

  private String customerId;
  private String superCustomerId;
  private String agentId;
  @NotBlank
  @Length(min = 1, max = 30)
  private String firstName;
  @NotBlank
  @Length(min = 1, max = 30)
  private String lastName;
  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Past
  private Date dateOfBirth;
  @Length(max = 50)
  private String title;
  @Length(max = 50)
  private String gender;
  @PhoneNumber
  private String phoneNumber;
  @Length(max = 250)
  private String email;
  private Long entityId;

}
