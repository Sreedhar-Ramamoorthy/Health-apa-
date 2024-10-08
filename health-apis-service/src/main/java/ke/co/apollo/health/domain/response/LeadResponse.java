package ke.co.apollo.health.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.Date;
import ke.co.apollo.health.common.domain.model.Interest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadResponse implements Serializable {

  private String leadId;
  private String firstName;
  private String lastName;
  private String email;
  private String mobile;
  @JsonFormat(pattern = "dd/MM/yyyy")
  private Date dob;
  @JsonInclude(Include.NON_NULL)
  private Interest interest;
  private Date createTime;

}
