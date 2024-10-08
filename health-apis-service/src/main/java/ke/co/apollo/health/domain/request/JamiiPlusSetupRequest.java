package ke.co.apollo.health.domain.request;

import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.domain.PolicyBeneficiary;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JamiiPlusSetupRequest {
    private PolicyBeneficiary policyBeneficiary;
    private Benefit benefit;
    private String product;
}
