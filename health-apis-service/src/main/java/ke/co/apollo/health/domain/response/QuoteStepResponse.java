package ke.co.apollo.health.domain.response;

import ke.co.apollo.health.enums.HealthQuoteStepsEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteStepResponse {
    private String quoteOrPolicy;
    private HealthQuoteStepsEnum step;

    private String customerId;
    private String agentId;
}
