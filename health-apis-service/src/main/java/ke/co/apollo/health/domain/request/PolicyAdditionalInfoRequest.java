package ke.co.apollo.health.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyAdditionalInfoRequest {
    @JsonProperty("PolicyId")
    public int policyId;
    @JsonProperty("PolicyEffectiveDate")
    public String policyEffectiveDate;
    @JsonProperty("PolicyAdditionalInfoList")
    public List<PolicyAdditionalInfoList> policyAdditionalInfoList;
}
