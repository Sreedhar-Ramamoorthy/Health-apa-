package ke.co.apollo.health.common.domain.model;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerNotification implements Serializable {

  private Integer id;
  private String entityId;
  private String type;
  private String message;
  private String status;
  private Date createTime;

}
