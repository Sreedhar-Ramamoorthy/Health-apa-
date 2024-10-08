package ke.co.apollo.health.common.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dependant {

  String dependantCode; //Represent Health Customer_id
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd")
  Date dateOfBirth;
  String firstName;
  String lastName;
  String relationship;
  String title;
  String gender;
  Long entityId;

}
