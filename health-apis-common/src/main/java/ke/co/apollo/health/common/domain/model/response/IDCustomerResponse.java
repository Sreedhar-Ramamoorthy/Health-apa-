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
public class IDCustomerResponse implements java.io.Serializable {

  private static final long serialVersionUID = 7092906238192790921L;

  private String idNumber;
  private String registrationOffice;
  private Date dateOfIssue;
  private String fingerPrint;
  private String photo;
  private String pin;
  private String serialNumber;
  private String signature;
  private String citizenship;
  private String clan;
  private Date dateOfBirth;
  private Date dateOfDeath;
  private String ethnicGroup;
  private String family;
  private String firstName;
  private Integer gender;
  private String occupation;
  private String otherNames;
  private String placeOfBirth;
  private String placeOfDeath;
  private String placeOfResidence;
  private String surname;

}
