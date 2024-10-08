package ke.co.apollo.health.domain.response;

import ke.co.apollo.health.domain.PolicyBeneficiary.*;
import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class JamiiPlusSetupResponse {
    private int totalBeneficiary;
    private String track;
    private Beneficiary principalBeneficiary;
    private Beneficiary spouseBeneficiary;
    private List<Beneficiary> childrenBeneficiary;
    private boolean hasPrincipal;
    private boolean hasSpouse;

    public boolean getHasPrincipal() {
        return hasPrincipal;
    }

    public boolean getHasSpouse() {
        return hasSpouse;
    }
}
