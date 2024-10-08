package ke.co.apollo.health.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActBusinessError {

  String description;
  String number;
  String type;
  String appName;
  String className;
  String methodName;
  String userName;

}
