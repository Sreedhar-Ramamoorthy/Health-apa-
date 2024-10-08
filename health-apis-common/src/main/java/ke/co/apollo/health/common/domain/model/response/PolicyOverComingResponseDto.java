package ke.co.apollo.health.common.domain.model.response;

import ke.co.apollo.health.common.domain.model.PolicyOverComing;
import ke.co.apollo.health.common.domain.model.PolicyOverComingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyOverComingResponseDto {
    List<PolicyOverComingDto> policyOverComingListDto = new ArrayList<>();
    private Integer total;
}
