package ke.co.apollo.health.common.domain.model.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntermediaryCommissionResponse {
       private Long countPaid;
       private BigDecimal totalPaid;
       private Long countDueProcessed;
       private BigDecimal totalDueProcessed;
       private Long countDueNotProcessed;
       private BigDecimal totalDueNotProcessed;
}
