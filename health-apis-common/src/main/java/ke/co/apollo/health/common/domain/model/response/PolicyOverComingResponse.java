package ke.co.apollo.health.common.domain.model.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import ke.co.apollo.health.common.domain.model.PolicyOverComing;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyOverComingResponse {
    List<PolicyOverComing> policyOverComingList = new ArrayList<>();
    private Integer total;
}
