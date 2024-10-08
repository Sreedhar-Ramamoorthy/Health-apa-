package ke.co.apollo.health.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Phone {

  private Long phoneId;
  private String phoneNumber;
  private String countryDialCode;
  private String regionDialCode;
  private String phoneType;

}
