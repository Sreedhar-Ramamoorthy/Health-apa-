package ke.co.apollo.health.common.domain.model;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPolicyCache {

  private String entityId;
  private List<Policy> policyList;
  private Date checkTime;
  private Date createTime;
  private Date updateTime;

}
