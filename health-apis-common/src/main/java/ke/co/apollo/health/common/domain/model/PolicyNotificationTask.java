package ke.co.apollo.health.common.domain.model;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyNotificationTask implements java.io.Serializable {

  private static final long serialVersionUID = 1060535688127796225L;

  @Length(max = 50)
  private String taskId;
  @Length(min = 1, max = 50)
  private String subtype;
  @NotBlank
  @Length(min = 1, max = 50)
  private String type;
  @NotBlank
  @Length(min = 1, max = 50)
  private String destination;
  @Length(min = 1, max = 50)
  private String subject;
  @NotBlank
  @Length(min = 1, max = 500)
  private String text;
  @NotBlank
  @Length(min = 1, max = 50)
  private String status;
  private String policyNumber;
  private String category;
  private String messageId;
  private int failureNumber;
  @NotNull
  private Date scheduleTime;
  @NotNull
  private Date createTime;
  private Date updateTime;
  private String createBy;
  private String updateBy;

}
