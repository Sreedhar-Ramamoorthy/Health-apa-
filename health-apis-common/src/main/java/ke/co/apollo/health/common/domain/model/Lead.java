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
public class Lead implements Serializable {

  private String leadId;
  private String firstName;
  private String lastName;
  private String email;
  private String mobile;
  private Date dob;
  private Interest interest;
  private Date createTime;
  private Date updateTime;
  private String product;

}
