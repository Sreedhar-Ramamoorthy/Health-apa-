package ke.co.apollo.health.common.domain.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Intermediary {

  private String agentId;

  private String parentAgentId;

  private Integer entityId;

  private String firstName;

  private String lastName;

  private String phoneNumber;

  private boolean limitedCompany;

  private String status;

  private boolean deleted;

  private String role;

  private String email;

  private String poBox;

  private String postalCode;

  private String officeContactNumber;

  private String bankId; // bank entityId

  private String bankName;

  private String bankAccountNumber;

  private String branchId;

  private String branchName;

  private String agentCode;

  private String organization;

  private boolean enabled; // enabled disabled

  private Date createTime;

  private Date updateTime;

  private Date lastLoginTime;

}
