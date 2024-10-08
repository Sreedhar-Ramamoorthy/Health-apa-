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
public class Notification {

  private String id;

  private String superCustomerId;

  private String title;

  private String type;

  private String content;

  private String status;

  private Date createTime;

  private Date updateTime;


}
