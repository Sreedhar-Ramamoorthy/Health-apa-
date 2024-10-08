package ke.co.apollo.health.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

  private Long addressId;
  private String territory;
  private String startDate;
  private String endDate;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String postCode;
  private String addressType;
  private Long latitude;
  private Long longitude;

}
