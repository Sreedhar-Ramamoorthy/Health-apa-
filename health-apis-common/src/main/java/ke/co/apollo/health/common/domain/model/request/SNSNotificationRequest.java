package ke.co.apollo.health.common.domain.model.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SNSNotificationRequest {

  @NotBlank
  private String cognitoId;
  @NotBlank
  private String message;
  private String firebaseToken;

}
