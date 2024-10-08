package ke.co.apollo.health.common.domain.model.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse implements java.io.Serializable {

  private static final long serialVersionUID = 7092906238192790921L;

  private String firstname;
  private String surname;
  private String othernames;
  private Integer idTypeID;
  private Date dateOfBirth;
  private String photo;
  private String gender;
  private String serialNumber;
  private String citizenship;
  private String signature;

}
