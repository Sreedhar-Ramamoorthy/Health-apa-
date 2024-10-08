package ke.co.apollo.health.common.domain.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PolicyAdditionalInfoRequest {
    @JsonProperty("policyId")
    public int policyId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("policyEffectiveDate")
    public String policyEffectiveDate;
    @JsonProperty("policyAdditionalInfoList")
    public List<PolicyAdditionalInfoList> policyAdditionalInfoList;
}



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class PolicyAdditionalInfoList {
    public String key;
    public String value;
}
