package ke.co.apollo.health.common.domain.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CognitoSns {

  private String cognitoId;

  private String firebaseToken;

  private String endpointArn;

  private Date createTime;

  private Date updateTime;

}
