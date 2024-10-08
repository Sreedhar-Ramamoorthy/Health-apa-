package ke.co.apollo.health.common.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Principal {

  private Long entityId; // Actisure client entity id

  private String customerId;

  private String firstName;

  private String lastName;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date dateOfBirth;

  private String title;

  private String gender;

  private String phoneNumber;

  private String email;

  private String quoteId;

  private String idNo;

  private String kraPin;

}
