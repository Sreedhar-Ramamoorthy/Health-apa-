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
public class MarketingPreference {

  String agentId;
  String entityId;
  String product;
  boolean sms;
  boolean phone;
  boolean email;
  boolean mail;
  Date createTime;
  Date updateTime;
}
