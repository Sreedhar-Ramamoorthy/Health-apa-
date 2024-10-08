package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CognitoSNSRequest {

  @NotBlank
  private String cognitoId;
  @NotBlank
  private String firebaseToken;
  private String endpointArn;

}
