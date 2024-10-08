package ke.co.apollo.health.common.domain.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntermediaryPolicyDetailsRequest {

    private Integer agentId;

    private String filter;

    private String sort;

    private String sortColumn;

    private Integer policyHolderId;

    private Date fromRenewalDate;

    private Date toRenewalDate;

    private boolean active;
}
