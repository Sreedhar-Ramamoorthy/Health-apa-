package ke.co.apollo.health.policy.model;

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
public class AgentCommission implements Serializable {

  Long journalId;
  String description;
  Integer eventType;
  Long linkRef;
  String agentName;
  Long entityId;

  String policyNumber;
  Long policyId;
  Date policyEffectiveDate;
  Date postingDate;
  Long paymentId;
  String transferNumber;

  Date paymentDate;
  Float credit;
  Float debit;
  Float netCommission;


}
